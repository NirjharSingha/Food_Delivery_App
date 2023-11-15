package employee;

import utils.ConnectDatabase;
import utils.Login;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class EmployeeQuery {
    public void updateOrderStatus() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        String query1 = "SELECT O.order_id,\n" +
                "    O.restaurant_id,\n" +
                "    O.user_email,\n" +
                "    O.order_date,\n" +
                "    O.delivery_agent,\n" +
                "    O.delivery_time,\n" +
                "    OS.delivery_taken,\n" +
                "    OS.delivered_to_customer,\n" +
                "    OS.cancellation_status\n" +
                "FROM Orders AS O\n" +
                "   INNER JOIN OrderStatus AS OS ON O.order_id = OS.order_id\n" +
                "WHERE O.delivery_agent = ?\n" +
                " AND OS.delivered_to_customer IS NULL AND OS.cancellation_status IS NULL " +
                " ORDER BY O.order_date DESC";
        PreparedStatement st = connection.prepareStatement(query1);
        st.setString(1, Login.getLoggedINUser());
        ArrayList<String> ids = new ArrayList<>();
        ResultSet r = st.executeQuery();
        boolean flag = false;
        System.out.println("Order_id \t\t\t\t\t\t\t\t\t\t\t\t user_email \t\t restaurant_id \t\t order_date \t\t\t delivery_time \t\t delivery_taken \t\t delivered_to_customer \t\t cancellation_status");
        while (r.next()) {
            System.out.println(r.getString("O.order_id") + " \t\t\t " + r.getString("O.user_email") + " \t\t\t " + r.getString("O.restaurant_id") + " \t\t\t " + r.getTimestamp("O.order_date") + " \t\t\t " + r.getInt("O.delivery_time") + " \t\t\t " + r.getTimestamp("OS.delivery_taken") + " \t\t\t\t " + r.getTimestamp("OS.delivered_to_customer") + " \t\t\t\t\t\t " + r.getTimestamp("OS.cancellation_status"));
            ids.add(r.getString("O.order_id"));
            flag = true;
        }
        if(flag) {
            System.out.println("From these pending orders Enter the order id for which you want to update status");
            String id = sc.next().trim();

            if (!ids.contains(id)) {
                System.out.println("Invalid id");
                return;
            }
            String query = "SELECT delivery_taken, delivered_to_customer FROM OrderStatus WHERE order_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                Timestamp t1 = rs.getTimestamp("delivery_taken");
                Timestamp t2 = rs.getTimestamp("delivered_to_customer");
                if(t1 == null) {
                    System.out.println("Is the delivery taken?");
                    System.out.println("Press 1 if yes");
                    System.out.println("Press 0 if no");
                    int choice = sc.nextInt();
                    if(choice == 1) {
                        String updateQuery = "UPDATE OrderStatus SET delivery_taken = ? WHERE order_id = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                        preparedStatement.setString(2, id);
                        preparedStatement.executeUpdate();

                        System.out.println("Status updated");
                    }
                } else if(t2 == null) {
                    System.out.println("Is the order delivered to customer?");
                    System.out.println("Press 1 if yes");
                    System.out.println("Press 0 if no");
                    int choice = sc.nextInt();
                    if(choice == 1) {
                        String updateQuery = "UPDATE OrderStatus SET delivered_to_customer = ? WHERE order_id = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                        preparedStatement.setString(2, id);
                        preparedStatement.executeUpdate();

                        String q = "UPDATE Employee SET availability_status = availability_status - 1 WHERE employee_id = ?";
                        PreparedStatement stm = connection.prepareStatement(q);
                        stm.setString(1, Login.getLoggedINUser());
                        stm.executeUpdate();

                        System.out.println("Status updated");
                    }
                }
            }
        } else {
            System.out.println("No pending order");
        }
    }
}
