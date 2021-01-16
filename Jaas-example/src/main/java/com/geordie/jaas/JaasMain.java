package com.geordie.jaas;

import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * This example associate below
 * https://docs.oracle.com/javase/10/security/jaas-authentication-tutorial.htm
 */
public class JaasMain {

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.setProperty("java.security.auth.login.config", JaasMain.class.getResource("jaas.conf").toExternalForm());
        LoginContext lc = null;
        try {
            lc = new LoginContext("MyContext", new TestCallbackHandler());
        } catch (LoginException le) {
            System.err.println("Cannot create LoginContext. "
                    + le.getMessage());
            System.exit(-1);
        } catch (SecurityException se) {
            System.err.println("Cannot create LoginContext. "
                    + se.getMessage());
            System.exit(-1);
        }

        // the user has 3 attempts to authenticate successfully
        int i;
        for (i = 0; i < 3; i++) {
            try {

                // attempt authentication
                lc.login();

                // if we return with no exception, authentication succeeded
                break;

            } catch (LoginException le) {

                System.err.println("Authentication failed:");
                System.err.println("  " + le.getMessage());
                try {
                    Thread.currentThread().sleep(3000);
                } catch (Exception e) {
                    // ignore
                }

            }
        }

        // did they fail three times?
        if (i == 3) {
            System.out.println("Sorry");
            System.exit(-1);
        }
        System.out.println("Authentication succeeded!");
    }

    static class TestCallbackHandler implements CallbackHandler {

        /**
         * @param callbacks an array of <code>Callback</code> objects which contain
         *                  the information requested by an underlying security
         *                  service to be retrieved or displayed.
         * @throws java.io.IOException          if an input or output error occurs. <p>
         * @throws UnsupportedCallbackException if the implementation of this
         *                                      method does not support one or more of the Callbacks
         *                                      specified in the <code>callbacks</code> parameter.
         */
        public void handle(Callback[] callbacks)
                throws UnsupportedCallbackException {

            Scanner in = new Scanner(System.in);
            System.out.println("Handle callback length:" + callbacks.length);

            for (int i = 0; i < callbacks.length; i++) {
                System.out.println(String.format("Callback index : %d , class : %s", i, callbacks[i].getClass()));
                System.out.flush();
                if (callbacks[i] instanceof NameCallback) {
                    NameCallback nc = (NameCallback) callbacks[i];
                    System.err.print(nc.getPrompt());
                    System.err.flush();
                    nc.setName(in.next());

                } else if (callbacks[i] instanceof PasswordCallback) {
                    PasswordCallback pc = (PasswordCallback) callbacks[i];
                    System.err.print(pc.getPrompt());
                    System.err.flush();
                    pc.setPassword(in.next().toCharArray());

                } else {
                    throw new UnsupportedCallbackException
                            (callbacks[i], "Unrecognized Callback");
                }
            }
        }
    }
}


