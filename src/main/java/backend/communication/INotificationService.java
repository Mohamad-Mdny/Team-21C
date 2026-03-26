package backend.communication;

public interface INotificationService {

    EmailSendResult sendEmail (String to, String subject, String body);
    EmailSendResult sendEmail (EmailMessage emailMessage);
}
