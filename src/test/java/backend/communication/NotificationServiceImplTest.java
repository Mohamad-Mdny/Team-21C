package backend.communication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceImplTest {

    private NotificationServiceImpl service;
    private EmailMessage lastSent;

    @BeforeEach
    void setUp() {
        EmailProvider fakeProvider = message -> {
            lastSent = message;
            return new EmailSendResult(true, "sent");
        };
        service = new NotificationServiceImpl(fakeProvider);
        lastSent = null;
    }

    // Test 8.0: Registration email sends successfully
    @Test
    void validEmailSendsSuccessfully() {
        EmailSendResult result = service.sendEmail("cool@example.com", "Account Created", "Your account has been created");
        assertTrue(result.isSuccess());
        assertEquals("cool@example.com", lastSent.getTo());
        assertEquals("Account Created", lastSent.getSubject());
    }

    // Test 8.1: Email body contains correct password
    @Test
    void emailBodyPassedCorrectly() {
        service.sendEmail("cool@example.com", "Account Created", "Your password is: 12ss_56_SS");
        assertEquals("Your password is: 12ss_56_SS", lastSent.getBody());
    }

    // Test 8.2: Second member registration email sends successfully
    @Test
    void sendToPU0002() {
        EmailSendResult result = service.sendEmail("cool1@example.com", "Account Created", "Your password is: 34pp_78_LL");
        assertTrue(result.isSuccess());
        assertEquals("cool1@example.com", lastSent.getTo());
    }

    // Test 8.3: Commercial application email sends successfully
    @Test
    void sendToPU0003Commercial() {
        EmailSendResult result = service.sendEmail("pondPharma@example.com", "Application Received", "Your commercial membership is being validated.");
        assertTrue(result.isSuccess());
        assertEquals("pondPharma@example.com", lastSent.getTo());
    }

    // Test 8.4: Null message is rejected
    @Test
    void nullMessageReturnsFalse() {
        EmailSendResult result = service.sendEmail((EmailMessage) null);
        assertFalse(result.isSuccess());
    }

    // Test 8.5: Blank recipient is rejected
    @Test
    void blankToFieldReturnsFalse() {
        EmailSendResult result = service.sendEmail("", "Account Created", "Your account has been created");
        assertFalse(result.isSuccess());
        assertNull(lastSent);
    }

    // Test 8.6: Blank subject is rejected
    @Test
    void blankSubjectReturnsFalse() {
        EmailSendResult result = service.sendEmail("cool@example.com", "   ", "Your account has been created");
        assertFalse(result.isSuccess());
    }

    // Test 8.7: Empty body is rejected
    @Test
    void blankBodyReturnsFalse() {
        EmailSendResult result = service.sendEmail("cool@example.com", "Account Created", "");
        assertFalse(result.isSuccess());
    }

    // Test 9.0: SMTP failure does not crash the application
    @Test
    void providerExceptionReturnsFalseNoCrash() {
        EmailProvider failingProvider = message -> {
            throw new RuntimeException("SMTP connection refused");
        };
        NotificationServiceImpl failService = new NotificationServiceImpl(failingProvider);
        EmailSendResult result = failService.sendEmail("cool@example.com", "Account Created", "Your account has been created");
        assertFalse(result.isSuccess());
    }

    // Test 9.1: Failed send returns a non-empty error message
    @Test
    void failureResultContainsErrorMessage() {
        EmailProvider failingProvider = message -> {
            throw new RuntimeException("Timeout");
        };
        NotificationServiceImpl failService = new NotificationServiceImpl(failingProvider);
        EmailSendResult result = failService.sendEmail("cool1@example.com", "Account Created", "Your account has been created");
        assertNotNull(result.getMessage());
        assertFalse(result.getMessage().isEmpty());
    }
}
