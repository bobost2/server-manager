package com.bobost.panel_back_end.controller;

import com.bobost.panel_back_end.dto.user.*;
import com.bobost.panel_back_end.service.TotpService;
import com.bobost.panel_back_end.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final TotpService totpService;
    private final UserService userService;
    private final FindByIndexNameSessionRepository<? extends Session> sessions;

    public UserController(TotpService totpService, UserService userService,
                          FindByIndexNameSessionRepository<? extends Session> sessions) {
        this.totpService = totpService;
        this.userService = userService;
        this.sessions = sessions;
    }

    @GetMapping("/info")
    public UserInfoDTO getInfo(Principal principal) {
        return userService.getUserInfo(Long.parseLong(principal.getName()));
    }

    @PostMapping("/setup-2fa")
    public ResponseEntity<?> setup2fa(Principal principal) {
        final String QRCodeURI = totpService.setupTotpAndReturnURI(Long.parseLong(principal.getName()));
        return ResponseEntity.ok().body(Map.of("qrCodeURI", QRCodeURI));
    }

    @PostMapping("/verify-2fa")
    public void verify2fa(@RequestBody OTPCodeDto requestBody, Principal principal) {
        totpService.verifyTotp(Long.parseLong(principal.getName()), requestBody.code);
    }

    @PostMapping("/disable-2fa")
    public void disable2fa(@RequestBody OTPCodeDto requestBody, Principal principal) {
        totpService.disableTotp(Long.parseLong(principal.getName()), requestBody.code);
    }

    @GetMapping("/has-password-expired")
    public boolean hasPasswordExpired(Principal principal) {
        return userService.hasPasswordExpired(Long.parseLong(principal.getName()));
    }

    @GetMapping("/has-2fa")
    public boolean has2fa(Principal principal) {
        return totpService.isTotpEnabled(Long.parseLong(principal.getName()));
    }

    @PatchMapping("/update-password")
    public void updatePassword(@RequestBody ResetPasswordDto requestBody, Principal principal) {
        userService.updatePassword(Long.parseLong(principal.getName()), requestBody.oldPassword, requestBody.newPassword);
    }

    @PatchMapping("/update-username")
    public void updateUsername(@RequestBody ChangeUsernameDto requestBody,
                               @AuthenticationPrincipal UserDetails userDetails) {
        userService.updateUsername(Long.parseLong(userDetails.getUsername()), requestBody.newUsername);
    }

    @GetMapping("/sessions")
    public List<SessionsDto> getUserSessions(Principal principal,
                                             HttpServletRequest request) {
        List<SessionsDto> sessionsList = new ArrayList<>();

        Map<String, ? extends Session> userSessions =
                sessions.findByIndexNameAndIndexValue(
                        FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME,
                        principal.getName()
                );

        String currentId = request.getSession().getId();
        for (var entry : userSessions.entrySet()) {
            String sessionId = entry.getKey();
            Session session = entry.getValue();

            SessionsDto sessionDto = new SessionsDto();
            sessionDto.setCurrentSession(sessionId.equals(currentId));
            sessionDto.setSessionId(sessionId.substring(0, 4) + "...");
            sessionDto.setLastRequest(Date.from(session.getLastAccessedTime()));

            sessionsList.add(sessionDto);
        }
        return sessionsList;
    }

    @DeleteMapping("/sessions")
    public void deleteUserSessions(Principal principal,
                                   HttpServletRequest request) {
        Map<String, ? extends Session> userSessions =
                sessions.findByIndexNameAndIndexValue(
                        FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME,
                        principal.getName()
                );

        String currentId = request.getSession().getId();

        for (String sessionId : userSessions.keySet()) {
            if (!sessionId.equals(currentId)) {
                sessions.deleteById(sessionId);
            }
        }
    }
}
