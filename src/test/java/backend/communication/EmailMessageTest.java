package backend.communication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailMessageTest {

    // Test 8.8: Registration email data object is correct
    @Test
    void constructorSetsAllFields() {
        EmailMessage msg = new EmailMessage("cool@example.com", "Account Created", "Your password is: 12ss_56_SS");
        assertEquals("cool@example.com", msg.getTo());
        assertEquals("Account Created", msg.getSubject());
        assertEquals("Your password is: 12ss_56_SS", msg.getBody());
    }

    // Test 8.9: Commercial application email data object is correct
    @Test
    void commercialApplicationEmail() {
        EmailMessage msg = new EmailMessage("pondPharma@example.com", "Application Received", "Your commercial membership is being validated.");
        assertEquals("pondPharma@example.com", msg.getTo());
        assertEquals("Application Received", msg.getSubject());
        assertEquals("Your commercial membership is being validated.", msg.getBody());
    }
}
