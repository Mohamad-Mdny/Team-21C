package backend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterControllerTest {

    private RegisterController controller;

    @BeforeEach
    void setUp() {
        controller = new RegisterController();
    }

    // Test 1.0: Password is correct length
    @Test
    void generatePasswordReturnsCorrectLength() {
        String password = controller.generatePassword();
        assertEquals(10, password.length());
    }

    // Test 1.1: Password only uses valid characters
    @Test
    void generatePasswordContainsOnlyValidCharacters() {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%&*?";
        String password = controller.generatePassword();
        for (char c : password.toCharArray()) {
            assertTrue(validChars.indexOf(c) >= 0,
                "Password contains invalid character: " + c);
        }
    }

    // Test 1.2: Each generated password is unique
    @Test
    void generatePasswordIsDifferentEachTime() {
        String p1 = controller.generatePassword();
        String p2 = controller.generatePassword();
        assertNotEquals(p1, p2);
    }

    // Test 1.3: Valid email accepted for non-commercial member
    @Test
    void emailCheckAcceptsPU0001() {
        assertTrue(controller.emailCheck("cool@example.com"));
    }

    // Test 1.4: Valid email accepted for second non-commercial member
    @Test
    void emailCheckAcceptsPU0002() {
        assertTrue(controller.emailCheck("cool1@example.com"));
    }

    // Test 2.0: Email with no @ symbol rejected
    @Test
    void emailCheckRejectsNoAtSymbol() {
        assertFalse(controller.emailCheck("notanemail"));
    }

    // Test 2.1: Blank email rejected
    @Test
    void emailCheckRejectsBlankString() {
        assertFalse(controller.emailCheck(""));
    }

    // Test 6.0: Valid commercial email accepted
    @Test
    void emailCheckAcceptsPU0003Commercial() {
        assertTrue(controller.emailCheck("pondPharma@example.com"));
    }

    // Test 7.0: Commercial email missing @ symbol rejected
    @Test
    void emailCheckRejectsMissingAtInCommercial() {
        assertFalse(controller.emailCheck("pondPharmacy"));
    }
}
