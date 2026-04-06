package backend.prm.controller;

import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.report.CampaignHitReportRow;
import backend.prm.report.CampaignReportRow;
import backend.prm.report.SalesReportRow;
import backend.prm.service.PromotionService;

import java.time.LocalDateTime;
import java.util.List;

public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public boolean shouldShowPromotionsLink(LocalDateTime now) {
        return !promotionService.getActiveCampaigns(now).isEmpty();
    }

    public List<PromotionCampaign> getActiveCampaigns(LocalDateTime now) {
        return promotionService.getActiveCampaigns(now);
    }

    public PromotionCampaign viewCampaignAndRecordClick(long campaignId, LocalDateTime now) {
        promotionService.recordCampaignClick(campaignId);
        return promotionService.getCampaignById(campaignId);
    }

    public List<PromotionItem> getCampaignItems(long campaignId) {
        return promotionService.getItemsByCampaign(campaignId);
    }

    public void addItemToOrder(long campaignId, long itemId, int quantity) {
        promotionService.recordItemAddedToOrder(campaignId, itemId, quantity);
    }

    public void addItemToOrder(long campaignId, long itemId, int quantity, String orderReference) {
        promotionService.recordItemAddedToOrder(campaignId, itemId, quantity, orderReference);
    }

    public void confirmOrderPayment(long campaignId, long itemId, int quantity) {
        promotionService.recordItemPurchased(campaignId, itemId, quantity);
    }

    public void confirmOrderPayment(long campaignId, long itemId, int quantity, String orderReference) {
        promotionService.recordItemPurchased(campaignId, itemId, quantity, orderReference);
    }

    public PromotionCampaign createCampaign(String title,
                                            String description,
                                            LocalDateTime startDateTime,
                                            LocalDateTime endDateTime,
                                            double discountPercent) {
        return promotionService.createCampaign(title, description, startDateTime, endDateTime, discountPercent);
    }

    public PromotionCampaign updateCampaign(long id,
                                            String title,
                                            String description,
                                            LocalDateTime startDateTime,
                                            LocalDateTime endDateTime,
                                            double discountPercent) {
        return promotionService.updateCampaign(id, title, description, startDateTime, endDateTime, discountPercent);
    }

    public PromotionItem updateItem(long campaignId, long itemId, String productId, Double overrideDiscountPercent) {
        return promotionService.updateItem(campaignId, itemId, productId, overrideDiscountPercent);
    }
    public void deleteCampaign(long campaignId) {
        promotionService.deleteCampaign(campaignId);
    }

    public void deleteItem(long itemId) {
        promotionService.deleteItem(itemId);
    }

    public void cancelCampaign(long campaignId) {
        promotionService.cancelCampaign(campaignId);
    }

    public void reactivateCampaign(long campaignId) {
        promotionService.reactivateCampaign(campaignId);
    }

    public PromotionItem addItemToCampaign(long campaignId, String productId, Double overrideDiscountPercent) {
        return promotionService.addItemToCampaign(campaignId, productId, overrideDiscountPercent);
    }

    public List<PromotionCampaign> getAllCampaigns() {
        return promotionService.getAllCampaigns();
    }

    public PromotionCampaign getCampaignById(long campaignId) {
        return promotionService.getCampaignById(campaignId);
    }

    public List<SalesReportRow> getSalesReport(LocalDateTime from, LocalDateTime to) {
        return promotionService.getSalesReport(from, to);
    }

    public List<CampaignReportRow> getCampaignReport(LocalDateTime from, LocalDateTime to) {
        return promotionService.getCampaignReport(from, to);
    }

    public List<CampaignHitReportRow> getCampaignHitReport(LocalDateTime from, LocalDateTime to) {
        return promotionService.getCampaignHitReport(from, to);
    }
}