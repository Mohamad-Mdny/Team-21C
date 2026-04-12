package backend.communication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailSendResultTest {

    // Test 8.10: Successful email send result is correct
    @Test
    void successResult() {
        EmailSendResult result = new EmailSendResult(true, "Email got sent successfully");
        assertTrue(result.isSuccess());
        assertEquals("Email got sent successfully", result.getMessage());
    }

    // Test 9.2: Failed email send result is correct
    @Test
    void failureResult() {
        EmailSendResult result = new EmailSendResult(false, "email was not able to send");
        assertFalse(result.isSuccess());
        assertEquals("email was not able to send", result.getMessage());
    }
}
