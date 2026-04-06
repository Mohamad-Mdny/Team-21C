package backend.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void PU_S_01_catalogueItemsHaveBasicInfoPopulated() {
        Item item = new Item(1, "Paracetamol 500mg", "Tablet", "mg", 16, 2.49f, 100, 20.0f);


        assertTrue(item.getItemID() > 0);
        assertNotNull(item.getDescription());
        assertNotNull(item.getPackageType());
    }

}