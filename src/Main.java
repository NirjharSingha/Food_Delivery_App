import utils.Login;
import utils.Register;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to Food Delivery App");
        Scanner sc = new Scanner(System.in);

        while(true) {
            System.out.println("Press 0 if you want to register");
            System.out.println("Press 1 if you want to log in");
            System.out.println("Press 2 if you want to exit");
            int choice = sc.nextInt();
            if (choice == 0) {
                Register register = new Register();
                register.userReg();
            } else if (choice == 1) {
                Login login = new Login();
                login.handleLogin();
                Register register = new Register();
                register.restaurantReg();
            } else if(choice == 2) {
                break;
            } else {
                System.out.println("invalid input");
            }
        }
        sc.close();
    }
}