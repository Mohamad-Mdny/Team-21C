package backend.communication;

public interface EmailProvider {

    // send email using an email message object
    EmailSendResult send (EmailMessage emailMessage);
}
