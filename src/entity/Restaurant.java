package entity;

import utils.Login;

import java.sql.Timestamp;
import java.util.Scanner;

public class Restaurant {
    private String res_id;
    private String name;
    private String phone_number;
    private String owner;
    private String address;
    private Timestamp registration_date;

    public String getRes_id() {
        return res_id;
    }

    public String getName() {
        return name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getOwner() {
        return owner;
    }

    public String getAddress() {
        return address;
    }

    public Timestamp getRegistration_date() {
        return registration_date;
    }

    public Restaurant() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your restaurant registration number");
        res_id = sc.next();
        System.out.println("Enter your restaurant name");
        name = sc.next();
        System.out.println("Enter your phone number");
        phone_number = sc.next();
        System.out.println("Enter restaurant address");
        address = sc.next();
        owner = Login.getLoggedINUser();
        registration_date = new Timestamp(System.currentTimeMillis());
    }
}
