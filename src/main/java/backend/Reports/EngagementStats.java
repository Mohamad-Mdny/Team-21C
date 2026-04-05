package backend.Reports;

public class EngagementStats {
    private String counterId;
    private String counterDescription;
    private int hitsCount;
    private int purchases;

    public EngagementStats(String counterId, String counterDescription, int hitsCount, int purchases) {
        this.counterId = counterId;
        this.counterDescription = counterDescription;
        this.hitsCount = hitsCount;
        this.purchases = purchases;
    }

    public String getCounterId() {
        return counterId;
    }
    public String getCounterDescription() {
        return counterDescription;
    }
    public int getHitsCount() {
        return hitsCount;
    }
    public int getPurchases() {
        return purchases;
    }
    public String getConversionRate() {
        if (hitsCount == 0 || purchases == 0) return "N/A";
        double rate = (double) purchases / hitsCount;
        return purchases + "/" + hitsCount + " = " + String.format("%.2f", rate) + " (" + String.format("%.0f", rate * 100) + "%)";
    }
}
