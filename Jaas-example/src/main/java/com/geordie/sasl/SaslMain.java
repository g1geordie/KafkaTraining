package com.geordie.sasl;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import java.util.Scanner;

public class SaslMain {

    private static String[] mechanisms = new String[]{"PLAIN"};

    public static void main(String[] args) throws SaslException {
        MySaslServerProvider.initialize();

        SaslServer ss = getSaslServer();
        SaslClient sc = getSaslClient();

        byte[] challenge;
        byte[] response;

        challenge = ss.evaluateResponse(new byte[0]);
        while (challenge != null) {
            response = sc.evaluateChallenge(challenge);
            challenge = ss.evaluateResponse(response);
        }
        System.out.println("Auth Success");
    }


    private static SaslServer getSaslServer() throws SaslException {
        return Sasl.createSaslServer("PLAIN", "gd",
                "localhost", null, new CallbackHandler() {
                    private String name;
                    private String password;

                    @Override
                    public void handle(Callback[] cbs) {
                        for (Callback cb : cbs) {
                            if (cb instanceof AuthenticationCallback) {
                                AuthenticationCallback authenticationCallback = (AuthenticationCallback) cb;
                                boolean success = "user".equals(name) && "password".equals(password);
                                authenticationCallback.setAuthentication(success);
                            } else if (cb instanceof NameCallback) {
                                NameCallback nameCallback = (NameCallback) cb;
                                name = nameCallback.getName();
                            } else if (cb instanceof PasswordCallback) {
                                PasswordCallback passwordCallback = (PasswordCallback) cb;
                                password = new String(passwordCallback.getPassword());
                            }
                        }
                    }
                });
    }

    private static SaslClient getSaslClient() throws SaslException {
        return Sasl.createSaslClient(mechanisms, "ID", "gd",
                "localhost", null, callbacks -> {
                    Scanner in = new Scanner(System.in);

                    for (Callback callback : callbacks) {
                        if (callback instanceof NameCallback) {
                            NameCallback nc = (NameCallback) callback;
                            System.err.print(nc.getPrompt());
                            System.err.flush();
                            nc.setName(in.next());

                        } else if (callback instanceof PasswordCallback) {
                            PasswordCallback pc = (PasswordCallback) callback;
                            System.err.print(pc.getPrompt());
                            System.err.flush();
                            pc.setPassword(in.next().toCharArray());
                        }
                    }
                });
    }
}
