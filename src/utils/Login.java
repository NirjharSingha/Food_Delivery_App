package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Login {
    public void handleLogin() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection con = db.getCon();

        System.out.println("Enter your email");
        Scanner sc = new Scanner(System.in);
        String email = sc.next();

        String query = "SELECT password FROM Users WHERE email = ?";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, email);

        ResultSet r = statement.executeQuery();
        if (r.next()) {
            System.out.println("Enter your password");
            String password = sc.next();
            if(Objects.equals(password, r.getString("password"))) {
                System.out.println("You've logged in successfully");
            } else {
                System.out.println("invalid password");
            }
        } else {
            System.out.println("Invalid email. utils.Register first");
        }
    }
}
