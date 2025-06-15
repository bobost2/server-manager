package com.bobost.panel_back_end.service;

public interface TotpService {
    String generateSecret();
    String getOtpURI(String secret, String username);
    boolean verifyCode(String secret, String code);

    String setupTotpAndReturnURI(long userId);
    void verifyTotp(long userId, String code);
    void disableTotp(long userId, String code);

    // Login specific methods
    boolean isTotpEnabled(long userId);
    boolean isTotpEnabled(String username);
    boolean verifyLoginCode(String username, String code);
}
