package backend.Reports;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EngagementStatsTest {

    // conversion rate should show purchases divided by hits as a percentage
    @Test
    void conversionRate_normalCase_showsCorrectPercentage() {
        EngagementStats stats = new EngagementStats("C001", "Summer Sale", 100, 25);
        String rate = stats.getConversionRate();
        assertEquals("25/100 = 0.25 (25%)", rate);
    }

    // When there are no hits at all, conversion rate should return N/A
    @Test
    void conversionRate_zeroHits_returnsNA() {
        EngagementStats stats = new EngagementStats("C002", "No Views", 0, 0);
        assertEquals("N/A", stats.getConversionRate());
    }

    // When there are hits but no purchases, conversion rate should return N/A
    @Test
    void conversionRate_hitsButNoPurchases_returnsNA() {
        EngagementStats stats = new EngagementStats("C003", "No Buys", 50, 0);
        assertEquals("N/A", stats.getConversionRate());
    }

    // When every hit results in a purchase, conversion rate should show 100%
    @Test
    void conversionRate_allHitsPurchased_shows100Percent() {
        EngagementStats stats = new EngagementStats("C004", "Perfect", 10, 10);
        String rate = stats.getConversionRate();
        assertEquals("10/10 = 1.00 (100%)", rate);
    }

    // Getters should return exactly the values passed to the constructor
    @Test
    void getters_returnCorrectValues() {
        EngagementStats stats = new EngagementStats("C005", "Desc", 40, 8);
        assertEquals("C005", stats.getCounterId());
        assertEquals("Desc", stats.getCounterDescription());
        assertEquals(40, stats.getHitsCount());
        assertEquals(8, stats.getPurchases());
    }
}