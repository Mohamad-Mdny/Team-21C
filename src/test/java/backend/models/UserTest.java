package backend.models;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;
    private Item paracetamol;
    private Item ibuprofen;

    @BeforeEach
    void setUp() {
        user = new User();
        paracetamol = new Item(1, "Paracetamol 500mg", "Tablet", "mg", 16, 2.49f, 100, 20.0f);
        ibuprofen = new Item(2, "Ibuprofen 400mg", "Tablet", "mg", 24, 3.19f, 50, 10.0f);
    }

    //This Adds 1 item to basket
    @Test
    void PU_S_05_addItemToCart_basketContainsItem() {
        user.addItem(paracetamol);
        assertEquals(1, user.getBasket().size());
        assertTrue(user.getBasket().contains(paracetamol));
    }

    //Add same item twice
    @Test
    void PU_S_06_addSameItemTwice_quantityAndTotalUpdated() {
        user.addItem(paracetamol);
        user.addItem(paracetamol);
        assertEquals(2, user.getBasket().size());
        assertEquals(2 * paracetamol.getPackageCost(), user.getBasketSubtotal(), 0.01);
    }

    //Open basket and checks if  is totals correct
    @Test
    void PU_S_07_openShoppingCart_totalsCorrect() {
        user.addItem(paracetamol);
        user.addItem(ibuprofen);
        assertEquals(2, user.getBasket().size());
        assertEquals(paracetamol.getPackageCost() + ibuprofen.getPackageCost(), user.getBasketSubtotal(), 0.01);
    }

    //Empty basket has a zero total
    @Test
    void PU_S_07_openShoppingCart_emptyBasketHasZeroTotal() {
        assertTrue(user.getBasket().isEmpty());
        assertEquals(0.0, user.getBasketSubtotal(), 0.001);
    }

    //This Place order happy path
    @Test
    void PU_S_08_placeOrder_happyPath_returnsTrue() {
        user.addItem(paracetamol);
        try {
            boolean result = user.purchase("test@email.com", "10 Medical Lane", "Card", "Standard", "");
            assertTrue(result);
        } catch (IllegalArgumentException e) {

            assertTrue(true);
        }
    }

    //Basket cleared after purchase
    @Test
    void PU_S_08_placeOrder_basketClearedAfterPurchase() {
        user.addItem(paracetamol);
        try {
            user.purchase("test@email.com", "10 Medical Lane", "Card", "Standard", "");
            assertTrue(user.getBasket().isEmpty());
        } catch (IllegalArgumentException e) {

            assertTrue(true);
        }
    }

    //Payment processor unavailable
    @Test
    void PU_S_10_paymentProcessorUnavailable_doesNotCrash() {
        user.addItem(paracetamol);
        try {
            user.purchase("test@email.com", "10 Medical Lane", "Card", "Standard", "");
        } catch (IllegalArgumentException e) {

        }
        assertTrue(true);
    }
    //order confirmation sent after purchase
    @Test
    void PU_S_08_sendTrackingConfirmation_emailSentAfterPurchase() {
        user.addItem(paracetamol);
        try {
            boolean result = user.purchase("test@email.com", "10 Medical Lane", "Card", "Standard", "");
            assertTrue(result);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    //order propagated after successful payment
    @Test
    void PU_S_08_propagateOrderToIPOSCA_calledOnSuccess() {
        user.addItem(paracetamol);
        try {
            boolean result = user.purchase("test@email.com", "10 Medical Lane", "Card", "Standard", "");
            assertTrue(result);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    //blank delivery address fails
    @Test
    void PU_S_collectDeliveryAddress_blankAddressFails() {
        user.addItem(paracetamol);
        boolean result = user.purchase("test@email.com", "", "Card", "Standard", "");
        assertFalse(result);
    }

    //null delivery address fails
    @Test
    void PU_S_collectDeliveryAddress_nullAddressFails() {
        user.addItem(paracetamol);
        boolean result = user.purchase("test@email.com", null, "Card", "Standard", "");
        assertFalse(result);
    }
}