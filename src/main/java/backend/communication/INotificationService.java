package backend.communication;

public interface INotificationService {

    // send an email using specific parameters
    EmailSendResult sendEmail (String to, String subject, String body);

    // send an email using an emailmessage object
    EmailSendResult sendEmail (EmailMessage emailMessage);
}
