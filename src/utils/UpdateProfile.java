package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateProfile {
    public void updateProfile() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the updated value of each field. If you don't want to update a field then just press ENTER");
        System.out.print("Enter updated name:");
        String name = sc.nextLine().trim();
        System.out.print("Enter updated password:");
        String pass = sc.nextLine().trim();
        System.out.print("Enter updated phone number:");
        String phone = sc.nextLine().trim();

        String query = "SELECT  username, password, phone_number FROM Users WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        if(r.next()) {
            String updateQuery = "UPDATE Users set username = ?, password = ?, phone_number = ? WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, name.equals("") ? r.getString("username") : name);
            preparedStatement.setString(2, pass.equals("") ? r.getString("password") : pass);
            preparedStatement.setString(3, phone.equals("") ? r.getString("phone_number") : phone);
            preparedStatement.setString(4, Login.getLoggedINUser());
            preparedStatement.executeUpdate();

            System.out.println("Profile updated successfully");
        }
    }
    public void updateEmployee() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection connection = db.getCon();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the updated value of each field. If you don't want to update a field then just press ENTER");
        System.out.print("Enter updated name:");
        String name = sc.nextLine().trim();
        System.out.print("Enter updated password:");
        String pass = sc.nextLine().trim();
        System.out.print("Enter updated phone number:");
        String phone = sc.nextLine().trim();

        String query = "SELECT  name, login_pass, phone_number FROM Employee WHERE employee_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, Login.getLoggedINUser());
        ResultSet r = statement.executeQuery();
        if(r.next()) {
            String updateQuery = "UPDATE Employee set name = ?, login_pass = ?, phone_number = ? WHERE employee_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, name.equals("") ? r.getString("name") : name);
            preparedStatement.setString(2, pass.equals("") ? r.getString("login_pass") : pass);
            preparedStatement.setString(3, phone.equals("") ? r.getString("phone_number") : phone);
            preparedStatement.setString(4, Login.getLoggedINUser());
            preparedStatement.executeUpdate();

            System.out.println("Profile updated successfully");
        }
    }
}
