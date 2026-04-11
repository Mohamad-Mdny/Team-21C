package backend.prm.service;

import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
import backend.prm.repository.FakePromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PromotionServiceTest {

    private FakePromotionRepository fakeRepo;
    private PromotionService service;

    private final LocalDateTime NOW  = LocalDateTime.now();
    private final LocalDateTime PAST = NOW.minusDays(5);
    private final LocalDateTime FAR  = NOW.plusDays(10);

    @BeforeEach
    void setUp() {
        fakeRepo = new FakePromotionRepository();
        service  = new PromotionService(fakeRepo);
    }

    // Creating a campaign with valid inputs should save and return it
    @Test
    void createCampaign_validInput_returnsSavedCampaign() {
        PromotionCampaign result = service.createCampaign(
                "Summer Sale", "desc", PAST, FAR, 15.0);

        assertNotNull(result);
        assertEquals("Summer Sale", result.getTitle());
        assertEquals(15.0, result.getDiscountPercent(), 0.001);
    }

    // A blank title should be rejected
    @Test
    void createCampaign_blankTitle_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createCampaign("  ", "desc", PAST, FAR, 10.0));
    }

    // End date before start date should be rejected
    @Test
    void createCampaign_endBeforeStart_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createCampaign("Sale", "desc", FAR, PAST, 10.0));
    }

    // Discount above 100% should be rejected
    @Test
    void createCampaign_discountOver100_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createCampaign("Sale", "desc", PAST, FAR, 101.0));
    }

    // Negative discount should be rejected
    @Test
    void createCampaign_negativeDiscount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createCampaign("Sale", "desc", PAST, FAR, -1.0));
    }

    // Null start or end date should be rejected
    @Test
    void createCampaign_nullDates_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createCampaign("Sale", "desc", null, FAR, 10.0));
    }

    // Updating a campaign with valid data should reflect new title and discount
    @Test
    void updateCampaign_validInput_updatesTitle() {
        service.createCampaign("Old Title", "old", PAST, FAR, 5.0);

        PromotionCampaign result = service.updateCampaign(
                1L, "New Title", "new", PAST, FAR, 20.0);

        assertEquals("New Title", result.getTitle());
        assertEquals(20.0, result.getDiscountPercent(), 0.001);
    }

    // Updating a campaign that does not exist should throw an error
    @Test
    void updateCampaign_campaignNotFound_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateCampaign(99L, "Title", "desc", PAST, FAR, 10.0));
    }

    // Cancelling an active campaign should set the cancelled date
    @Test
    void cancelCampaign_activeCampaign_setCancelledAt() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);

        service.cancelCampaign(1L);

        PromotionCampaign cancelled = fakeRepo.findCampaignById(1L).orElseThrow();
        assertNotNull(cancelled.getCancelledAt());
        assertEquals(PromotionStatus.CANCELLED, cancelled.getStatus(NOW));
    }

    // Cancelling an already expired campaign should not be allowed
    @Test
    void cancelCampaign_expiredCampaign_throwsIllegalState() {
        service.createCampaign("Old Sale", "desc",
                NOW.minusDays(10), NOW.minusDays(1), 5.0);

        assertThrows(IllegalStateException.class, () ->
                service.cancelCampaign(1L));
    }

    // Cancelling a campaign that does not exist should throw an error
    @Test
    void cancelCampaign_notFound_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.cancelCampaign(99L));
    }

    // Reactivating a cancelled campaign should clear the cancelled date
    @Test
    void reactivateCampaign_cancelledCampaign_clearsCancelledAt() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);
        service.cancelCampaign(1L);

        service.reactivateCampaign(1L);

        PromotionCampaign reactivated = fakeRepo.findCampaignById(1L).orElseThrow();
        assertNull(reactivated.getCancelledAt());
    }

    // Reactivating a campaign that is still active should not be allowed
    @Test
    void reactivateCampaign_activeCampaign_throwsIllegalState() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);

        assertThrows(IllegalStateException.class, () ->
                service.reactivateCampaign(1L));
    }

    // Adding a valid product to a campaign should save it
    @Test
    void addItemToCampaign_validProduct_savesItem() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);

        PromotionItem result = service.addItemToCampaign(1L, "42", null);

        assertNotNull(result);
        assertEquals("42", result.getItemId());
    }

    // Adding the same product twice to a campaign should be rejected
    @Test
    void addItemToCampaign_duplicateProduct_throwsIllegalArgument() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);
        service.addItemToCampaign(1L, "42", null);

        assertThrows(IllegalArgumentException.class, () ->
                service.addItemToCampaign(1L, "42", null));
    }

    // A product ID that is not a number should be rejected
    @Test
    void addItemToCampaign_nonNumericProductId_throwsIllegalArgument() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);

        assertThrows(IllegalArgumentException.class, () ->
                service.addItemToCampaign(1L, "ABC", null));
    }

    // A blank product ID should be rejected
    @Test
    void addItemToCampaign_blankProductId_throwsIllegalArgument() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);

        assertThrows(IllegalArgumentException.class, () ->
                service.addItemToCampaign(1L, "  ", null));
    }

    // An item override discount above 100% should be rejected
    @Test
    void addItemToCampaign_overrideDiscountOutOfRange_throwsIllegalArgument() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);

        assertThrows(IllegalArgumentException.class, () ->
                service.addItemToCampaign(1L, "42", 150.0));
    }

    // Only campaigns running at the current time should appear in the active list
    @Test
    void getActiveCampaigns_returnsOnlyActiveCampaigns() {
        service.createCampaign("Active", "desc", PAST, FAR, 10.0);
        service.createCampaign("Expired", "desc",
                NOW.minusDays(10), NOW.minusDays(1), 5.0);

        var active = service.getActiveCampaigns(NOW);

        assertEquals(1, active.size());
        assertEquals("Active", active.get(0).getTitle());
    }

    // Clicking a link for an active campaign should increase its click count by 1
    @Test
    void recordCampaignClick_activeCampaign_incrementsClick() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);

        service.recordCampaignClick(1L);

        PromotionCampaign campaign = fakeRepo.findCampaignById(1L).orElseThrow();
        assertEquals(1, campaign.getClickCount());
    }

    // Clicking a link for a scheduled campaign that has not started should not be allowed
    @Test
    void recordCampaignClick_scheduledCampaign_throwsIllegalState() {
        service.createCampaign("Sale", "desc",
                NOW.plusDays(1), NOW.plusDays(5), 10.0);

        assertThrows(IllegalStateException.class, () ->
                service.recordCampaignClick(1L));
    }

    // A null start date on a sales report request should be rejected
    @Test
    void getSalesReport_nullFrom_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.getSalesReport(null, NOW));
    }

    // A report where the end date is before the start date should be rejected
    @Test
    void getSalesReport_endBeforeStart_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.getSalesReport(FAR, PAST));
    }

    // Requesting a campaign report for a valid date range should return a result
    @Test
    void getCampaignReport_validRange_returnsList() {
        var result = service.getCampaignReport(PAST, FAR);
        assertNotNull(result);
    }

    // Requesting a campaign hit report for a valid date range should return a result
    @Test
    void getCampaignHitReport_validRange_returnsList() {
        var result = service.getCampaignHitReport(PAST, FAR);
        assertNotNull(result);
    }

    // Deleting an existing campaign should remove it from the system
    @Test
    void deleteCampaign_existingCampaign_removesIt() {
        service.createCampaign("Sale", "desc", PAST, FAR, 10.0);

        service.deleteCampaign(1L);

        assertTrue(fakeRepo.findCampaignById(1L).isEmpty());
    }

    // Deleting a campaign that does not exist should throw an error
    @Test
    void deleteCampaign_notFound_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                service.deleteCampaign(99L));
    }
}