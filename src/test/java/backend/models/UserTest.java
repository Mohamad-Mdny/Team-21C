package backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Item paracetamol;
    private Item analgin;

    @BeforeEach
    void setUp() {
        user = new User();
        paracetamol = new Item(10000001, "Paracetamol", "Box", "Caps", 20, 0.10f, 121, 10);
        analgin = new Item(10000003, "Analgin", "Box", "Caps", 10, 1.20f, 25, 10);
    }

    // Test 3.0: New user is not signed in by default
    @Test
    void newUserIsNotSignedIn() {
        assertFalse(user.isSignedIn());
    }

    // Test 3.1: Sign-in sets state to true
    @Test
    void signInChangesState() {
        user.signIn();
        assertTrue(user.isSignedIn());
    }

    // Test 5.0: Sign-out after sign-in resets state
    @Test
    void signOutAfterSignIn() {
        user.signIn();
        user.signOut();
        assertFalse(user.isSignedIn());
    }

    // Test 3.2: Fresh user basket is empty
    @Test
    void basketStartsEmpty() {
        assertTrue(user.getBasket().isEmpty());
    }

    // Test 3.3: Adding one item increases basket size
    @Test
    void addItemIncreasesBasketSize() {
        user.addItem(paracetamol);
        assertEquals(1, user.getBasket().size());
    }

    // Test 3.4: Adding two items increases basket size
    @Test
    void addMultipleItems() {
        user.addItem(paracetamol);
        user.addItem(analgin);
        assertEquals(2, user.getBasket().size());
    }

    // Test 3.5: Removing one item decreases basket size
    @Test
    void removeItem() {
        user.addItem(paracetamol);
        user.addItem(analgin);
        user.removeItem(paracetamol);
        assertEquals(1, user.getBasket().size());
    }

    // Test 3.6: Removing item not in basket has no effect
    @Test
    void removeItemNotInBasketDoesNothing() {
        user.addItem(paracetamol);
        user.removeItem(analgin);
        assertEquals(1, user.getBasket().size());
    }

    // Test 3.7: Empty basket subtotal is zero
    @Test
    void emptyBasketSubtotalIsZero() {
        assertEquals(0.0, user.getBasketSubtotal(), 0.001);
    }

    // Test 3.8: Subtotal sums package costs correctly
    @Test
    void subtotalSumsPackageCosts() {
        user.addItem(paracetamol);
        user.addItem(analgin);
        assertEquals(1.30, user.getBasketSubtotal(), 0.01);
    }

    // Test 3.9: Duplicate items counted separately in subtotal
    @Test
    void duplicateItemsCountedSeparately() {
        user.addItem(paracetamol);
        user.addItem(paracetamol);
        assertEquals(0.20, user.getBasketSubtotal(), 0.01);
    }

    // Test 3.10: Purchase fails with empty basket
    @Test
    void purchaseFailsWithEmptyBasket() {
        assertFalse(user.purchase("cool@example.com", "25 High Street, Chislehurst", "Visa 0001", "Standard", "none"));
    }

    // Test 3.11: Purchase fails with null delivery address
    @Test
    void purchaseFailsWithNullAddress() {
        user.addItem(paracetamol);
        assertFalse(user.purchase("cool@example.com", null, "Visa 0001", "Standard", "none"));
    }

    // Test 3.12: Purchase fails with blank delivery address
    @Test
    void purchaseFailsWithBlankAddress() {
        user.addItem(paracetamol);
        assertFalse(user.purchase("cool@example.com", "   ", "Visa 0001", "Standard", "none"));
    }

    // Test 3.13: Purchase fails with null payment method
    @Test
    void purchaseFailsWithNullPayment() {
        user.addItem(paracetamol);
        assertFalse(user.purchase("cool@example.com", "25 High Street, Chislehurst", null, "Standard", "none"));
    }

    // Test 3.14: Purchase fails with blank delivery option
    @Test
    void purchaseFailsWithBlankDeliveryOption() {
        user.addItem(paracetamol);
        assertFalse(user.purchase("cool@example.com", "25 High Street, Chislehurst", "Visa 0001", "", "none"));
    }
}
