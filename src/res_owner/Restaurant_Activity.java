package res_owner;

import utils.ConnectDatabase;
import utils.Login;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Restaurant_Activity {

    public void updateRes(String res_id) throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        String query = "SELECT res_owner, name, address, phone_number FROM Restaurants WHERE restaurant_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, res_id);
        ResultSet r = statement.executeQuery();
        if(r.next()) {
            if(!Objects.equals(r.getString("res_owner"), Login.getLoggedINUser())) {
                System.out.println("This restaurant doesn't belong to you. You cannot update it.");
            } else {
                System.out.println("Enter the updated value of each field. If you don't want to update a field then just press ENTER");
                System.out.print("Enter updated name:");
                String name = sc.nextLine().trim();
                System.out.print("Enter updated address:");
                String address = sc.nextLine().trim();
                System.out.print("Enter updated phone number:");
                String phone = sc.nextLine().trim();

                String updateQuery = "UPDATE Restaurants set name = ?, address = ?, phone_number = ? WHERE restaurant_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, name.equals("") ? r.getString("name") : name);
                preparedStatement.setString(2, address.equals("") ? r.getString("address") : address);
                preparedStatement.setString(3, phone.equals("") ? r.getString("phone_number") : phone);
                preparedStatement.setString(4, res_id);
                preparedStatement.executeUpdate();

                System.out.println("Restaurant updated successfully. These are your restaurants:");
                listRes();
            }
        }
    }

    public void listRes() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        String query = "SELECT restaurant_id, name, address, phone_number, registration_date FROM Restaurants WHERE res_owner = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        System.out.println("restaurant_id" + " \t\t\t " + "name" + " \t\t\t " + "address" + " \t\t\t " + "phone_number" + " \t\t\t " + "registration_date");
        while(r.next()) {
            System.out.println(r.getString("restaurant_id") + " \t\t\t\t\t\t " + r.getString("name") + " \t\t\t " + r.getString("address") + " \t\t\t\t " + r.getString("phone_number") + " \t\t\t " + r.getTimestamp("registration_date"));
        }
    }

    public void listMenu() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter registration number of the restaurant(Press enter if you want to see menu of  all your restaurant/s)");
        String res_id = sc.nextLine().trim();
        if(res_id.equals("")) {
            String query = "SELECT menu_id, m.name, r.name, category, price, availability_status FROM Menu as m INNER JOIN Restaurants as r ON m.restaurant_id = r.restaurant_id WHERE res_owner = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Login.getLoggedINUser());
            ResultSet r = statement.executeQuery();
            System.out.println("menu_id menu_name restaurant_name category price availability_status");
            while (r.next()) {
                System.out.println(r.getString("menu_id") + " " + r.getString("m.name") + " " + r.getString("r.name") + " " + r.getString("category") + " " + r.getBigDecimal("price") + " " + r.getBoolean("availability_status"));
            }
        } else {
            String query = "SELECT menu_id, m.name, m.restaurant_id, category, price, availability_status FROM Menu as m INNER JOIN Restaurants as r ON m.restaurant_id = r.restaurant_id WHERE res_owner = ? AND m.restaurant_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Login.getLoggedINUser());
            statement.setString(2, res_id);
            ResultSet r = statement.executeQuery();
            System.out.println("menu_id \t\t\t name \t\t\t category \t\t\t price \t\t\t availability_status");
            while (r.next()) {
                System.out.println(r.getString("menu_id") + " \t\t\t " + r.getString("m.name") + " \t\t\t " + r.getString("category") + " \t\t\t " + r.getBigDecimal("price") + " \t\t\t\t " + r.getBoolean("availability_status"));
            }
        }
    }

    public void updateMenu() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter menu id to update:");
        String menu_id = sc.nextLine().trim();

        String query = "SELECT name, restaurant_id, category, price, availability_status FROM Menu WHERE menu_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, menu_id);

        ResultSet r = statement.executeQuery();
        if(r.next()) {
            String verifyQuery = "SELECT res_owner FROM Restaurants WHERE restaurant_id = ?";
            PreparedStatement st = connection.prepareStatement(verifyQuery);
            st.setString(1, r.getString("restaurant_id"));
            ResultSet rs = st.executeQuery();
            if(rs.next()) {
                if(Objects.equals(rs.getString("res_owner"), Login.getLoggedINUser())) {
                    System.out.println("Enter the updated value of each field. If you don't want to update a field then just press ENTER");
                    System.out.print("Enter updated name:");
                    String name = sc.nextLine().trim();
                    System.out.print("Enter updated category:");
                    String category = sc.nextLine().trim();
                    System.out.print("Enter updated price:");
                    String price = sc.nextLine().trim();
                    System.out.print("Enter availability status:");
                    String status = sc.nextLine().trim();
                    if(!Objects.equals(status, "0") && !status.equals("1") && !status.equals("")) {
                        System.out.println("Invalid status");
                        return;
                    }

                    String updateQuery = "UPDATE Menu set name = ?, category = ?, price = ?, availability_status = ? WHERE menu_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, name.equals("") ? r.getString("name") : name);
                    preparedStatement.setString(2, category.equals("") ? r.getString("category") : category);
                    preparedStatement.setBigDecimal(3, price.equals("") ? r.getBigDecimal("price") : new BigDecimal(price));
                    preparedStatement.setBoolean(4, status.equals("") ? r.getBoolean("availability_status") : status.equals("1"));
                    preparedStatement.setString(5, menu_id);
                    preparedStatement.executeUpdate();

                    System.out.println("Menu updated successfully");
                } else {
                    System.out.println("This menu item doesn't belong to your restaurant. You cannot update it.");
                }
            } else {
                System.out.println("Invalid id");
            }
        }
    }

    public void insertMenu(String res_id) throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter menu name(it cannot be empty):");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.print("Enter category(Veg, Non-veg, Vegan Or Drink):");
        String category = sc.next();
        if(!Objects.equals(category, "Veg") && !Objects.equals(category, "Non-veg") && !Objects.equals(category, "Vegan") && !Objects.equals(category, "Drink")) {
            System.out.println("Invalid category");
            return;
        }
        System.out.print("Enter price(it cannot be empty):");
        BigDecimal price = sc.nextBigDecimal();
        System.out.print("Enter availability status(0 or 1):");
        String status = sc.next();
        if(!Objects.equals(status, "0") && !status.equals("1")) {
            System.out.println("Invalid status");
            return;
        }

        String menu_id = res_id + "_" + name.replace(" ", "_");;
        String verifyQuery = "SELECT name FROM Menu WHERE restaurant_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(verifyQuery);
        preparedStatement.setString(1, res_id);

        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            if(Objects.equals(resultSet.getString("name"), name)) {
                System.out.println("Duplicate menu name. Cannot insert.");
                return;
            }
        }

        String query = "INSERT INTO Menu (menu_id, restaurant_id, name, category, price, availability_status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, menu_id);
        statement.setString(2, res_id);
        statement.setString(3, name);
        statement.setString(4, category);
        statement.setBigDecimal(5, price);
        statement.setBoolean(6, status.equals("1"));
        statement.executeUpdate();

        System.out.println("Menu inserted");
    }
}
