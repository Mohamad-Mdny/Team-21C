package backend.prm.controller;

import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.service.PromotionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Acts as the entry point between the UI (frontend) and the PromotionService.
 * Handles all user interactions related to promotion campaigns:
 *
 * CUSTOMER operations:
 *  - Check if "Promotions" link should be shown
 *  - List active campaigns
 *  - View campaign details and items
 *  - Record campaign click
 *  - Add promotional items to order
 *  - Confirm order payment (triggers purchased counters)
 *
 * ADMIN operations:
 *  - Create a new campaign
 *  - Cancel a campaign
 *  - Add items to a campaign
 *  - View all campaigns (any status)
 */
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    //Checks whether the "Promotions" link should be shown to a visiting customer.
    public boolean shouldShowPromotionsLink(LocalDateTime now) {
        List<PromotionCampaign> active = promotionService.getActiveCampaigns(now);
        return !active.isEmpty();
    }

    //Returns the list of all currently active promotion campaigns.
   public List<PromotionCampaign> getActiveCampaigns(LocalDateTime now) {
        return promotionService.getActiveCampaigns(now);
    }

    //Returns the details of a specific campaign and records a click.
  public PromotionCampaign viewCampaignAndRecordClick(long campaignId, LocalDateTime now) {
        // Record the click counter (CAMP_X_counter)
        promotionService.recordCampaignClick(campaignId);
        return promotionService.getCampaignById(campaignId);
    }

    //Returns the list of promotional items in a specific campaign.
   public List<PromotionItem> getCampaignItems(long campaignId) {
        return promotionService.getItemsByCampaign(campaignId);
    }

    //Records that a customer has added a promotional item to their order.
   public void addItemToOrder(long campaignId, long itemId, int quantity) {
        promotionService.recordItemAddedToOrder(campaignId, itemId, quantity);
    }

    //Records that an order containing promotional items has been paid/completed.
    public void confirmOrderPayment(long campaignId, long itemId, int quantity) {
        promotionService.recordItemPurchased(campaignId, itemId, quantity);
    }

    //Returns the conversion rate for a specific promotional item.
   public double getItemConversionRate(long campaignId, long itemId) {
        return promotionService.getConversionRate(campaignId, itemId);
    }


    //Creates a new promotion campaign.
    public PromotionCampaign createCampaign(String title, String description,
                                            LocalDateTime startDateTime,
                                            LocalDateTime endDateTime) {
        return promotionService.createCampaign(title, description, startDateTime, endDateTime);
    }

    //Cancels an active or scheduled campaign.
    public void cancelCampaign(long campaignId) {
        promotionService.cancelCampaign(campaignId);
    }

    //Adds a product to a campaign with a specified discount.
   public PromotionItem addItemToCampaign(long campaignId, long productId, double discountPercent) {
        return promotionService.addItemToCampaign(campaignId, productId, discountPercent);
    }

    /**
     * Returns all campaigns regardless of status.
     * Used by Admin to manage and monitor all campaigns.
     */
    public List<PromotionCampaign> getAllCampaigns() {
        return promotionService.getAllCampaigns();
    }

    //Returns a single campaign by ID.
   public PromotionCampaign getCampaignById(long campaignId) {
        return promotionService.getCampaignById(campaignId);
    }

    //Returns conversion rates for all items in a campaign.
   public Map<Long, Double> getCampaignConversionRates(long campaignId) {
        return promotionService.getCampaignConversionRates(campaignId);
    }
}