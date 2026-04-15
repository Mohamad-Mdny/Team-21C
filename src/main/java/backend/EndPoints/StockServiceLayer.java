package backend.EndPoints;


public class StockServiceLayer {


    public static boolean isAvailable(int productId, int qty) {
        return StockSyncServices.isStockAvailable(productId, qty);
    }

    public static boolean decrement(String productId, int qty, String orderId) {
        boolean success =
                StockSyncServices.decrementStockInCA(Integer.parseInt(productId), qty, orderId);

        if (success) {
            StockRepo.decrementLocalStock(Integer.parseInt(productId), qty);
        }

        return success;
    }


}
