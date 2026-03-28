package backend.communication;

public class EmailMessage {
    private String to;
    private String subject;
    private String body;


    // creates an email message with an email receiver, subject and coontent body
    public EmailMessage(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }


    // getter methods for email, subject and the content
    public String getTo() {
        return to;
    }
    public String getSubject() {
        return subject;
    }
    public String getBody() {
        return body;
    }
}
