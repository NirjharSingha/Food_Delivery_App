package utils;

import employee.AdminQuery;
import employee.EmployeeQuery;
import order.Order;
import order.OrderStatus;
import order.Review;
import res_owner.Owner_Query;
import res_owner.Restaurant_Activity;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Home {
    public void homePage() throws SQLException {
        while(true) {
            if(Objects.equals(Login.getUserType(), "Customer") || Objects.equals(Login.getUserType(), "Res_owner")) {
                System.out.println("Press 1 to place new order");
                System.out.println("Press 2 to submit review");
                System.out.println("Press 3 to cancel order");
                System.out.println("Press 4 to register a new restaurant");
            }
            System.out.println("Press 5 to update profile");

            if(Objects.equals(Login.getUserType(), "Res_owner")) {
                System.out.println("Press 6 to view and update your restaurant/s");
                System.out.println("Press 7 to see different menu items of your restaurant/s");
                System.out.println("Press 8 to update any menu item/s of your restaurant/s");
            }

            if(Objects.equals(Login.getUserType(), "Admin")) {
                System.out.println("Press 6 to register new employee");
                System.out.println("Press 7 to list the name of restaurants that got maximum orders");
                System.out.println("Press 8 to list the employee name for whom order cancelled because of late delivery");
                System.out.println("Press 9 to list all users");
            }

            if(Objects.equals(Login.getUserType(), "Customer") || Objects.equals(Login.getUserType(), "Res_owner")) {
                System.out.println("Press 9 to see to see the status of orders that you placed as a customer");
            }

            if(Objects.equals(Login.getUserType(), "Res_owner")) {
                System.out.println("Press 10 to see orders status of your restaurant/s");
            }

            if(Objects.equals(Login.getUserType(), "Admin")) {
                System.out.println("Press 10 to see orders status of different restaurant/s as an admin");
                System.out.println("Press 11 to list all restaurants");
            }

            if(Objects.equals(Login.getUserType(), "Delivery_Agent")) {
                System.out.println("Press 10 to see and update orders status of your pending orders");
            }

            System.out.println("Press 100 to exit");

            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            if (choice == 1) {
                if(Objects.equals(Login.getUserType(), "Customer") || Objects.equals(Login.getUserType(), "Res_owner")) {
                    Order order = new Order();
                    order.orderFilters();
                    order.placeOrder();
                }
            } else if (choice == 2) {
                if(Objects.equals(Login.getUserType(), "Customer") || Objects.equals(Login.getUserType(), "Res_owner")) {
                    Review review = new Review();
                    review.submitReview();
                }
            } else if (choice == 3) {
                if(Objects.equals(Login.getUserType(), "Customer") || Objects.equals(Login.getUserType(), "Res_owner")) {
                    Order order = new Order();
                    order.cancelOrder();
                }
            } else if (choice == 4) {
                if(Objects.equals(Login.getUserType(), "Customer") || Objects.equals(Login.getUserType(), "Res_owner")) {
                    Register register = new Register();
                    register.restaurantReg();
                }
            } else if (choice == 5) {
                UpdateProfile updateUser = new UpdateProfile();
                if(Objects.equals(Login.getUserType(), "Customer") || Objects.equals(Login.getUserType(), "Res_owner")) {
                    updateUser.updateProfile();
                } else {
                    updateUser.updateEmployee();
                }
            }else if (choice == 6) {
                if(Objects.equals(Login.getUserType(), "Admin")) {
                    Register register = new Register();
                    register.employeeReg();
                } else if(Objects.equals(Login.getUserType(), "Res_owner")) {
                    Owner_Query ownerQuery = new Owner_Query();
                    ownerQuery.queryMain();
                }
            } else if (choice == 7) {
                if(Objects.equals(Login.getUserType(), "Admin")) {
                    AdminQuery adminQuery = new AdminQuery();
                    adminQuery.maxOrderRes();
                } else if(Objects.equals(Login.getUserType(), "Res_owner")) {
                    Restaurant_Activity restaurantActivity = new Restaurant_Activity();
                    restaurantActivity.listMenu();
                }
            } else if (choice == 8) {
                if(Objects.equals(Login.getUserType(), "Admin")) {
                    AdminQuery adminQuery = new AdminQuery();
                    adminQuery.canceledLateOrders();
                } else if(Objects.equals(Login.getUserType(), "Res_owner")) {
                    Restaurant_Activity restaurantActivity = new Restaurant_Activity();
                    restaurantActivity.updateMenu();
                }
            } else if (choice == 9) {
                if(Objects.equals(Login.getUserType(), "Customer") || Objects.equals(Login.getUserType(), "Res_owner")) {
                    OrderStatus orderStatus = new OrderStatus();
                    orderStatus.customerQuery();
                } else if(Objects.equals(Login.getUserType(), "Admin")) {
                    AdminQuery adminQuery = new AdminQuery();
                    adminQuery.listUsers();
                }
            } else if (choice == 10) {
                if(Objects.equals(Login.getUserType(), "Admin")) {
                    OrderStatus orderStatus = new OrderStatus();
                    orderStatus.adminQuery();
                } else if(Objects.equals(Login.getUserType(), "Res_owner")) {
                    OrderStatus orderStatus = new OrderStatus();
                    orderStatus.ownerQuery();
                } else if(Objects.equals(Login.getUserType(), "Delivery_Agent")) {
                    EmployeeQuery employeeQuery = new EmployeeQuery();
                    employeeQuery.updateOrderStatus();
                }
            } else if (choice == 11) {
                if(Objects.equals(Login.getUserType(), "Admin")) {
                    AdminQuery adminQuery = new AdminQuery();
                    adminQuery.listRestaurants();
                }
            } else if(choice == 100) {
                break;
            }
        }
    }
}
