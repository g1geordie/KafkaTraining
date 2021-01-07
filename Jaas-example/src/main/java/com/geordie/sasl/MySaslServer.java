package com.geordie.sasl;

import javax.security.auth.callback.*;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MySaslServer implements javax.security.sasl.SaslServer {

    public static final String PLAIN_MECHANISM = "PLAIN";

    private final CallbackHandler callbackHandler;
    private String authorizationId;

    public MySaslServer(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    boolean init = false;
    boolean success = false;

    @Override
    public byte[] evaluateResponse(byte[] responseBytes) throws SaslException {
        if (!init) {
            if (responseBytes.length == 0) {
                init = true;
                return new byte[0];
            } else {
                throw new SaslException("Error Request");
            }
        }

        List<String> user = getUser(responseBytes);

        NameCallback nameCallback = new NameCallback("name:");
        PasswordCallback passwordCallback = new PasswordCallback("password:", false);
        AuthenticationCallback authenticationCallback = new AuthenticationCallback();

        nameCallback.setName(user.get(1));
        passwordCallback.setPassword(user.get(2).toCharArray());

        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback, authenticationCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            e.printStackTrace();
        }
        if (!authenticationCallback.isAuthentication())
            throw new SaslException("Account or Password has error. ");
        success = true;
        return null;
    }

    private List<String> getUser(byte[] responseBytes) {
        String user = new String(responseBytes, StandardCharsets.UTF_8);
        return Arrays.asList(user.split("\u0000", 3));
    }


    @Override
    public String getAuthorizationID() {
        if (!success)
            throw new IllegalStateException("Authentication exchange has not completed");
        return authorizationId;
    }

    @Override
    public String getMechanismName() {
        return PLAIN_MECHANISM;
    }

    @Override
    public Object getNegotiatedProperty(String propName) {
        if (!success)
            throw new IllegalStateException("Authentication exchange has not completed");
        return null;
    }

    @Override
    public boolean isComplete() {
        return success;
    }

    @Override
    public byte[] unwrap(byte[] incoming, int offset, int len) {
        if (!success)
            throw new IllegalStateException("Authentication exchange has not completed");
        return Arrays.copyOfRange(incoming, offset, offset + len);
    }

    @Override
    public byte[] wrap(byte[] outgoing, int offset, int len) {
        if (!success)
            throw new IllegalStateException("Authentication exchange has not completed");
        return Arrays.copyOfRange(outgoing, offset, offset + len);
    }

    @Override
    public void dispose() {
    }

    public static class MySaslServerFactory implements SaslServerFactory {

        @Override
        public SaslServer createSaslServer(String mechanism, String protocol, String serverName, Map<String, ?> props, CallbackHandler cbh)
                throws SaslException {

            if (!PLAIN_MECHANISM.equals(mechanism))
                throw new SaslException(String.format("Mechanism \'%s\' is not supported. Only PLAIN is supported.", mechanism));

            return new MySaslServer(cbh);
        }

        @Override
        public String[] getMechanismNames(Map<String, ?> props) {
            if (props == null) return new String[]{PLAIN_MECHANISM};
            String noPlainText = (String) props.get(Sasl.POLICY_NOPLAINTEXT);
            if ("true".equals(noPlainText))
                return new String[]{};
            else
                return new String[]{PLAIN_MECHANISM};
        }
    }
}
