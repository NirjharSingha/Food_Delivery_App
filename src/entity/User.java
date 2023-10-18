package entity;

import java.sql.Timestamp;
import java.util.Scanner;

public class User {
    private String email;
    private String username;
    private String password;
    private String phone_number;
    private String usertype;
    private Timestamp registration_date;

    public User() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your email");
        email = sc.next();
        System.out.println("Enter your name");
        username = sc.next();
        System.out.println("Enter your password");
        password = sc.next();
        System.out.println("Enter your phone number");
        phone_number = sc.next();
        usertype = "Customer";
        registration_date = new Timestamp(System.currentTimeMillis());
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getUsertype() {
        return usertype;
    }

    public Timestamp getRegistration_date() {
        return registration_date;
    }
}
