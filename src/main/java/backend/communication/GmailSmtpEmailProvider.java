package backend.communication;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class GmailSmtpEmailProvider implements EmailProvider {

    private final String username;
    private final String password;


    // initialising the provider with gmail login information

    public GmailSmtpEmailProvider(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            throw new IllegalArgumentException("Login information are missing");
        }
        this.username = username;
        this.password = password;
    }


    //sends an email using gmail Smtp
    @Override
    public EmailSendResult send (EmailMessage emailMessage) {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessage.getTo()));
            message.setSubject(emailMessage.getSubject());
            message.setText(emailMessage.getBody());

            Transport.send(message);
            return new EmailSendResult(true, "Email got sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            return new EmailSendResult(false, "Email could not be sent: " + e.getMessage());
        }
    }

    // checks if a string is empty
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
