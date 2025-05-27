package com.bobost.panel_back_end.service.impl;

import com.bobost.panel_back_end.data.entity.User;
import com.bobost.panel_back_end.data.repository.UserRepository;
import com.bobost.panel_back_end.exception.user.InvalidOTPCodeException;
import com.bobost.panel_back_end.exception.user.UserDoesNotHaveTOTPException;
import com.bobost.panel_back_end.exception.user.UserHasAlreadyTOTPException;
import com.bobost.panel_back_end.exception.user.UserNotFoundException;
import com.bobost.panel_back_end.service.TotpService;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.jboss.aerogear.security.otp.api.Clock;
import org.springframework.stereotype.Service;

@Service
public class TotpServiceImpl implements TotpService {
    private final UserRepository userRepository;

    public TotpServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String generateSecret() {
        return Base32.random();
    }

    @Override
    public String getOtpURI(String secret, String username) {
        final String issuer = "Server Manager Panel";
        return "otpauth://totp/" + username + "?secret=" + secret + "&issuer=" + issuer
                + "&algorithm=SHA1"
                + "&digits=6"
                + "&period=30";
    }

    @Override
    public boolean verifyCode(String secret, String code) {
        // We check the past and future to allow for clock drift
        Totp pastTotp   = new Totp(secret, new OffsetClock(-1));
        Totp nowTotp    = new Totp(secret);
        Totp futureTotp = new Totp(secret, new OffsetClock(+1));

        return pastTotp.verify(code)
                || nowTotp.verify(code)
                || futureTotp.verify(code);
    }

    // Helper class for the TOTP Clock
    private static class OffsetClock extends Clock {
        private final long offset;

        OffsetClock(long offset) {
            super();
            this.offset = offset;
        }

        @Override
        public long getCurrentInterval() {
            return super.getCurrentInterval() + offset;
        }
    }

    @Override
    public String setupTotpAndReturnURI(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found - " + username)
        );

        if (user.isTotpEnabled()) {
            throw new UserHasAlreadyTOTPException("User " + username + " already has TOTP enabled");
        }

        String secret = this.generateSecret();
        user.setTotpSecret(secret);
        userRepository.save(user);

        return this.getOtpURI(secret, username);
    }

    @Override
    public void verifyTotp(String username, String code) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found - " + username)
        );

        if (user.isTotpEnabled()) {
            throw new UserHasAlreadyTOTPException("User " + username + " already has TOTP enabled");
        }

        if (this.verifyCode(user.getTotpSecret(), code)) {
            user.setTotpEnabled(true);
            userRepository.save(user);
        } else {
            throw new InvalidOTPCodeException("Invalid OTP code");
        }
    }

    @Override
    public void disableTotp(String username, String code) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found - " + username)
        );

        if (!user.isTotpEnabled()) {
            throw new UserDoesNotHaveTOTPException("User " + username + " does not have TOTP enabled");
        }

        if (this.verifyCode(user.getTotpSecret(), code)) {
            user.setTotpEnabled(false);
            user.setTotpSecret(null);
            userRepository.save(user);
        } else {
            throw new InvalidOTPCodeException("Invalid OTP code");
        }
    }

    @Override
    public boolean isTotpEnabled(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found - " + username)
        );

        return user.isTotpEnabled();
    }

    @Override
    public boolean verifyLoginCode(String username, String code) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found - " + username)
        );

        if (!user.isTotpEnabled()) {
            throw new UserDoesNotHaveTOTPException("User " + username + " does not have TOTP enabled");
        }

        return this.verifyCode(user.getTotpSecret(), code);
    }
}
