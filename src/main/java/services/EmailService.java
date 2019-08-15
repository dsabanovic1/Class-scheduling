package services;

import models.ContactEmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class EmailService {


    public static void sendContactEmail(ContactEmail contactEmail) throws ClassNotFoundException,SQLException,MessagingException{
        final String fromEmail = "dzenan.sabanovic.149@gmail.com";
        final String password = "Sarajevo1";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        sendTLSEmail(session, contactEmail.getToEmail(), "You've been contacted via zyox.de", contactEmail.getMailBody());

    }

    private static void sendTLSEmail(Session session, String toEmail, String subject, String body)
            throws MessagingException, ClassNotFoundException, SQLException {
        MimeMessage msg = new MimeMessage(session);

        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");

        msg.setFrom(new InternetAddress("blabal@gmail.com"));

        msg.setSubject(subject, "UTF-8");

        msg.setText(body, "UTF-8");

        msg.setSentDate(new Date());

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        Transport.send(msg);
    }
}
