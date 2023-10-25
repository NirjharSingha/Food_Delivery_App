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
                "LIMIT " + x;
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        System.out.println("order_id restaurant order_date total_price(without delivery charge) delivery_charge delivery_time delivery_taken delivered_to_customer payment_status cancellation_status");
        while (r.next()) {
            System.out.println(r.getString("O.order_id") + " " + r.getString("O.restaurant_id") + " " + r.getTimestamp("O.order_date") + " " + r.getBigDecimal("O.total_price") + " " + r.getBigDecimal("O.delivery_fee") + " " + r.getInt("O.delivery_time") + " " + r.getTimestamp("OS.delivery_taken") + " " + r.getTimestamp("OS.delivered_to_customer") + " " + r.getString("OS.payment_status") + " " + r.getTimestamp("OS.cancellation_status"));
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
                (choice == 1 ? "" : choice == 2 ? "AND OS.delivered_to_customer IS NULL AND OS.cancellation_status IS NULL" : "OS.cancellation_status IS NOT NULL") +
                "ORDER BY O.order_date DESC";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        System.out.println("order_id user restaurant order_date total_price(without delivery charge) delivery_charge delivery_time delivery_taken delivered_to_customer payment_status cancellation_status");
        while (r.next()) {
            System.out.println(r.getString("O.order_id") + " " + r.getString("O.user_email") + " " + r.getString("O.restaurant_id") + " " + r.getTimestamp("O.order_date") + " " + r.getBigDecimal("O.total_price") + " " + r.getBigDecimal("O.delivery_fee") + " " + r.getInt("O.delivery_time") + " " + r.getTimestamp("OS.delivery_taken") + " " + r.getTimestamp("OS.delivered_to_customer") + " " + r.getString("OS.payment_status") + " " + r.getTimestamp("OS.cancellation_status"));
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
                (choice == 1 ? "" : choice == 2 ? "WHERE OS.delivered_to_customer IS NULL AND OS.cancellation_status IS NULL" : "WHERE OS.cancellation_status IS NOT NULL") +
                "ORDER BY O.order_date DESC";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet r = statement.executeQuery();
        System.out.println("order_id user restaurant order_date total_price(without delivery charge) delivery_charge delivery_time delivery_taken delivered_to_customer payment_status cancellation_status");
        while (r.next()) {
            System.out.println(r.getString("O.order_id") + " " + r.getString("O.user_email") + " " + r.getString("O.restaurant_id") + " " + r.getTimestamp("O.order_date") + " " + r.getBigDecimal("O.total_price") + " " + r.getBigDecimal("O.delivery_fee") + " " + r.getInt("O.delivery_time") + " " + r.getTimestamp("OS.delivery_taken") + " " + r.getTimestamp("OS.delivered_to_customer") + " " + r.getString("OS.payment_status") + " " + r.getTimestamp("OS.cancellation_status"));
        }
    }
}
