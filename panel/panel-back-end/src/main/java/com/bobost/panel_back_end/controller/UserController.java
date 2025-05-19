package com.bobost.panel_back_end.controller;

import com.bobost.panel_back_end.dto.user.OTPCodeDto;
import com.bobost.panel_back_end.dto.user.ResetPasswordDto;
import com.bobost.panel_back_end.service.TotpService;
import com.bobost.panel_back_end.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final TotpService totpService;
    private final UserService userService;

    public UserController(TotpService totpService, UserService userService) {
        this.totpService = totpService;
        this.userService = userService;
    }

    @GetMapping("/info")
    public Map<String,Object> getInfo(Authentication auth) {
        auth.getCredentials();
        return Map.of(
                "username", auth.getName(),
                "permissions", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
    }

    @PostMapping("/setup-2fa")
    public ResponseEntity<?> setup2fa(Principal principal) {
        final String QRCodeURI = totpService.setupTotpAndReturnURI(principal.getName());
        return ResponseEntity.ok().body(Map.of("qrCodeURI", QRCodeURI));
    }

    @PostMapping("/verify-2fa")
    public void verify2fa(@RequestBody OTPCodeDto requestBody, Principal principal) {
        totpService.verifyTotp(principal.getName(), requestBody.code);
    }

    @PostMapping("/disable-2fa")
    public void disable2fa(@RequestBody OTPCodeDto requestBody, Principal principal) {
        totpService.disableTotp(principal.getName(), requestBody.code);
    }

    @GetMapping("/has-password-expired")
    public boolean hasPasswordExpired(Principal principal) {
        return userService.hasPasswordExpired(principal.getName());
    }

    @PostMapping("/update-password")
    public void updatePassword(@RequestBody ResetPasswordDto requestBody, Principal principal) {
        userService.updatePassword(principal.getName(), requestBody.oldPassword, requestBody.newPassword);
    }
}
