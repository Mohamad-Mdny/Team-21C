package backend.EndPoints;

import backend.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StockRepo {

    public static void decrementLocalStock(int productId, int qty) {

        // decrements stock on the local db
        try {
            Connection conn = new DatabaseManager().makeConnection();

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE catalogue SET Stock = Stock - ? WHERE ProductID = ?"
            );

            stmt.setInt(1, qty);
            stmt.setInt(2, productId);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
