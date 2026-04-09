package backend.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    //checks if transaction has valid ID after payment
    @Test
    void PU_S_08_transactionHasValidID() {
        Transaction t = new Transaction(1, 250, "buyer@example.com");
        assertTrue(t.getTransactionID() > 0);
    }

    //checks if transaction amount is correct
    @Test
    void PU_S_08_transactionAmountIsCorrect() {
        Transaction t = new Transaction(1, 250, "buyer@example.com");
        assertEquals(250, t.getAmount());
    }

    //checks if transaction stores customer email
    @Test
    void PU_S_08_transactionStoresCustomerEmail() {
        Transaction t = new Transaction(1, 250, "buyer@example.com");
        assertEquals("buyer@example.com", t.getTransactionEmailAddress());
    }
}