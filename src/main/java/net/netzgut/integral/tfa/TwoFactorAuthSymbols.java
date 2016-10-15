package net.netzgut.integral.tfa;

public class TwoFactorAuthSymbols {

    /**
     * The issuer of the TwoFactorAuthCode, normally displayed in Authenticator apps.
     */
    public static final String ISSUER              = "integral.tfa.issuer";

    /**
     * The algorithm you want to use.
     */
    public static final String ALGROITHM           = "integral.tfa.algoritm";

    /**
     * Duration of a single code.
     */
    public static final String TIME_STEP           = "integral.tfa.time-step";

    /**
     * Validity of codes to compensate for synchronization issues.
     */
    public static final String MAX_CODE_AGE_OFFSET = "integral.tfa.max-code-age-offset";

}
