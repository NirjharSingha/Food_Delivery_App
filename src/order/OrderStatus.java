package order;

import utils.ConnectDatabase;
import utils.Login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class OrderStatus {
    public void customerQuery() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        System.out.println("See the status of last x orders.");
        System.out.println("Enter the value of x");
        Scanner scanner = new Scanner(System.in);
        int x = scanner.nextInt();
        String query = "SELECT O.order_id,\n" +
                "    O.restaurant_id,\n" +
                "    O.order_date,\n" +
                "    O.total_price,\n" +
                "    O.delivery_fee,\n" +
                "    O.delivery_time,\n" +
                "    OS.delivery_taken,\n" +
                "    OS.delivered_to_customer,\n" +
                "    OS.payment_status,\n" +
                "    OS.cancellation_status\n" +
                "FROM Orders AS O\n" +
                "    JOIN OrderStatus AS OS ON O.order_id = OS.order_id\n" +
                "WHERE O.user_email = ?\n" +
                "ORDER BY O.order_date DESC\n" +
                "LIMIT ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        statement.setInt(2, x);
        ResultSet r = statement.executeQuery();
        System.out.println("order_id \t\t restaurant \t\t order_date \t\t\t\t total_price(without delivery charge) \t\t delivery_charge \t\t delivery_time \t\t\t\t delivery_taken \t\t\t\t\t\t\t\t delivered_to_customer \t\t\t\t\t payment_status \t\t\t\t cancellation_status");
        while (r.next()) {
            System.out.println(r.getString("O.order_id") + " \t " + r.getString("O.restaurant_id") + " \t\t\t\t " + r.getTimestamp("O.order_date") + " \t\t " + r.getBigDecimal("O.total_price") + " \t\t\t\t\t\t\t\t\t\t\t " + r.getBigDecimal("O.delivery_fee") + " \t\t\t\t " + r.getInt("O.delivery_time") + " \t\t\t\t\t\t " + r.getTimestamp("OS.delivery_taken") + " \t\t\t\t\t\t\t\t " + r.getTimestamp("OS.delivered_to_customer") + " \t\t\t\t\t " + r.getString("OS.payment_status") + " \t\t\t\t\t\t\t\t " + r.getTimestamp("OS.cancellation_status"));
        }
    }

    public void ownerQuery() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        System.out.println("Enter 1 to see all orders");
        System.out.println("Enter 2 to see pending orders");
        System.out.println("Enter 3 to see refunded orders");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if(choice != 1 && choice != 2 && choice != 3) {
            System.out.println("Invalid input");
            return;
        }
        String query = "SELECT O.order_id,\n" +
                "    O.restaurant_id,\n" +
                "    O.user_email,\n" +
                "    O.order_date,\n" +
                "    O.total_price,\n" +
                "    O.delivery_fee,\n" +
                "    O.delivery_time,\n" +
                "    OS.delivery_taken,\n" +
                "    OS.delivered_to_customer,\n" +
                "    OS.payment_status,\n" +
                "    OS.cancellation_status\n" +
                "FROM Orders AS O\n" +
                "    JOIN OrderStatus AS OS ON O.order_id = OS.order_id\n" +
                "WHERE O.restaurant_id IN (SELECT restaurant_id FROM Restaurants WHERE res_owner = ?)\n" +
                (choice == 1 ? "" : choice == 2 ? " AND OS.delivered_to_customer IS NULL AND OS.cancellation_status IS NULL " : " AND OS.cancellation_status IS NOT NULL ") +
                " ORDER BY O.order_date DESC";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        System.out.println("order_id \t\t user \t\t\t\t restaurant \t\t\t order_date \t\t\t\t total_price(without delivery charge) \t\t\t\t delivery_charge \t\t delivery_time \t\t\t\t delivery_taken \t\t\t\t\t\t\t\t delivered_to_customer \t\t\t\t\t payment_status \t\t\t\t cancellation_status");
        while (r.next()) {
            System.out.println(r.getString("O.order_id") + " \t " + r.getString("O.user_email") + " \t\t\t " + r.getString("O.restaurant_id") + " \t\t\t\t " + r.getTimestamp("O.order_date") + " \t\t\t\t\t " + r.getBigDecimal("O.total_price") + " \t\t\t\t\t\t\t\t\t\t\t " + r.getBigDecimal("O.delivery_fee") + " \t\t\t\t " + r.getInt("O.delivery_time") + " \t\t\t\t\t\t " + r.getTimestamp("OS.delivery_taken") + " \t\t\t\t\t\t\t\t " + r.getTimestamp("OS.delivered_to_customer") + " \t\t\t\t\t " + r.getString("OS.payment_status") + " \t\t\t\t\t\t\t\t " + r.getTimestamp("OS.cancellation_status"));
        }
    }

    public void adminQuery() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        System.out.println("Enter 1 to see all orders");
        System.out.println("Enter 2 to see pending orders");
        System.out.println("Enter 3 to see refunded orders");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if(choice != 1 && choice != 2 && choice != 3) {
            System.out.println("Invalid input");
            return;
        }
        String query = "SELECT O.order_id,\n" +
                "    O.restaurant_id,\n" +
                "    O.user_email,\n" +
                "    O.order_date,\n" +
                "    O.total_price,\n" +
                "    O.delivery_fee,\n" +
                "    O.delivery_time,\n" +
                "    OS.delivery_taken,\n" +
                "    OS.delivered_to_customer,\n" +
                "    OS.payment_status,\n" +
                "    OS.cancellation_status\n" +
                "FROM Orders AS O\n" +
                "    JOIN OrderStatus AS OS ON O.order_id = OS.order_id\n" +
                (choice == 1 ? "" : choice == 2 ? " WHERE OS.delivered_to_customer IS NULL AND OS.cancellation_status IS NULL " : " WHERE OS.cancellation_status IS NOT NULL ") +
                "ORDER BY O.order_date DESC";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet r = statement.executeQuery();
        System.out.println("order_id \t\t user \t\t\t\t restaurant \t\t\t order_date \t\t\t\t total_price(without delivery charge) \t\t\t\t delivery_charge \t\t delivery_time \t\t\t\t delivery_taken \t\t\t\t\t\t\t\t delivered_to_customer \t\t\t\t\t payment_status \t\t\t\t cancellation_status");
        while (r.next()) {
            System.out.println(r.getString("O.order_id") + " \t " + r.getString("O.user_email") + " \t\t\t " + r.getString("O.restaurant_id") + " \t\t\t\t " + r.getTimestamp("O.order_date") + " \t\t\t\t\t " + r.getBigDecimal("O.total_price") + " \t\t\t\t\t\t\t\t\t\t\t " + r.getBigDecimal("O.delivery_fee") + " \t\t\t\t " + r.getInt("O.delivery_time") + " \t\t\t\t\t\t " + r.getTimestamp("OS.delivery_taken") + " \t\t\t\t\t\t\t\t " + r.getTimestamp("OS.delivered_to_customer") + " \t\t\t\t\t " + r.getString("OS.payment_status") + " \t\t\t\t\t\t\t\t " + r.getTimestamp("OS.cancellation_status"));
        }
    }
}
