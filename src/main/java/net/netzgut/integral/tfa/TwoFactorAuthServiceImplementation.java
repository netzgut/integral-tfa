package net.netzgut.integral.tfa;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.netzgut.integral.internal.tfa.Base32;

public class TwoFactorAuthServiceImplementation implements TwoFactorAuthService {

    private static final Logger log = LoggerFactory.getLogger(TwoFactorAuthServiceImplementation.class);

    private final String        algorithm;
    private final long          timeStep;
    private final String        issuer;
    private final int           maxCodeAgeOffset;

    public TwoFactorAuthServiceImplementation(@Symbol(TwoFactorAuthSymbols.ALGROITHM) String algorithm,
                                              @Symbol(TwoFactorAuthSymbols.TIME_STEP) int timeStep,
                                              @Symbol(TwoFactorAuthSymbols.ISSUER) String issuer,
                                              @Symbol(TwoFactorAuthSymbols.MAX_CODE_AGE_OFFSET) int maxCodeAgeOffset) {
        this.algorithm = algorithm;
        this.timeStep = timeStep;
        this.issuer = issuer;
        this.maxCodeAgeOffset = maxCodeAgeOffset;
    }

    @Override
    public String generateSecret() {
        byte[] buffer = new byte[10];
        new SecureRandom().nextBytes(buffer);
        return new String(Base32.encode(buffer));
    }

    @Override
    public URI buildURI(String secret, String label) {
        if (secret == null || secret.length() == 0) {
            throw new IllegalArgumentException("secret must not be blank");
        }

        if (label == null || label.length() == 0) {
            throw new IllegalArgumentException("label must not be blank");
        }

        String query = String.format("secret=%s&issuer=%s&period=%d", secret, this.issuer, this.timeStep);
        URI uri = null;
        try {
            uri = new URI("otpauth", "totp", "/" + label, query, null);
        }
        catch (URISyntaxException e) {
            log.warn("Couldn't build URI", e); // TODO: Better way to report problem without exposing secret to log
        }
        return uri;
    }

    @Override
    public boolean verify(long code, String secret) {
        long timeCounter = System.currentTimeMillis() / 1000 / this.timeStep;
        byte[] decodedKey = Base32.decode(secret);

        // Age is used to check codes generated in the near past.
        int age = this.maxCodeAgeOffset;
        try {
            for (int i = -age; i <= age; ++i) {
                long expected = getHash(decodedKey, timeCounter + i);
                if (expected == code) {
                    return true;
                }
            }
        }
        catch (InvalidKeyException e) {
            log.warn("InvalidKey received"); // TODO: Better way to report problem without exposing secret to log
            return false;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format("Algorithm '%s' not found/creatable", this.algorithm), e);
        }

        return false;
    }

    private int getHash(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, this.algorithm);
        Mac mac = Mac.getInstance(this.algorithm);
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[20 - 1] & 0xF;

        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return (int) truncatedHash;
    }

}
