package employee;

import utils.ConnectDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public void canceledLateOrders() throws SQLException {
        System.out.println("Canceled orders due to late deliveries in last t days by each delivery agent");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the value of t");
        int t = sc.nextInt();
        String query = "SELECT O.delivery_agent AS delivery_agent_id,\n" +
                "    COUNT(*) AS canceled_late_orders\n" +
                "FROM Orders AS O\n" +
                "    JOIN OrderStatus AS OS ON O.order_id = OS.order_id\n" +
                "WHERE OS.cancellation_status IS NOT NULL\n" +
                "    AND TIMESTAMPDIFF(MINUTE, O.order_date, OS.cancellation_status) > O.delivery_time\n" +
                "    AND O.order_date >= DATE_SUB(CURDATE(), INTERVAL " + t + " DAY)\n" +
                "GROUP BY O.delivery_agent ORDER BY canceled_late_orders DESC";
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet r = statement.executeQuery();
        System.out.println("Employee_id  canceled_late_orders");
        while(r.next()) {
            System.out.println(r.getString("delivery_agent_id") + "  " + r.getInt("canceled_late_orders"));
        }
    }
}
