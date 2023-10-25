package employee;

import utils.ConnectDatabase;
import utils.Login;

import java.sql.*;
import java.util.Scanner;

public class EmployeeQuery {
    public void updateOrderStatus() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the value of t");
        int t = sc.nextInt();
        String query = "SELECT delivery_taken, delivered_to_customer FROM OrderStatus WHERE order_id = (SELECT order_id FROM Orders WHERE delivery_agent = ? LIMIT 1) ";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        if(r.next()) {
            Timestamp t1 = r.getTimestamp("delivery_taken");
            Timestamp t2 = r.getTimestamp("delivered_to_customer");
            if(t1 == null) {
                System.out.println("Is the delivery taken?");
                System.out.println("Press 1 if yes");
                String choice = sc.nextLine().trim();
                if(choice.equals("1")) {
                    String updateQuery = "UPDATE OrderStatus SET delivery_taken = ? WHERE order_id = (SELECT order_id FROM Orders WHERE delivery_agent = ? LIMIT 1)";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    preparedStatement.setString(2, Login.getLoggedINUser());
                    statement.executeUpdate();
                }
            } else if(t2 == null) {
                System.out.println("Is the delivery taken?");
                System.out.println("Press 1 if yes");
                String choice = sc.nextLine().trim();
                if(choice.equals("1")) {
                    String updateQuery = "UPDATE OrderStatus SET delivered_to_customer = ? WHERE order_id = (SELECT order_id FROM Orders WHERE delivery_agent = ? LIMIT 1)";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    preparedStatement.setString(2, Login.getLoggedINUser());
                    statement.executeUpdate();
                } else {
                    System.out.println("You have nothing to update");
                }
            }
        }
    }
}
