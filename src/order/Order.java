package order;

import utils.ConnectDatabase;
import utils.Login;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
                "    rating\n" +
                ") AS (\n" +
                "    SELECT m.menu_id,\n" +
                "        m.name AS menu_name,\n" +
                "        m.category,\n" +
                "        m.price,\n" +
                "        m.availability_status,\n" +
                "        r.name AS res_name,\n" +
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
        System.out.println("menu_id \t\t\t\t\t menu_name \t\t\t\t\t category \t\t\t\t\t price \t\t\t\t\t restaurant \t\t\t\t\t rating");
        while(r.next()) {
            System.out.println(r.getString("menu_id") + " \t\t\t\t\t " + r.getString("menu_name")+ " \t\t\t\t\t " + r.getString("category")+ " \t\t\t\t\t " + r.getBigDecimal("price")+ " \t\t\t\t\t " + r.getString("res_name") +  " \t\t\t\t\t " + r.getBigDecimal("rating"));
        }
        System.out.println("Now you can place the order");
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
            } else {
                System.out.println("Invalid menu id");
                return;
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
            System.out.println("Press 2 to confirm your order.");
            System.out.println("Items selected from different restaurants will be considered as different orders, and you need to pay separate delivery charges for each of them.");
            String choice = sc.next();
            if (!choice.equals("1")) {
                System.out.println("order confirmed");
                break;
            }
        }
        sc.nextLine();
        System.out.println("Enter delivery address");
        String del_address = sc.nextLine();


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
        System.out.println("Your order is placed. Details are given below:");
        System.out.println("Order_id \t\t\t Restaurant \t\t\t Price(without delivery charge) \t\t\t Delivery_charge \t\t\t\t Delivery_time");
        for (Map.Entry<String, Map<String, OrderItem>> entry : restaurantMenuMap.entrySet()) {
            String restaurantId = entry.getKey(); // i-th key
            Map<String, OrderItem> menuItems = entry.getValue(); // i-th value (another map)
            String orderId = Login.getLoggedINUser() + restaurantId + LocalDateTime.now();

            String query1 = "{CALL addNewOrder(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement statement1 = connection.prepareCall(query1);
            statement1.setString(1, Login.getLoggedINUser());

            String tempQuery = "SELECT employee_id\n" +
                    "FROM Employee\n" +
                    "WHERE job = 'Delivery_Agent' AND availability_status = (SELECT MIN(availability_status) FROM Employee)\n" +
                    "LIMIT 1;\n";
            PreparedStatement tempSt = connection.prepareStatement(tempQuery);
            ResultSet r = tempSt.executeQuery();
            if(r.next()) {
                statement1.setString(2, r.getString("employee_id"));
                String updateQuery = "UPDATE Employee SET availability_status = availability_status + 1 WHERE employee_id = ?";
                PreparedStatement st = connection.prepareStatement(updateQuery);
                st.setString(1, r.getString("employee_id"));
                st.executeUpdate();
            } else {
                statement1.setString(2, null);
            }

            statement1.setString(3, restaurantId);
            statement1.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement1.setString(5, del_address);
            statement1.setBigDecimal(6, orderTotalPriceMap.get(restaurantId));
            statement1.setInt(7, 30);
            statement1.setBigDecimal(8, orderTotalPriceMap.get(restaurantId).multiply(new BigDecimal("0.1")));
            statement1.setString(9, orderId);

            statement1.execute();
            orderId = statement1.getString(9);

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

            System.out.println(orderId + " \t\t\t " + restaurantId + " \t\t\t\t\t\t\t " + orderTotalPriceMap.get(restaurantId) + " \t\t\t\t\t\t\t\t " + orderTotalPriceMap.get(restaurantId).multiply(new BigDecimal("0.10")) + " \t\t\t\t\t\t 30 min");
        }

    }

    public void cancelOrder() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        String query = "SELECT DISTINCT O.order_id\n" +
                "FROM Orders AS O\n" +
                "INNER JOIN OrderStatus AS OS ON O.order_id = OS.order_id\n" +
                "WHERE O.user_email = ? \n" +
                "AND OS.delivered_to_customer IS NULL AND OS.cancellation_status IS NULL";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        System.out.println("These are the pending orders");
        ArrayList<String> ids = new ArrayList<>();
        while(r.next()) {
            System.out.println(r.getString("O.order_id"));
            ids.add(r.getString("O.order_id"));
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the order_id you want to cancel");
        String id = sc.next();
        if (ids.contains(id)) {
            String timeQuery = "SELECT order_date, delivery_time FROM Orders WHERE order_id = ?";
            PreparedStatement timeStatement = connection.prepareStatement(timeQuery);
            timeStatement.setString(1, id);
            ResultSet timeResult = timeStatement.executeQuery();
            if (timeResult.next()) {
                Timestamp orderDate = timeResult.getTimestamp("order_date");
                int deliveryTime = timeResult.getInt("delivery_time");

                // Calculate the current time
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                // Calculate the time difference in minutes
                long timeDifference = (currentTime.getTime() - orderDate.getTime()) / (60 * 1000);
                String choice;

                if (timeDifference > deliveryTime) {
                    System.out.println("The delivery time has exceeded. You will get refund if you cancel the order now.");
                    System.out.println("Press 1 if you want to confirm the cancellation");
                    System.out.println("Press any other key to skip");
                    sc.nextLine();
                    choice = sc.nextLine().trim();
                    if(choice.equals("1")) {
                        String q = "UPDATE OrderStatus SET payment_status = ?, cancellation_status = ? WHERE order_id = ?";
                        PreparedStatement st = connection.prepareStatement(q);
                        st.setString(1, "Refunded");
                        st.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                        st.setString(3, id);
                        st.executeUpdate();
                    }
                } else {
                    System.out.println("The delivery time has not exceeded. If you cancel the order now you won't get any refund");
                    System.out.println("Press 1 if you want to confirm the cancellation");
                    System.out.println("Press any other key to skip");
                    sc.nextLine();
                    choice = sc.nextLine().trim();
                    if(choice.equals("1")) {
                        String q = "UPDATE OrderStatus SET cancellation_status = ? WHERE order_id = ?";
                        PreparedStatement st = connection.prepareStatement(q);
                        st.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                        st.setString(2, id);
                        st.executeUpdate();
                    }
                }
                if(choice.equals("1")) {
                    String q = "UPDATE Employee SET availability_status = availability_status - 1 WHERE employee_id = (SELECT delivery_agent FROM Orders WHERE order_id = ?)";
                    PreparedStatement st = connection.prepareStatement(q);
                    st.setString(1, id);
                    st.executeUpdate();
                    System.out.println("Order cancelled");
                }
            }
        } else {
            System.out.println("Invalid id");
        }

    }
}
