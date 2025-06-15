package com.bobost.panel_back_end.controller;

import com.bobost.panel_back_end.dto.user.*;
import com.bobost.panel_back_end.service.TotpService;
import com.bobost.panel_back_end.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final TotpService totpService;
    private final UserService userService;
    private final SessionRegistry sessionRegistry;

    public UserController(TotpService totpService, UserService userService, SessionRegistry sessionRegistry) {
        this.totpService = totpService;
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
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


    // TODO: Find a method to restore the session after application restart
    @GetMapping("/sessions")
    public List<SessionsDto> getUserSessions(@AuthenticationPrincipal UserDetails userDetails,
                                             HttpServletRequest request) {
        List<SessionsDto> sessionsDtoList = new ArrayList<>();

        var sessions = sessionRegistry.getAllSessions(userDetails, false);
        for (SessionInformation session : sessions) {
            SessionsDto sessionDto = new SessionsDto();
            sessionDto.setCurrentSession(session.getSessionId().equals(request.getSession().getId()));
            String obfuscatedSessionId =
                    session.getSessionId().substring(0, 4) + "...";
            sessionDto.setSessionId(obfuscatedSessionId);
            sessionDto.setLastRequest(session.getLastRequest());

            sessionsDtoList.add(sessionDto);
        }

        return sessionsDtoList;
    }

    @DeleteMapping("/sessions")
    public void deleteUserSessions(@AuthenticationPrincipal UserDetails userDetails,
                                   HttpServletRequest request) {
        List<SessionInformation> sessions =
                sessionRegistry.getAllSessions(userDetails, false);

        String currentSessionId = request.getSession().getId();
        for (SessionInformation session : sessions) {
            if (!session.getSessionId().equals(currentSessionId)) {
                session.expireNow();
            }
        }
    }
}
