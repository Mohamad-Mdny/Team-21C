package backend.prm.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PromotionCampaignTest {

    @Test
    void testCampaignIsScheduled() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        PromotionCampaign campaign = new PromotionCampaign(
                1, "C001","Test", "Desc", start, end, 10.0
        );

        PromotionStatus status = campaign.getStatus(LocalDateTime.now());

        assertEquals(PromotionStatus.SCHEDULED, status);
    }

    @Test
    void testCampaignIsCancelled() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        PromotionCampaign campaign = new PromotionCampaign(
                1, "C001","Test", "Desc", start, end, 10.0
        );

        campaign.setCancelledAt(LocalDateTime.now());
        assertEquals(PromotionStatus.CANCELLED, campaign.getStatus(LocalDateTime.now()));
    }

    @Test
    void testCampaignIsActive() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        PromotionCampaign campaign = new PromotionCampaign(
                1, "C001","Test", "Desc", start, end, 10.0
        );

        assertEquals(PromotionStatus.ACTIVE, campaign.getStatus(LocalDateTime.now()));
    }

    @Test
    void testCampaignIsExpired() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        PromotionCampaign campaign = new PromotionCampaign(
                1, "C001","Test", "Desc", start, end, 10.0
        );

        assertEquals(PromotionStatus.EXPIRED, campaign.getStatus(LocalDateTime.now()));
    }

    @Test
    void testClickCountIncrements() {
        PromotionCampaign campaign = new PromotionCampaign(
                1, "C001", "Test", "Desc",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                10.0
        );

        campaign.incrementClickCount();

        assertEquals(1, campaign.getClickCount());
    }
}
