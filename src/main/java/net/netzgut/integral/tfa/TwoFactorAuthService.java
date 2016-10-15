package net.netzgut.integral.tfa;

import java.net.URI;

public interface TwoFactorAuthService {

    String generateSecret();

    URI buildURI(String secret, String label);

    boolean verify(long code, String secret);
}
