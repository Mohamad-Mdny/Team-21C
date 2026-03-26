package backend.communication;

public interface EmailProvider {
    EmailSendResult send (EmailMessage emailMessage);
}
