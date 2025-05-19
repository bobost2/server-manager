package com.bobost.panel_back_end.service;

public interface TotpService {
    String generateSecret();
    String getOtpURI(String secret, String username);
    boolean verifyCode(String secret, String code);

    String setupTotpAndReturnURI(String username);
    void verifyTotp(String username, String code);
    void disableTotp(String username, String code);

    // Login specific methods
    boolean isTotpEnabled(String username);
    boolean verifyLoginCode(String username, String code);
}
