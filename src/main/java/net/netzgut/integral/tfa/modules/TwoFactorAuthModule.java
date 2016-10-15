package net.netzgut.integral.tfa.modules;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;

import net.netzgut.integral.tfa.TwoFactorAuthService;
import net.netzgut.integral.tfa.TwoFactorAuthServiceImplementation;
import net.netzgut.integral.tfa.TwoFactorAuthSymbols;

public class TwoFactorAuthModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(TwoFactorAuthService.class, TwoFactorAuthServiceImplementation.class);
    }

    @FactoryDefaults
    @Contribute(SymbolProvider.class)
    public static void supplyFactoryDefaults(MappedConfiguration<String, String> conf) {
        conf.add(TwoFactorAuthSymbols.ISSUER, "Integral TFA");
        conf.add(TwoFactorAuthSymbols.ALGROITHM, "HmacSHA1");
        conf.add(TwoFactorAuthSymbols.TIME_STEP, "30");
        conf.add(TwoFactorAuthSymbols.MAX_CODE_AGE_OFFSET, "1");
    }

}
