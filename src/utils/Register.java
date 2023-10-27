package utils;

import entity.Employee;
import entity.Restaurant;
import entity.User;

import java.sql.*;
import java.util.Scanner;

public class Register {
    public void userReg() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your email");
        String email = sc.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter your name");
        String username = sc.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter your password");
        String password = sc.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter your phone number");
        String phone_number = sc.next();
        String usertype = "Customer";
        Timestamp registration_date = new Timestamp(System.currentTimeMillis());
        User user = new User(email, username, password, phone_number, usertype, registration_date);

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
            System.out.println("You've registered successfully");
            Login.setLoggedINUser(user.getEmail());
            Login.setUserType(user.getUsertype());

            Home home = new Home();
            home.homePage();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void restaurantReg() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your restaurant registration number");
        String res_id = sc.nextLine().trim();
        if (res_id.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter your restaurant name");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter your restaurant contact number");
        String phone_number = sc.nextLine().trim();
        if (phone_number.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter restaurant address");
        String address = sc.nextLine().trim();
        if (address.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        String owner = Login.getLoggedINUser();
        Timestamp registration_date = new Timestamp(System.currentTimeMillis());
        Restaurant restaurant = new Restaurant(res_id, name, phone_number, owner, address, registration_date);

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

            String updateQuery = "UPDATE Users set usertype = ? WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1,"Res_owner");
            statement.setString(2, Login.getLoggedINUser());
            statement.executeUpdate();

            System.out.println("Registration successful");
            Login.setUserType("Res_owner");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void employeeReg() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your employee id");
        String id = sc.nextLine();
        if (id.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter your name");
        String name = sc.nextLine();
        if (name.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter your password");
        String password = sc.nextLine();
        if (password.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        System.out.println("Enter your phone number");
        String phone_number = sc.next();
        if (phone_number.isEmpty()) {
            System.out.println("This input should not be empty.");
            return;
        }
        String job = "Delivery_Agent";
        Timestamp registration_date = new Timestamp(System.currentTimeMillis());
        Employee employee = new Employee(id, name, password, phone_number, job, registration_date);

        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        String query = "SELECT employee_id FROM Employee WHERE employee_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, employee.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                System.out.println("Duplicate id");
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        String insertQuery = "INSERT INTO Employee (employee_id, name, login_pass, phone_number, job, registration_date, availability_status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, employee.getId());
            preparedStatement.setString(2, employee.getName());
            preparedStatement.setString(3, employee.getPassword());
            preparedStatement.setString(4, employee.getPhone_number());
            preparedStatement.setString(5, employee.getJob());
            preparedStatement.setTimestamp(6, employee.getRegistration_date());
            preparedStatement.setInt(7, 0);

            preparedStatement.executeUpdate();
            System.out.println("Registration successful");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
