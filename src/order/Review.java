package order;

import utils.ConnectDatabase;
import utils.Login;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Review {
    public void submitReview() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        String query = "SELECT DISTINCT O.order_id\n" +
                "FROM Orders AS O\n" +
                "LEFT JOIN Reviews AS R ON O.order_id = R.order_id\n" +
                "LEFT JOIN OrderStatus AS OS ON O.order_id = OS.order_id\n" +
                "WHERE O.user_email = ? \n" +
                "AND OS.delivered_to_customer IS NOT NULL\n" +
                "AND R.order_id IS NULL";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        ArrayList<String> ids = new ArrayList<>();
        boolean flag = false;
        while(r.next()) {
            System.out.println(r.getString("O.order_id"));
            ids.add(r.getString("O.order_id"));
            flag = true;
        }
        if(flag) {
            System.out.println("These are the order ids for which you didn't submit review");
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the order_id for which you want to submit review");
            String id = sc.next();
            if (ids.contains(id)) {
                String q = "SELECT OD.menu_id, M.name FROM OrderDetails AS OD INNER JOIN Menu AS M ON OD.menu_id = M.menu_id WHERE OD.order_id = ?";
                PreparedStatement st = connection.prepareStatement(q);
                st.setString(1, id);
                ResultSet rs = st.executeQuery();
                System.out.println("Enter rating out of 5:");
                while (rs.next()) {
                    System.out.print(rs.getString("OD.menu_id") + "  " + rs.getString("M.name") + " : ");
                    BigDecimal value = sc.nextBigDecimal();
                    String insertQuery = "INSERT INTO Reviews (order_id, menu_id, rating, review_date) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setString(1, id);
                    preparedStatement.setString(2, rs.getString("OD.menu_id"));
                    preparedStatement.setBigDecimal(3, value);
                    preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

                    preparedStatement.executeUpdate();
                }
                System.out.println("Review submitted");
            } else {
                System.out.println("Invalid id");
            }
        } else {
            System.out.println("There is no order to submit review");
        }
    }
}
