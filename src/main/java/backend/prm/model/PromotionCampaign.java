package main.java.backend.prm.model;


import java.time.LocalDateTime;

public class PromotionCampaign {

    private long id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime cancelledAt;
    private int clickCount;

    public PromotionCampaign() {
    }

    public PromotionCampaign(long id, String title, String description,
                             LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    public void incrementClickCount() {
        this.clickCount++;
    }
    public int getClickCount() {
        return clickCount;
    }
}