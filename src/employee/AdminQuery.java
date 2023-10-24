package employee;

import utils.ConnectDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminQuery {
    public void maxOrderRes() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("top x restaurants who got maximum orders in last t days");
        System.out.println("Enter the value of x");
        int x = scanner.nextInt();
        System.out.println("Enter the value of t");
        int t = scanner.nextInt();
        String query = "SELECT restaurant_id,\n" +
                "    COUNT(order_id) AS order_count\n" +
                "FROM Orders\n" +
                "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL " + t + " DAY)\n" +
                "GROUP BY restaurant_id\n" +
                "ORDER BY order_count DESC\n" +
                "LIMIT " + x;
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.executeUpdate();
    }
}
