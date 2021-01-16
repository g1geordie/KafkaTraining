package com.geordie.sasl;

import javax.security.auth.callback.Callback;

public class AuthenticationCallback implements Callback {

    private boolean authentication = false;

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }
}
