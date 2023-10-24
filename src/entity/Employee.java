package entity;

import java.sql.Timestamp;
import java.util.Scanner;

public class Employee {
    private String id;
    private String name;
    private String password;
    private String phone_number;
    private String job;
    private Timestamp registration_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Timestamp getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Timestamp registration_date) {
        this.registration_date = registration_date;
    }

    public Employee() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your email");
        id = sc.next();
        System.out.println("Enter your name");
        name = sc.next();
        System.out.println("Enter your password");
        password = sc.next();
        System.out.println("Enter your phone number");
        phone_number = sc.next();
        job = "Delivery_Agent";
        registration_date = new Timestamp(System.currentTimeMillis());
    }
}
