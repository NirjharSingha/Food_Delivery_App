package res_owner;

import utils.ConnectDatabase;
import utils.Login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Owner_Query {
    public void queryMain() throws SQLException {

        System.out.println("These are the restaurants that you own:");
        Restaurant_Activity restaurantActivity = new Restaurant_Activity();
        restaurantActivity.listRes();

        System.out.print("Enter the registration number of the restaurant you want to see the information:");
        Scanner scanner = new Scanner(System.in);
        String res_id = scanner.next();

        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();

        String verifyQuery = "SELECT res_owner FROM Restaurants WHERE restaurant_id = ?";
        PreparedStatement st = connection.prepareStatement(verifyQuery);
        st.setString(1, res_id);
        ResultSet rs = st.executeQuery();
        if(rs.next()) {
            if (Objects.equals(rs.getString("res_owner"), Login.getLoggedINUser())) {

                System.out.println("Press 1 to see best or worst selling items of the selected restaurant");
                System.out.println("Press 2 to see best or worst reviewed items of the selected restaurant");
                System.out.println("Press 3 to see the repeated customer rate of the selected restaurant");
                System.out.println("Press 4 to update the restaurant");
                System.out.println("Press 5 to insert a new menu item in this restaurant");

                int choice = scanner.nextInt();
                if(choice == 1) {
                    sellRate(res_id);
                } else if(choice == 2) {
                    reviewRate(res_id);
                } else if(choice == 3) {
                    repeatedCustomer(res_id);
                } else if(choice == 4) {
                    restaurantActivity.updateRes(res_id);
                } else if(choice == 5) {
                    restaurantActivity.insertMenu(res_id);
                } else {
                    System.out.println("invalid input");
                }
            } else {
                System.out.println("This is not your restaurant. You cannot see its information.");
            }
        }
    }

    public void sellRate(String res_id) throws SQLException {
        System.out.println("press 0 to get top x worst selling items in last t days");
        System.out.println("press 1 to get top x best selling items in last t days");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if(choice != 0 && choice != 1) {
            System.out.println("Invalid input");
        } else {
            System.out.println("Enter the value of x");
            int x = scanner.nextInt();
            System.out.println("Enter the value of t");
            int t = scanner.nextInt();
            String query = "SELECT M.menu_id,\n" +
                    "    M.name,\n" +
                    "    IFNULL(SUM(OD.quantity), 0) AS total_quantity\n" +
                    "FROM Menu AS M\n" +
                    "    LEFT JOIN OrderDetails AS OD ON M.menu_id = OD.menu_id\n" +
                    "    LEFT JOIN Orders AS O ON OD.order_id = O.order_id\n" +
                    "WHERE M.restaurant_id = ? " + " " +
                    "    AND (O.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) OR O.order_date IS NULL)\n" +
                    "GROUP BY M.menu_id,\n" +
                    "    M.name\n" +
                    "ORDER BY total_quantity " + (choice == 1 ? "DESC" : "ASC") + "\n" +
                    "LIMIT ?";
            ConnectDatabase db = new ConnectDatabase();
            Connection connection = db.getCon();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, res_id);
            statement.setInt(2, t);
            statement.setInt(3, x);
            ResultSet rs = statement.executeQuery();

            System.out.println("menu_id \t\t\t name \t\t\t\t total_quantity");
            while(rs.next()) {
                System.out.println(rs.getString("M.menu_id") + "\t\t\t\t" + rs.getString("M.name") + "\t\t\t\t\t" + rs.getInt("total_quantity"));
            }
        }
    }

    public void reviewRate(String res_id) throws SQLException {
        System.out.println("press 0 to get top x worst reviewed items in last t days");
        System.out.println("press 1 to get top x best reviewed items in last t days");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if(choice != 0 && choice != 1) {
            System.out.println("Invalid input");
        } else {
            System.out.println("Enter the value of x");
            int x = scanner.nextInt();
            System.out.println("Enter the value of t");
            int t = scanner.nextInt();
            String query = "WITH ReviewedMenuItems(menu_id, name, average_rating) AS (\n" +
                    "    SELECT M.menu_id,\n" +
                    "        M.name,\n" +
                    "        AVG(R.rating) AS average_rating\n" +
                    "    FROM Menu AS M\n" +
                    "        JOIN Reviews AS R ON M.menu_id = R.menu_id\n" +
                    "        JOIN Orders AS O ON R.order_id = O.order_id\n" +
                    "    WHERE M.restaurant_id = ? " + " \n" +
                    "        AND R.rating IS NOT NULL\n" +
                    "        AND O.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)\n" +
                    "    GROUP BY M.menu_id,\n" +
                    "        M.name\n" +
                    "    HAVING COUNT(R.rating) > 0\n" +
                    ")\n" +
                    "SELECT * \n" +
                    "FROM ReviewedMenuItems\n" +
                    "ORDER BY average_rating "  + (choice == 1 ? "DESC" : "ASC") + "\n" +
                    "LIMIT ?";
            ConnectDatabase db = new ConnectDatabase();
            Connection connection = db.getCon();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, res_id);
            statement.setInt(2, t);
            statement.setInt(3, x);
            ResultSet rs = statement.executeQuery();

            System.out.println("menu_id \t\t\t name \t\t\t average_rating");
            while(rs.next()) {
                System.out.println(rs.getString("menu_id") + "\t\t\t" + rs.getString("name") + "\t\t\t" + rs.getBigDecimal("average_rating"));
            }
        }
    }

    public void repeatedCustomer(String res_id) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("customers who have ordered at least x times in last t days");
        System.out.println("Enter the value of x");
        int x = scanner.nextInt();
        System.out.println("Enter the value of t");
        int t = scanner.nextInt();
        String query = "SELECT user_email, order_count \n" +
                "FROM (\n" +
                "        SELECT user_email, COUNT(*) AS order_count\n" +
                "        FROM Orders\n" +
                "        WHERE restaurant_id = ? " + " \n" +
                "            AND order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)\n" +
                "        GROUP BY user_email\n" +
                "        HAVING order_count >= ? \n" +
                "    ) AS repeated_customers";
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, res_id);
        statement.setInt(2, t);
        statement.setInt(3, x);
        ResultSet rs = statement.executeQuery();

        System.out.println("user_email \t\t\t\t order_count");
        while(rs.next()) {
            System.out.println(rs.getString("user_email") + "\t\t\t\t" + rs.getInt("order_count"));
        }
    }
}
