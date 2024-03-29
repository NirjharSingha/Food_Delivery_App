import utils.Login;
import utils.Register;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to Food Delivery App");
        Scanner sc = new Scanner(System.in);
        System.out.println("Press 0 if you want to register");
        System.out.println("Press 1 if you want to log in");
        int choice = sc.nextInt();
        if (choice == 0) {
            Register register = new Register();
            register.userReg();
        } else if (choice == 1) {
            System.out.println("Press 1 to log in as a user");
            System.out.println("Press 2 to log in as an employee");
            int log = sc.nextInt();
            if(log != 1 && log != 2) {
                System.out.println("Invalid input");
            } else if(log == 1) {
                Login login = new Login();
                login.userLogin();
            } else {
                Login login = new Login();
                login.employeeLogin();
            }
        } else {
            System.out.println("invalid input");
        }
        sc.close();
    }
    // https://docs.google.com/drawings/d/1tiLkGjBgK1FTO4OqxL-3fOgUuKZyWR40J6K_h0ENL38/edit
}