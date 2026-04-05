package backend.prm.frontend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PromotionBasketTracker {

    public static final class Entry {
        private final long campaignId;
        private final long itemId;
        private final int quantity;

        public Entry(long campaignId, long itemId, int quantity) {
            this.campaignId = campaignId;
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public long getCampaignId() {
            return campaignId;
        }

        public long getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    private static final List<Entry> entries = new ArrayList<>();

    private PromotionBasketTracker() {}

    public static void add(long campaignId, long itemId, int quantity) {
        entries.add(new Entry(campaignId, itemId, quantity));
    }

    public static List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public static void clear() {
        entries.clear();
    }

    public static boolean isEmpty() {
        return entries.isEmpty();
    }
}