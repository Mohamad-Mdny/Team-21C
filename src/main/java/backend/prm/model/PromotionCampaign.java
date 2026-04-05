package backend.prm.model;

import java.time.LocalDateTime;

public class PromotionCampaign {

    private long id;
    private String campaignCode;
    private String title;
    private String description;
    private double discountPercent;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime cancelledAt;
    private int clickCount;

    public PromotionCampaign(long id,
                             String campaignCode,
                             String title,
                             String description,
                             LocalDateTime startDateTime,
                             LocalDateTime endDateTime,
                             double discountPercent) {
        this.id = id;
        this.campaignCode = campaignCode;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.discountPercent = discountPercent;
        this.clickCount = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCampaignCode() {
        return campaignCode;
    }

    public void setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }


    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public void incrementClickCount() {
        this.clickCount++;
    }

    public PromotionStatus getStatus(LocalDateTime now) {
        if (cancelledAt != null) {
            return PromotionStatus.CANCELLED;
        }
        if (now.isBefore(startDateTime)) {
            return PromotionStatus.SCHEDULED;
        }
        if (now.isAfter(endDateTime)) {
            return PromotionStatus.EXPIRED;
        }
        return PromotionStatus.ACTIVE;
    }

    @Override
    public String toString() {
        String code = campaignCode == null || campaignCode.isBlank() ? "Campaign" : campaignCode;
        return code + " - " + title;
    }
}
