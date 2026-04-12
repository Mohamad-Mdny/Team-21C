package backend.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    // Test 3.30: Catalogue item has correct fields
    @Test
    void constructorSetsAllFields() {
        Item item = new Item(10000001, "Paracetamol", "Box", "Caps", 20, 0.10f, 121, 10);
        assertEquals("10000001", item.getItemID());
        assertEquals("Paracetamol", item.getDescription());
        assertEquals("Box", item.getPackageType());
        assertEquals("Caps", item.getUnit());
        assertEquals(20, item.getUnitsInAPack());
        assertEquals(0.10f, item.getPackageCost(), 0.001);
        assertEquals(121, item.getAvailability());
        assertEquals(10, item.getStockLimit(), 0.001);
    }
}
