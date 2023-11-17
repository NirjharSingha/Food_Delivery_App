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
    delivery_address VARCHAR(255) NOT NULL,
    total_price DECIMAL(8, 2) NOT NULL,
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
DELIMITER //
CREATE FUNCTION generate_order_id(restaurant_id_param VARCHAR(100)) RETURNS VARCHAR(255)
DETERMINISTIC
BEGIN
    DECLARE next_order_no INT;
    SELECT COALESCE(MAX(SUBSTRING_INDEX(order_id, '_', -1)), 0) + 1
    INTO next_order_no
    FROM Orders
    WHERE restaurant_id = restaurant_id_param;
    RETURN CONCAT(restaurant_id_param, '_orderNo_', next_order_no);
END;
//
DELIMITER ;
DELIMITER //
CREATE PROCEDURE addNewOrder(
    IN user_email_param VARCHAR(100),
    IN delivery_agent_param VARCHAR(100),
    IN restaurant_id_param VARCHAR(100),
    IN order_date_param DATETIME,
    IN delivery_address_param VARCHAR(255),
    IN total_price_param DECIMAL(8, 2),
    IN delivery_time_param INT,
    IN delivery_fee_param DECIMAL(5, 2),
    OUT new_order_id_param VARCHAR(255)
)
BEGIN
	SET new_order_id_param = generate_order_id(restaurant_id_param);
    INSERT INTO Orders (
		order_id,
        user_email,
        delivery_agent,
        restaurant_id,
        order_date,
        delivery_address,
        total_price,
        delivery_time,
        delivery_fee
    )
    VALUES (
		new_order_id_param,
        user_email_param,
        delivery_agent_param,
        restaurant_id_param,
        order_date_param,
        delivery_address_param,
        total_price_param,
        delivery_time_param,
        delivery_fee_param
    );
END;
//
DELIMITER ;
DELIMITER //
CREATE TRIGGER after_insert_order
AFTER INSERT ON Orders
FOR EACH ROW
BEGIN
    INSERT INTO OrderStatus (order_id, payment_status)
    VALUES (NEW.order_id, 'Paid');
END;
//
DELIMITER ;