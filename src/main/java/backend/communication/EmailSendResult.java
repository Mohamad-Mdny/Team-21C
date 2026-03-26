package backend.communication;

public class EmailSendResult {

    private final boolean success;
    private final String message;

    public EmailSendResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
}
