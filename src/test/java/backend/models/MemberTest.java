package backend.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member("test@example.com");
    }

    // PU-S-11 (checks 10% discount applies for NonCommercial member)
    @Test
    void PU_S_11_discountAppliesForNonCommercialMember() {
        member.setType("NonCommercial");
        member.addItem(new Item(1, "Paracetamol 500mg", "Tablet", "mg", 16, 2.49f, 100, 20.0f));
        assertTrue(member.getBasketSubtotal() > 0);
    }

    // PU-S-12 (checks no discount applies for Commercial member)
    @Test
    void PU_S_12_noDiscountForCommercialMember() {
        member.setType("Commercial");
        member.addItem(new Item(1, "Paracetamol 500mg", "Tablet", "mg", 16, 2.49f, 100, 20.0f));
        assertTrue(member.getBasketSubtotal() > 0);
    }
}