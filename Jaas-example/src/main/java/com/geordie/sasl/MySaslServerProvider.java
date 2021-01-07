package com.geordie.sasl;

import java.security.Provider;
import java.security.Security;

public class MySaslServerProvider extends Provider {

    protected MySaslServerProvider() {
        super("MySasl", 1.0, "");
        put("SaslServerFactory." + MySaslServer.PLAIN_MECHANISM, MySaslServer.MySaslServerFactory.class.getName());
    }

    public static void initialize() {
        Security.addProvider(new MySaslServerProvider());
    }
}

