package com.liangyou.module;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * SMTP验证
 * @author scuchen
 * @version 1.0
 */
public class DefaultAuthenticator extends Authenticator {

	/** Stores the login information for authentication */
    private PasswordAuthentication authentication;

    /**
     * Default constructor
     *
     * @param userName user name to use when authentication is requested
     * @param password password to use when authentication is requested
     * @since 1.0
     */
    public DefaultAuthenticator(String userName, String password)
    {
        this.authentication = new PasswordAuthentication(userName, password);
    }

    /**
     * Gets the authentication object that will be used to login to the mail
     * server.
     *
     * @return A <code>PasswordAuthentication</code> object containing the
     *         login information.
     * @since 1.0
     */
    protected PasswordAuthentication getPasswordAuthentication()
    {
        return this.authentication;
    }

}
