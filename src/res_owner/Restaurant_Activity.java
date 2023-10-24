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

    public void updateRes() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the registration number of the restaurant you want to update:");
        String res_id = sc.next();
        String query = "SELECT res_owner name, address, phone_number FROM Restaurants WHERE restaurant_id = ?";
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

                String updateQuery = "UPDATE Restaurant set name = ?, address = ?, phone_number = ? WHERE restaurant_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, name.equals("") ? r.getString("name") : name);
                preparedStatement.setString(2, address.equals("") ? r.getString("address") : address);
                preparedStatement.setString(3, phone.equals("") ? r.getString("phone_number") : phone);
                preparedStatement.setString(4, res_id);
                preparedStatement.executeUpdate();
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
        System.out.println("restaurant_id" + " " + "name" + " " + "address" + " " + "phone_number" + " " + "registration_date");
        while(r.next()) {
            System.out.println(r.getString("restaurant_id") + " " + r.getString("name") + " " + r.getString("address") + " " + r.getString("phone_number") + " " + r.getTimestamp("registration_date"));
        }
    }

    public void listMenu() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter registration number of the restaurant(Press enter if you want to see menu of  all your restaurant/s)");
        String res_id = sc.nextLine().trim();
        if(res_id.equals("")) {
            String query = "SELECT menu_id, m.name, r.name, description, category, price, availability_status FROM Menu as m INNER JOIN Restaurants as r ON m.restaurant_id = r.restaurant_id WHERE res_owner = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Login.getLoggedINUser());
            ResultSet r = statement.executeQuery();
            System.out.println("menu_id menu_name restaurant_name description category price availability_status");
            while (r.next()) {
                System.out.println(r.getString("menu_id") + " " + r.getString("m.name") + " " + r.getString("r.name") + " " + r.getString("description") + " " + r.getString("category") + " " + r.getBigDecimal("price") + " " + r.getBoolean("availability_status"));
            }
        } else {
            String query = "SELECT menu_id, m.name, description, category, price, availability_status FROM Menu as m INNER JOIN Restaurants as r ON m.restaurant_id = r.restaurant_id WHERE res_owner = ? AND restaurant_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Login.getLoggedINUser());
            statement.setString(2, res_id);
            ResultSet r = statement.executeQuery();
            System.out.println("menu_id name description category price availability_status");
            while (r.next()) {
                System.out.println(r.getString("restaurant_id") + " " + r.getString("m.name") + " " + r.getString("description") + " " + r.getString("category") + " " + r.getBigDecimal("price") + " " + r.getBoolean("availability_status"));
            }
        }
    }

    public void updateMenu() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter menu id to update:");
        String menu_id = sc.next();

        String query = "SELECT name, restaurant_id, description, category, price, availability_status FROM Menu WHERE menu_id = ?";
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
                    System.out.print("Enter updated description:");
                    String desc = sc.nextLine().trim();
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

                    String updateQuery = "UPDATE Menu set name = ?, description = ?, category = ?, price = ?, availability_status = ? WHERE menu_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, name.equals("") ? r.getString("name") : name);
                    preparedStatement.setString(2, desc.equals("") ? r.getString("description") : desc);
                    preparedStatement.setString(3, category.equals("") ? r.getString("category") : category);
                    preparedStatement.setBigDecimal(4, price.equals("") ? r.getBigDecimal("price") : new BigDecimal(price));
                    preparedStatement.setBoolean(5, status.equals("") ? r.getBoolean("availability_status") : Boolean.parseBoolean(status));
                    preparedStatement.setString(6, menu_id);
                    preparedStatement.executeUpdate();
                } else {
                    System.out.println("This menu item doesn't belong to your restaurant. You cannot update it.");
                }
            }
        }
    }

    public void insertMenu() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter restaurant registration number(it cannot be empty):");
        String res_id = sc.next();
        System.out.print("Enter menu name(it cannot be empty):");
        String name = sc.next();
        System.out.print("Enter description(press ENTER if you want to skip):");
        String desc = sc.nextLine().trim();
        System.out.print("Enter category(Veg, Non-veg or Vegan):");
        String category = sc.next();
        System.out.print("Enter price(it cannot be empty):");
        BigDecimal price = sc.nextBigDecimal();
        System.out.print("Enter availability status(0 or 1):");
        String status = sc.next();
        if(!Objects.equals(status, "0") && !status.equals("1")) {
            System.out.println("Invalid status");
            return;
        }

        String menu_id = res_id + name;

        String query = "INSERT INTO Menu (menu_id, restaurant_id, name, description, category, price, availability_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, menu_id);
        statement.setString(2, res_id);
        statement.setString(3, name);
        statement.setString(4, !desc.equals("") ? desc: null);
        statement.setString(5, category);
        statement.setBigDecimal(6, price);
        statement.setBoolean(7, Boolean.parseBoolean(status));
        statement.executeUpdate();
    }
}
