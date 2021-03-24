package de.wip.tutorbot;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailManager {

    public static void sendMail(Session session,String recipient,String subject,String message,boolean isHTML) throws MessagingException {
        Message msg = new MimeMessage( session );

        InternetAddress addressTo = new InternetAddress( recipient );
        msg.setRecipient( Message.RecipientType.TO, addressTo );

        msg.setSubject( subject );
        msg.setContent( message, isHTML ? "text/html":"text/plain" );
        Transport.send( msg );
    }



    public static Session getGmailSession(String user,String pass){
        final Properties props = new Properties();

        // Zum Empfangen
        props.setProperty( "mail.pop3.host", "pop.gmail.com" );
        props.setProperty( "mail.pop3.user", user );
        props.setProperty( "mail.pop3.password", pass );
        props.setProperty( "mail.pop3.port", "995" );
        props.setProperty( "mail.pop3.auth", "true" );
        props.setProperty( "mail.pop3.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory" );

        // Zum Senden
        props.setProperty( "mail.smtp.host", "smtp.gmail.com" );
        props.setProperty( "mail.smtp.auth", "true" );
        props.setProperty( "mail.smtp.port", "465" );
        props.setProperty( "mail.smtp.socketFactory.port", "465" );
        props.setProperty( "mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory" );
        props.setProperty( "mail.smtp.socketFactory.fallback", "false" );
        return Session.getInstance( props, new javax.mail.Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( props.getProperty( "mail.pop3.user" ),
                        props.getProperty( "mail.pop3.password" ) );
            }
        } );
    //    session.setDebug( true );
    }

}
