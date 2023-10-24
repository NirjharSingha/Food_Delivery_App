package utils;

import entity.Restaurant;
import entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Register {
    public void userReg() {
        User user = new User();

        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        String query = "SELECT email FROM Users WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,user.getEmail());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                System.out.println("Duplicate email");
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        String insertQuery = "INSERT INTO Users (email, username, password, phone_number, usertype, registration_date) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getPhone_number());
            preparedStatement.setString(5, user.getUsertype());
            preparedStatement.setTimestamp(6, user.getRegistration_date());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void restaurantReg() {
        Restaurant restaurant = new Restaurant();

        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        String query = "SELECT restaurant_id FROM Restaurants WHERE restaurant_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,restaurant.getRes_id());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                System.out.println("Duplicate restaurant registration number");
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        String insertQuery = "INSERT INTO Restaurants (restaurant_id, name, address, phone_number, res_owner, registration_date) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, restaurant.getRes_id());
            preparedStatement.setString(2, restaurant.getName());
            preparedStatement.setString(3, restaurant.getAddress());
            preparedStatement.setString(4, restaurant.getPhone_number());
            preparedStatement.setString(5, restaurant.getOwner());
            preparedStatement.setTimestamp(6, restaurant.getRegistration_date());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
