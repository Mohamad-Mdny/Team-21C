package backend.communication;

public class EmailSendResult {

    private final boolean success;
    private final String message;

    // returns if an email has been sent successfully or not
    public EmailSendResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // returns true if email was sent
    public boolean isSuccess() {
        return success;
    }

    // returns the message sent
    public String getMessage() {
        return message;
    }
}
