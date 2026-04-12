package backend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member("cool@example.com");
    }

    // Test 3.15: Member is signed in on creation
    @Test
    void memberIsSignedInOnCreation() {
        assertTrue(member.isSignedIn());
    }

    // Test 3.16: Username stored correctly in constructor
    @Test
    void userNameSetInConstructor() {
        assertEquals("cool@example.com", member.getUserName());
    }

    // Test 3.17: Second member username stored correctly
    @Test
    void secondMemberUserNameSet() {
        Member member2 = new Member("cool1@example.com");
        assertEquals("cool1@example.com", member2.getUserName());
    }

    // Test 3.18: Card number returns masked last four digits
    @Test
    void cardNumberReturnsMaskedLastFourDigits() {
        member.setCardNumber("0000000000000001");
        assertEquals("0001", member.getCardNumber());
    }

    // Test 3.19: Default card number is masked
    @Test
    void defaultCardNumberMasked() {
        assertEquals("1234", member.getCardNumber());
    }

    // Test 3.20: CVV stored correctly
    @Test
    void setAndGetCVV() {
        member.setCVV(032);
        assertEquals(032, member.getCVV());
    }

    // Test 3.21: Expiry date stored correctly
    @Test
    void setAndGetExpiryDate() {
        member.setExpiryDate("9/2028");
        assertEquals("9/2028", member.getExpiryDate());
    }

    // Test 3.22: Delivery address stored correctly
    @Test
    void setAndGetDeliveryAddress() {
        member.setDeliveryAddress("25 High Street, Chislehurst, BR7 5BN");
        assertEquals("25 High Street, Chislehurst, BR7 5BN", member.getDeliveryAddress());
    }

    // Test 3.23: Billing address stored correctly
    @Test
    void setAndGetBillingAddress() {
        member.setBillingAddress("25 High Street, Chislehurst, BR7 5BN");
        assertEquals("25 High Street, Chislehurst, BR7 5BN", member.getBillingAddress());
    }

    // Test 3.24: Phone number stored correctly
    @Test
    void setAndGetPhoneNumber() {
        member.setPhoneNumber("02073218001");
        assertEquals("02073218001", member.getPhoneNumber());
    }

    // Test 3.25: Non-commercial account type stored correctly
    @Test
    void setAndGetNonCommercialType() {
        member.setType("nonCommercial");
        assertEquals("nonCommercial", member.getType());
    }

    // Test 3.26: Commercial account type stored correctly
    @Test
    void setAndGetCommercialType() {
        member.setType("Commercial");
        assertEquals("Commercial", member.getType());
    }

    // Test 3.27: Member inherits basket from User
    @Test
    void memberInheritsBasketFromUser() {
        Item ospen = new Item(30000001, "Ospen", "Box", "Caps", 20, 10.50f, 78, 10);
        member.addItem(ospen);
        assertEquals(1, member.getBasket().size());
    }

    // Test 3.28: Member purchase fails with empty basket
    @Test
    void memberPurchaseFailsWithEmptyBasket() {
        assertFalse(member.purchase("25 High Street, Chislehurst", "Card ending in 0005", "Standard", "none"));
    }

    // Test 3.29: Member purchase fails with null address
    @Test
    void memberPurchaseFailsWithNullAddress() {
        Item ospen = new Item(30000001, "Ospen", "Box", "Caps", 20, 10.50f, 78, 10);
        member.addItem(ospen);
        assertFalse(member.purchase(null, "Card ending in 0005", "Standard", "none"));
    }
}
