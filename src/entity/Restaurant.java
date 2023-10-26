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

    public Restaurant(String res_id, String name, String phone_number, String owner, String address, Timestamp registration_date) {
        this.res_id = res_id;
        this.name = name;
        this.phone_number = phone_number;
        this.owner = owner;
        this.address = address;
        this.registration_date = registration_date;
    }
}
