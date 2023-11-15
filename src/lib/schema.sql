CREATE DATABASE IF NOT EXISTS Food_Delivery_App_DB;
USE Food_Delivery_App_DB;
CREATE TABLE IF NOT EXISTS Users (
    email VARCHAR(100),
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15),
    usertype VARCHAR(100),
    registration_date DATETIME,
    PRIMARY KEY (email)
);
CREATE TABLE IF NOT EXISTS Restaurants (
    restaurant_id VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    res_owner VARCHAR(100),
    address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15),
    registration_date DATETIME,
    PRIMARY KEY (restaurant_id),
    FOREIGN KEY (res_owner) REFERENCES Users (email)
);
CREATE TABLE IF NOT EXISTS Menu (
    menu_id VARCHAR(100),
    restaurant_id VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL,
    price DECIMAL(5, 2) NOT NULL CHECK (price > 0),
    image LONGBLOB,
    availability_status BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY (menu_id),
    UNIQUE KEY unique_name_per_restaurant (restaurant_id, name),
    FOREIGN KEY (restaurant_id) REFERENCES Restaurants (restaurant_id)
);
CREATE TABLE IF NOT EXISTS Orders (
    order_id VARCHAR(255),
    user_email VARCHAR(100),
    delivery_agent VARCHAR(100),
    restaurant_id VARCHAR(100),
    order_date DATETIME,
    delivery_address VARCHAR(255),
    total_price DECIMAL(8, 2),
    delivery_time INT,
    delivery_fee DECIMAL(5, 2),
    PRIMARY KEY (order_id),
    FOREIGN KEY (user_email) REFERENCES Users (email),
    FOREIGN KEY (restaurant_id) REFERENCES Restaurants (restaurant_id),
    FOREIGN KEY (delivery_agent) REFERENCES Employee (employee_id)
);
CREATE TABLE IF NOT EXISTS OrderDetails (
    order_id VARCHAR(255),
    menu_id VARCHAR(100),
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (order_id, menu_id),
    FOREIGN KEY (order_id) REFERENCES Orders (order_id),
    FOREIGN KEY (menu_id) REFERENCES Menu (menu_id)
);
CREATE TABLE IF NOT EXISTS OrderStatus (
    order_id VARCHAR(255),
    delivery_taken DATETIME,
    delivered_to_customer DATETIME,
    payment_status VARCHAR(100),
    cancellation_status DATETIME,
    PRIMARY KEY (order_id),
    FOREIGN KEY (order_id) REFERENCES Orders (order_id)
);
CREATE TABLE IF NOT EXISTS Reviews (
    order_id VARCHAR(255),
    menu_id VARCHAR(100),
    rating DECIMAL(3, 2) CHECK (rating >= 0 AND rating <= 5),
    review_date DATETIME,
    PRIMARY KEY (order_id, menu_id),
    FOREIGN KEY (order_id) REFERENCES Orders (order_id),
    FOREIGN KEY (menu_id) REFERENCES Menu (menu_id)
);
CREATE TABLE IF NOT EXISTS Employee (
    employee_id VARCHAR(100),
    name VARCHAR(100) NOT NULL,
    login_pass VARCHAR(100) NOT NULL,
    job VARCHAR(100),
    phone_number VARCHAR(15),
    registration_date DATETIME,
    availability_status INT,
    PRIMARY KEY (employee_id)
);