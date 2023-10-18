package customer;

import utils.ConnectDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Order {
    public void placeOrder() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.println("Apply filters to place your order");
        System.out.print("Dish name:");
        String dishName = sc.nextLine().trim();
        System.out.print("Restaurant name:");
        String resName = sc.nextLine().trim();
        System.out.print("Price not more than:");
        String price = sc.nextLine().trim();
        System.out.print("Delivery time in minutes:");
        String deliveryTime = sc.nextLine().trim();
        System.out.print("Minimum rating:");
        String rating = sc.nextLine().trim();
        System.out.print("Category(Veg, Non-veg, Vegan):");
        String category = sc.nextLine().trim();

        String query = "WITH orders(\n" +
                "    menu_id,\n" +
                "    menu_name,\n" +
                "    category,\n" +
                "    price,\n" +
                "    availability_status,\n" +
                "    res_name,\n" +
                "    delivery_time,\n" +
                "    rating\n" +
                ") AS (\n" +
                "    SELECT m.menu_id,\n" +
                "        m.name AS menu_name,\n" +
                "        m.category,\n" +
                "        m.price,\n" +
                "        m.availability_status,\n" +
                "        r.name AS res_name,\n" +
                "        r.delivery_time,\n" +
                "        COALESCE((SELECT AVG(rating) FROM Reviews WHERE rating IS NOT NULL AND Reviews.menu_id = m.menu_id), 0) AS rating\n" +
                "    FROM Menu AS m\n" +
                "    INNER JOIN Restaurants AS r ON m.restaurant_id = r.restaurant_id\n" +
                ")\n" +
                "SELECT *\n" +
                "FROM orders\n" +
                "WHERE (\n" +
                "        LOWER(menu_name) LIKE LOWER(?)\n" +
                "        OR ? = ''\n" +
                "    )\n" +
                "    AND (\n" +
                "        LOWER(res_name) = LOWER(?)\n" +
                "        OR ? = ''\n" +
                "    )\n" +
                "    AND (\n" +
                "        price <= CAST(? AS DECIMAL(5, 2))\n" +
                "        OR ? = ''\n" +
                "    )\n" +
                "    AND (\n" +
                "        delivery_time <= CAST(? AS SIGNED)\n" +
                "        OR ? = ''\n" +
                "    )\n" +
                "    AND (\n" +
                "        LOWER(category) LIKE LOWER(?)\n" +
                "        OR ? = ''\n" +
                "    )" +
                "    AND (\n" +
                "        rating >= CAST(? AS DECIMAL(3, 2))" +
                "        OR ? = ''\n" +
                "    )";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, "%" + dishName + "%");
        preparedStatement.setString(2, dishName);
        preparedStatement.setString(3, resName);
        preparedStatement.setString(4, resName);
        preparedStatement.setString(5, price);
        preparedStatement.setString(6, price);
        preparedStatement.setString(7, deliveryTime);
        preparedStatement.setString(8, deliveryTime);
        preparedStatement.setString(9, category);
        preparedStatement.setString(10, category);
        preparedStatement.setString(11, rating);
        preparedStatement.setString(12, rating);

        ResultSet r = preparedStatement.executeQuery();
        while(r.next()) {
            System.out.println(r.getString("menu_name") + " " + r.getBigDecimal("rating"));
        }
    }
}
