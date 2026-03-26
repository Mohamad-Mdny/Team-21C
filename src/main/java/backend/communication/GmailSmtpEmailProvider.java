package backend.communication;

public class GmailSmtpEmailProvider implements EmailProvider {

    @Override
    public EmailSendResult send (EmailMessage emailMessage) {
        System.out.println("sending the email to: " + emailMessage.getTo());
        System.out.println("email subject is: " + emailMessage.getSubject());
        System.out.println("email body is: " + emailMessage.getBody());

        return new EmailSendResult(true, "email sent successfully");
    }
}
