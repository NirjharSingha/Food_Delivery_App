package customer;

import utils.ConnectDatabase;
import utils.Login;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Order {
    public void orderFilters() throws SQLException {
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
                "        LOWER(res_name) LIKE LOWER(?)\n" +
                "        OR ? = ''\n" +
                "    )\n" +
                "    AND (\n" +
                "        price <= CAST(? AS DECIMAL(5, 2))\n" +
                "        OR ? = ''\n" +
                "    )\n" +
                "    AND (\n" +
                "        LOWER(category) LIKE LOWER(?)\n" +
                "        OR ? = ''\n" +
                "    )" +
                "    AND (\n" +
                "        rating >= CAST(? AS DECIMAL(3, 2))" +
                "        OR ? = ''\n" +
                "    )" +
                "    AND availability_status = 1";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, "%" + dishName + "%");
        preparedStatement.setString(2, dishName);
        preparedStatement.setString(3, resName);
        preparedStatement.setString(4, resName);
        preparedStatement.setString(5, price);
        preparedStatement.setString(6, price);
        preparedStatement.setString(7, category);
        preparedStatement.setString(8, category);
        preparedStatement.setString(9, rating);
        preparedStatement.setString(10, rating);

        ResultSet r = preparedStatement.executeQuery();
        while(r.next()) {
            System.out.println(r.getString("menu_name") + " " + r.getBigDecimal("rating"));
        }
    }

    public void placeOrder() throws SQLException {
        Scanner sc = new Scanner(System.in);
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        String sql = "SELECT restaurant_id, price FROM Menu WHERE menu_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);

        Map<String, Map<String, OrderItem>> restaurantMenuMap = new HashMap<>();

        while (true) {
            System.out.println("Enter menu_id");
            String menuId = sc.next();
            System.out.println("Enter quantity");
            int quantity = sc.nextInt();

            String restaurantId = "";
            BigDecimal price = null;
            statement.setString(1, menuId);
            ResultSet r = statement.executeQuery();

            if (r.next()) {
                restaurantId = r.getString("restaurant_id");
                price = r.getBigDecimal("price");
            }

            if (restaurantMenuMap.containsKey(restaurantId)) {
                Map<String, OrderItem> menuItems = restaurantMenuMap.get(restaurantId);
                if (menuItems.containsKey(menuId)) {
                    // Menu item already exists, update the quantity
                    OrderItem orderItem = menuItems.get(menuId);
                    orderItem.setQuantity(orderItem.getQuantity() + quantity);
                } else {
                    // Menu item doesn't exist, add it to the map
                    menuItems.put(menuId, new OrderItem(quantity, price));
                }
            } else {
                // Create a new map for the restaurant and add the menu item
                Map<String, OrderItem> menuItems = new HashMap<>();
                menuItems.put(menuId, new OrderItem(quantity, price));
                restaurantMenuMap.put(restaurantId, menuItems);
            }

            System.out.println("Press 1 to add more items.");
            System.out.println("Press any other key to confirm your order.");
            System.out.println("Items selected from different restaurants will be considered as different orders, and you need to pay separate delivery charges for each of them.");
            int choice = sc.nextInt();
            if (choice != 1) {
                break;
            }
        }

        System.out.println("Enter delivery address");
        String del_address = sc.next();


        Map<String, BigDecimal> orderTotalPriceMap = new HashMap<>();
        for (Map.Entry<String, Map<String, OrderItem>> entry : restaurantMenuMap.entrySet()) {
            String restaurantId = entry.getKey();
            Map<String, OrderItem> menuItems = entry.getValue();
            BigDecimal totalOrderPrice = BigDecimal.ZERO;

            for (OrderItem orderItem : menuItems.values()) {
                totalOrderPrice = totalOrderPrice.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            }

            orderTotalPriceMap.put(restaurantId, totalOrderPrice);
        }

        for (Map.Entry<String, Map<String, OrderItem>> entry : restaurantMenuMap.entrySet()) {
            String restaurantId = entry.getKey(); // i-th key
            Map<String, OrderItem> menuItems = entry.getValue(); // i-th value (another map)
            String orderId = Login.getLoggedINUser() + restaurantId + LocalDateTime.now();

            String query1 = "INSERT INTO Orders (order_id, user_email, restaurant_id, order_date, delivery_address, total_price, delivery_time, delivery_fee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setString(1, orderId);
            statement1.setString(2, Login.getLoggedINUser());
            statement1.setString(3, restaurantId);
            statement1.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement1.setString(5, del_address);
            statement1.setBigDecimal(6, orderTotalPriceMap.get(restaurantId));
            statement1.setInt(7, 30);
            statement1.setBigDecimal(8, orderTotalPriceMap.get(restaurantId).multiply(new BigDecimal("0.1")));

            statement1.executeUpdate();

            // Example: Loop through menuItems (another map) for this restaurant
            for (Map.Entry<String, OrderItem> itemEntry : menuItems.entrySet()) {
                String menuId = itemEntry.getKey(); // Menu ID
                OrderItem orderItem = itemEntry.getValue(); // OrderItem

                String query2 = "INSERT INTO OrderDetails (order_id, menu_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement statement2 = connection.prepareStatement(query2);
                statement2.setString(1, orderId);
                statement2.setString(2, menuId);
                statement2.setInt(3, orderItem.getQuantity());

                statement2.executeUpdate();
            }

            String query3 = "INSERT INTO OrderStatus (order_id, payment_status, cancellation_status) VALUES (?, ?, ?)";
            PreparedStatement statement3 = connection.prepareStatement(query3);
            statement3.setString(1, orderId);
            statement3.setString(2, "Pending");
            statement3.setBoolean(3, false);

            statement3.executeUpdate();
        }

    }
}

class OrderItem {
    private int quantity;
    private BigDecimal price;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderItem(int quantity, BigDecimal price) {
        this.quantity = quantity;
        this.price = price;
    }
}