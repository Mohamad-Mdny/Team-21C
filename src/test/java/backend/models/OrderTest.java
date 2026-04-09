package backend.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    //checks if valid order ID returns order details
    @Test
    void PU_S_13_validOrderIdReturnsOrderDetails() {
        Order order = new Order(101, "Paracetamol x2", "buyer@example.com");
        assertEquals(101, order.getOrderID());
        assertNotNull(order.getDescription());
    }

    //checks if invalid order ID does not crash
    @Test
    void PU_S_14_invalidOrderIdDoesNotCrash() {
        assertDoesNotThrow(() -> new Order(99999, "ghost order", "nobody@example.com"));
    }


}