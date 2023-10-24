package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Login {

    private static String loggedINUser;
    private static String userType;

    public static String getUserType() {
        return userType;
    }

    public static void setUserType(String userType) {
        Login.userType = userType;
    }

    public static void setLoggedINUser(String loggedINUser) {
        Login.loggedINUser = loggedINUser;
    }

    public static String getLoggedINUser() {
        return loggedINUser;
    }

    public void userLogin() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection con = db.getCon();

        System.out.println("Enter your email");
        Scanner sc = new Scanner(System.in);
        String email = sc.next();

        String query = "SELECT password, usertype FROM Users WHERE email = ?";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, email);

        ResultSet r = statement.executeQuery();
        if (r.next()) {
            System.out.println("Enter your password");
            String password = sc.next();
            if(Objects.equals(password, r.getString("password"))) {
                System.out.println("You've logged in successfully");
                Login.setLoggedINUser(email);
                Login.setUserType(r.getString("usertype"));
            } else {
                System.out.println("invalid password");
            }
        } else {
            System.out.println("Invalid email. Register first");
        }
    }

    public void employeeLogin() throws SQLException {
        ConnectDatabase db = new ConnectDatabase();
        Connection con = db.getCon();

        System.out.println("Enter your employee id");
        Scanner sc = new Scanner(System.in);
        String id = sc.next();

        String query = "SELECT password, job FROM Employee WHERE employee_id = ?";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, id);

        ResultSet r = statement.executeQuery();
        if (r.next()) {
            System.out.println("Enter your password");
            String password = sc.next();
            if(Objects.equals(password, r.getString("password"))) {
                System.out.println("You've logged in successfully");
                Login.setLoggedINUser(id);
                Login.setUserType(r.getString("job"));
            } else {
                System.out.println("invalid password");
            }
        } else {
            System.out.println("Invalid id. Register first");
        }
    }
}
