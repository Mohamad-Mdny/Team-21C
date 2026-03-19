package backend.prm.repository;

import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Responsible for storing and retrieving PromotionCampaign and PromotionItem objects.
 * Currently uses in-memory storage (HashMap).
 */
public class PromotionRepository {

    // campaignId -> PromotionCampaign
    private final Map<Long, PromotionCampaign> campaignStore = new HashMap<>();

    // itemId -> PromotionItem
    private final Map<Long, PromotionItem> itemStore = new HashMap<>();

    // Auto-increment ID counters
    private long campaignIdSequence = 1;
    private long itemIdSequence = 1;


    //Saves a new campaign and assigns it a generated ID.
    public PromotionCampaign saveCampaign(PromotionCampaign campaign) {
        campaign.setId(campaignIdSequence++);
        campaignStore.put(campaign.getId(), campaign);
        return campaign;
    }


    //Finds a campaign by its ID.
    public Optional<PromotionCampaign> findCampaignById(long id) {
        return Optional.ofNullable(campaignStore.get(id));
    }

    //Returns all campaigns in the store
    public List<PromotionCampaign> findAllCampaigns() {
        return new ArrayList<>(campaignStore.values());
    }

    //Returns all campaigns that are currently ACTIVE at the given time.
    public List<PromotionCampaign> findActiveCampaigns(LocalDateTime now) {
        return campaignStore.values().stream()
                .filter(c -> c.getStatus(now) == PromotionStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public PromotionCampaign updateCampaign(PromotionCampaign campaign) {
        if (!campaignStore.containsKey(campaign.getId())) {
            throw new IllegalArgumentException("Cannot update — campaign not found with ID: " + campaign.getId());
        }
        campaignStore.put(campaign.getId(), campaign);
        return campaign;
    }

//    //Returns all campaigns with a specific status.
//    public List<PromotionCampaign> findCampaignsByStatus(PromotionStatus status, LocalDateTime now) {
//        return campaignStore.values().stream()
//                .filter(c -> c.getStatus(now) == status)
//                .collect(Collectors.toList());
//    }

    //Deletes a campaign by ID (admin use only).
    public void deleteCampaign(long campaignId) {
        if (!campaignStore.containsKey(campaignId)) {
            throw new IllegalArgumentException("Campaign not found with ID: " + campaignId);
        }
        campaignStore.remove(campaignId);
        // Remove all items belonging to this campaign
        itemStore.values().removeIf(item -> item.getCampaignId() == campaignId);
    }

    //Checks whether a campaign exists.
    public boolean campaignExists(long campaignId) {
        return campaignStore.containsKey(campaignId);
    }


    //Saves a new promotion item and assigns it a generated ID.
     public PromotionItem saveItem(PromotionItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }
        if (!campaignStore.containsKey(item.getCampaignId())) {
            throw new IllegalArgumentException(
                    "Cannot save item — campaign not found with ID: " + item.getCampaignId());
        }
        item.setId(itemIdSequence++);
        itemStore.put(item.getId(), item);
        return item;
    }

    //Updates an existing promotion item.
    public PromotionItem updateItem(PromotionItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }
        if (!itemStore.containsKey(item.getId())) {
            throw new IllegalArgumentException("Cannot update — item not found with ID: " + item.getId());
        }
        if (!campaignStore.containsKey(item.getCampaignId())) {
            throw new IllegalArgumentException(
                    "Cannot update item — campaign not found with ID: " + item.getCampaignId());
        }
        itemStore.put(item.getId(), item);
        return item;
    }

    //Finds a promotion item by its ID.
    public Optional<PromotionItem> findItemById(long itemId) {
        return Optional.ofNullable(itemStore.get(itemId));
    }

    //Returns all items belonging to a specific campaign.
    public List<PromotionItem> findItemsByCampaignId(long campaignId) {
        return itemStore.values().stream()
                .filter(item -> item.getCampaignId() == campaignId)
                .collect(Collectors.toList());
    }

    //Finds a promotion item by its ID and validates it belongs to the given campaign.
    public Optional<PromotionItem> findItemByIdAndCampaignId(long itemId, long campaignId) {
        return Optional.ofNullable(itemStore.get(itemId))
                .filter(item -> item.getCampaignId() == campaignId);
    }

    //Checks whether a campaign already contains an item for a given product.
    public boolean itemExistsInCampaign(long campaignId, long productId) {
        return itemStore.values().stream()
                .anyMatch(item -> item.getCampaignId() == campaignId
                        && item.getProductId() == productId);
    }

    //Deletes a promotion item by ID.
    public void deleteItem(long itemId) {
        if (!itemStore.containsKey(itemId)) {
            throw new IllegalArgumentException("Item not found with ID: " + itemId);
        }
        itemStore.remove(itemId);
    }
}