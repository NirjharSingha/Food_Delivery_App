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
        String query = "SELECT r.restaurant_id,\n" +
                "    IFNULL(COUNT(o.order_id), 0) AS order_count\n" +
                "FROM Restaurants AS r LEFT JOIN Orders AS o ON r.restaurant_id = o.restaurant_id\n" +
                "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) OR order_date IS NULL\n" +
                "GROUP BY restaurant_id\n" +
                "ORDER BY order_count DESC\n" +
                "LIMIT ?";
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, t);
        statement.setInt(2, x);
        ResultSet rs = statement.executeQuery();

        System.out.println("Restaurant_id   order_count");
        while(rs.next()) {
            System.out.println(rs.getString("restaurant_id") + "\t\t\t\t" + rs.getString("order_count"));
        }
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
                "    AND O.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)\n" +
                "GROUP BY O.delivery_agent ORDER BY canceled_late_orders DESC";
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, t);
        ResultSet r = statement.executeQuery();
        System.out.println("Employee_id  canceled_late_orders");
        while(r.next()) {
            System.out.println(r.getString("delivery_agent_id") + "\t\t\t\t" + r.getInt("canceled_late_orders"));
        }
    }
    public void listUsers() throws SQLException {
        System.out.println("List all users that have signed up in last t days");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the value of t");
        int t = sc.nextInt();
        String query = "SELECT email, username, phone_number, usertype, registration_date FROM Users WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)";
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, t);
        ResultSet r = statement.executeQuery();
        System.out.println("email \t\t\t\t username \t\t phone_number \t\t usertype \t\t\t registration_date");
        while(r.next()) {
            System.out.println(r.getString("email") + "\t\t\t" + r.getString("username") + "\t\t\t" + r.getString("phone_number") + "\t\t\t" + r.getString("usertype") + "\t\t\t" + r.getTimestamp("registration_date"));
        }
    }
    public void listRestaurants() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("List those restaurants that registered in last t days");
        System.out.print("Enter the value of t:");
        int t = sc.nextInt();
        sc.nextLine();
        System.out.print("Restaurants of a particular owner(Press ENTER if you want to skip filter):");
        String res_owner = sc.nextLine().trim();
        System.out.print("Restaurants at a particular address(Press ENTER if you want to skip filter):");
        String address = sc.nextLine().trim();
        String query = "SELECT restaurant_id,\n" +
                "    name,\n" +
                "    res_owner,\n" +
                "    address,\n" +
                "    phone_number,\n" +
                "    registration_date\n" +
                "FROM Restaurants\n" +
                "WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)\n" +
                "    AND (\n" +
                "        ? = ''\n" +
                "        OR res_owner = ?\n" +
                "    )\n" +
                "    AND (\n" +
                "        ? = ''\n" +
                "        OR address = ?\n" +
                "    )";
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, t);
        statement.setString(2, res_owner); // Set res_owner
        statement.setString(3, res_owner); // Set res_owner again
        statement.setString(4, address); // Set address
        statement.setString(5, address); // Set address again

        ResultSet r = statement.executeQuery();
        System.out.println("id \t\t res_name \t\t\t owner \t\t\t contact_number \t\t address \t\t registration_date");
        while(r.next()) {
            System.out.println(r.getString("restaurant_id") + "\t\t\t" + r.getString("name") + "\t\t\t" + r.getString("res_owner") + "\t\t\t" + r.getString("phone_number") + "\t\t\t" + r.getString("address") + "\t\t\t" + r.getTimestamp("registration_date"));
        }
    }
}
