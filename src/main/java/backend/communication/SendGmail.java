package backend.communication;

public class SendGmail {

    //ready to use class to send gmails
    public static EmailSendResult sendGmail(String to, String subject, String body) {
        String email = System.getenv("EMAIL");
        String password = System.getenv("EMAIL_PASSWORD");

        EmailProvider provider = new GmailSmtpEmailProvider(email, password);
        INotificationService service = new NotificationServiceImpl(provider);

        return service.sendEmail(to, subject, body);
    }
}
