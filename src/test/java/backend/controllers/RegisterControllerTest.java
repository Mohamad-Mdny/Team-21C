package backend.controllers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class RegisterControllerTest {

    private RegisterController registerController;

    @BeforeEach
    void setUp(){
        registerController = new RegisterController();
    }

    @Test
    void generatedPasswordReturnsLength(){
        String password = registerController.generatePassword();
        assertTrue(password.length() == 10);

    }

    @Test
    void generatedPasswordisRandom(){
        String password = registerController.generatePassword();
        String password2 = registerController.generatePassword();
        assertFalse(password.equals(password2));

    }
    @Test
    void generatedPasswordContainsOnlyValid(){
    String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%&*?";
    String password = registerController.generatePassword();
    for (char c : password.toCharArray()){
        assertTrue(validChars.indexOf(c) >= 0,
        "Password contains invalid characters: " + c);
    }

}}