package backend.communication;

public class NotificationServiceImpl implements INotificationService {

    private final EmailProvider emailProvider;

    public NotificationServiceImpl(EmailProvider emailProvider) {
        this.emailProvider = emailProvider;
    }

    @Override
    public EmailSendResult sendEmail (String to, String subject, String body) {
        return sendEmail(new EmailMessage(to, subject, body));
    }

    @Override
    public EmailSendResult sendEmail(EmailMessage message) {
        if (message == null ||
        isBlank(message.getTo()) ||
        isBlank(message.getSubject()) ||
        isBlank(message.getBody())) {
            return new EmailSendResult(false, " your request is missing something");
        }

        try {
            return emailProvider.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return new EmailSendResult(false, "email was not able to send");
        }

    }

    private boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }
}
