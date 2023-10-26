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

    public User(String email, String username, String password, String phone_number, String usertype, Timestamp registration_date) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone_number = phone_number;
        this.usertype = usertype;
        this.registration_date = registration_date;
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
