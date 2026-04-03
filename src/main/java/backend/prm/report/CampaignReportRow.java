package backend.prm.report;

import java.time.LocalDateTime;

public class CampaignReportRow {
    private final long campaignId;
    private final String campaignCode;
    private final String campaignTitle;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final String status;
    private final int clickCount;
    private final int totalAdded;
    private final int totalPurchased;

    public CampaignReportRow(long campaignId,
                             String campaignCode,
                             String campaignTitle,
                             LocalDateTime startDateTime,
                             LocalDateTime endDateTime,
                             String status,
                             int clickCount,
                             int totalAdded,
                             int totalPurchased) {
        this.campaignId = campaignId;
        this.campaignCode = campaignCode;
        this.campaignTitle = campaignTitle;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.clickCount = clickCount;
        this.totalAdded = totalAdded;
        this.totalPurchased = totalPurchased;
    }

    public long getCampaignId() { return campaignId; }
    public String getCampaignCode() { return campaignCode; }
    public String getCampaignTitle() { return campaignTitle; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public String getStatus() { return status; }
    public int getClickCount() { return clickCount; }
    public int getTotalAdded() { return totalAdded; }
    public int getTotalPurchased() { return totalPurchased; }
}
