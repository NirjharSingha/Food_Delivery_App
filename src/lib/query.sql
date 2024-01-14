-- function to generate order id
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

-- trigger to pay the bill of order
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

-- procedure to add new order
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

use Food_Delivery_App_DB;

-- Suggestions of food items to place order based on some filters like food name, restaurants, category, price, minimum rating etc. if applicable (Using aggregate function)
CREATE PROCEDURE filterForOrder(
    IN dishNameParam VARCHAR(100),
    IN resNameParam VARCHAR(100),
    IN priceParam VARCHAR(100),
    IN categoryParam VARCHAR(255),
    IN ratingParam VARCHAR(255)
)
WITH orders(
    menu_id,
    menu_name,
    category,
    price,
    availability_status,
    res_name,
    rating
) AS (
    SELECT m.menu_id,
        m.name AS menu_name,
        m.category,
        m.price,
        m.availability_status,
        r.name AS res_name,
        COALESCE(
            (
                SELECT AVG(rating)
                FROM Reviews
                WHERE rating IS NOT NULL
                    AND Reviews.menu_id = m.menu_id
            ),
            0
        ) AS rating
    FROM Menu AS m
        INNER JOIN Restaurants AS r ON m.restaurant_id = r.restaurant_id
)
SELECT *
FROM orders
WHERE (
        LOWER(menu_name) LIKE LOWER("%" + dishNameParam + "%")
        OR dishNameParam = ''
    )
    AND (
        LOWER(res_name) LIKE LOWER(resNameParam)
        OR resNameParam = ''
    )
    AND (
        price <= CAST(priceParam AS DECIMAL(5, 2))
        OR priceParam = ''
    )
    AND (
        LOWER(category) LIKE LOWER(categoryParam)
        OR categoryParam = ''
    )
    AND (
        rating >= CAST(ratingParam AS DECIMAL(3, 2))
        OR ratingParam = ''
    )
    AND availability_status = 1;

-- list restaurants based on filters like registration date, owner, address if applicable
CREATE PROCEDURE listResForAdmin(
    IN dateParam INT,
    IN resOwnParam VARCHAR(100),
    IN addressParam VARCHAR(100)
)
SELECT restaurant_id,
    name,
    res_owner,
    address,
    phone_number,
    registration_date
FROM Restaurants
WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL dateParam DAY)
    AND (
        resOwnParam = ''
        OR res_owner = resOwnParam
    )
    AND (
        addressParam = ''
        OR address = addressParam
    );

-- top x restaurants that got maximum orders in last t days(Using outer join and aggregate function):
CREATE PROCEDURE maxOrderResAdmin(
    IN dateParam INT,
    IN limitParam INT
)
SELECT r.restaurant_id,
    IFNULL(COUNT(o.order_id), 0) AS order_count
FROM Restaurants AS r
    LEFT JOIN Orders AS o ON r.restaurant_id = o.restaurant_id
WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL dateParam DAY)
    OR order_date IS NULL
GROUP BY restaurant_id
ORDER BY order_count DESC
LIMIT limitParam;

-- cancelled orders due to late delivery in last t days by each delivery agent(Using aggregate function)
CREATE PROCEDURE canceledLateOrdersAdmin(
    IN dateParam INT
)
SELECT O.delivery_agent AS delivery_agent_id,
    COUNT(*) AS canceled_late_orders
FROM Orders AS O
    JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE OS.cancellation_status IS NOT NULL
    AND TIMESTAMPDIFF(MINUTE, O.order_date, OS.cancellation_status) > O.delivery_time
    AND O.order_date >= DATE_SUB(CURDATE(), INTERVAL dateParam DAY)
GROUP BY O.delivery_agent
ORDER BY canceled_late_orders DESC;

-- users who registered in last t days
CREATE PROCEDURE listUsersAdmin(
    IN dateParam INT
)
SELECT email,
    username,
    phone_number,
    usertype,
    registration_date
FROM Users
WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL dateParam DAY);

-- pending orders of a delivery agent
CREATE PROCEDURE pendingOrdersDeliveryAgent(
    IN deliveryAgentParam VARCHAR(100)
)
SELECT O.order_id,
    O.restaurant_id,
    O.user_email,
    O.order_date,
    O.delivery_agent,
    O.delivery_time,
    OS.delivery_taken,
    OS.delivered_to_customer,
    OS.cancellation_status
FROM Orders AS O
    INNER JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE O.delivery_agent = deliveryAgentParam
    AND OS.delivered_to_customer IS NULL
    AND OS.cancellation_status IS NULL
ORDER BY O.order_date DESC;

-- pending orders of a user that are neither delivered nor cancelled
CREATE PROCEDURE pendingOrdersOfUser(
    IN userEmailParam VARCHAR(100)
)
SELECT DISTINCT O.order_id
FROM Orders AS O
    INNER JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE O.user_email = userEmailParam
    AND OS.delivered_to_customer IS NULL
    AND OS.cancellation_status IS NULL;

-- see the status of last x orders of a user
CREATE PROCEDURE lastXOrderStatusUser(
    IN countParam INT,
    IN userEmailParam VARCHAR(100)
)
SELECT O.order_id,
O.restaurant_id,
O.order_date,
O.total_price,
O.delivery_fee,
O.delivery_time,
OS.delivery_taken,
OS.delivered_to_customer,
OS.payment_status,
OS.cancellation_status
FROM Orders AS O
JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE O.user_email = userEmailParam
ORDER BY O.order_date DESC
LIMIT countParam;

-- see all orders of the restaurants of a restaurant owner
CREATE PROCEDURE allOrdersOfRestaurantsOfAOwner(
    IN resOwnerParamParam VARCHAR(100)
)
SELECT O.order_id,
O.restaurant_id,
O.user_email,
O.order_date,
O.total_price,
O.delivery_fee,
O.delivery_time,
OS.delivery_taken,
OS.delivered_to_customer,
OS.payment_status,
OS.cancellation_status
FROM Orders AS O
JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE O.restaurant_id IN (SELECT restaurant_id FROM Restaurants WHERE res_owner
= resOwnerParamParam)
ORDER BY O.order_date DESC;

-- see pending orders of the restaurants of a restaurant owner
CREATE PROCEDURE pendingOrdersOfRestaurantsOfAOwner(
    IN resOwnerParam VARCHAR(100)
)
SELECT O.order_id,
O.restaurant_id,
O.user_email,
O.order_date,
O.total_price,
O.delivery_fee,
O.delivery_time,
OS.delivery_taken,
OS.delivered_to_customer,
OS.payment_status,
OS.cancellation_status
FROM Orders AS O
JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE O.restaurant_id IN (SELECT restaurant_id FROM Restaurants WHERE res_owner
= resOwnerParam)
AND OS.delivered_to_customer IS NULL AND OS.cancellation_status IS NULL
ORDER BY O.order_date DESC;

-- see cancelled orders of the restaurants of a restaurant owner
CREATE PROCEDURE canceledOrdersOfRestaurantsOfAOwner(
    IN resOwnerParam VARCHAR(100)
)
SELECT O.order_id,
O.restaurant_id,
O.user_email,
O.order_date,
O.total_price,
O.delivery_fee,
O.delivery_time,
OS.delivery_taken,
OS.delivered_to_customer,
OS.payment_status,
OS.cancellation_status
FROM Orders AS O
JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE O.restaurant_id IN (SELECT restaurant_id FROM Restaurants WHERE res_owner
= resOwnerParam)
AND OS.cancellation_status IS NOT NULL
ORDER BY O.order_date DESC;

-- list of orders of a user for which review is not submitted (Using outer join)
CREATE PROCEDURE ordersWithoutReviewUser(
    IN userEmailParam VARCHAR(100)
)
SELECT DISTINCT O.order_id
FROM Orders AS O
LEFT JOIN Reviews AS R ON O.order_id = R.order_id
LEFT JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE O.user_email = userEmailParam
AND OS.delivered_to_customer IS NOT NULL
AND R.order_id IS NULL;

-- top x best or worst selling items in last t days based on choice of a restaurant (Using outer join and aggregate function)
CREATE PROCEDURE bestSellingItemRestaurant(
    IN countParam INT,
	IN dayParam INT,
    IN resIdParam VARCHAR(100)
)
SELECT M.menu_id,
M.name,
IFNULL(SUM(OD.quantity), 0) AS total_quantity
FROM Menu AS M
LEFT JOIN OrderDetails AS OD ON M.menu_id = OD.menu_id
LEFT JOIN Orders AS O ON OD.order_id = O.order_id
WHERE M.restaurant_id = resIdParam
AND (O.order_date >= DATE_SUB(CURDATE(), INTERVAL dayParam DAY) OR O.order_date
IS NULL)
GROUP BY M.menu_id,
M.name
ORDER BY total_quantity DESC
LIMIT countParam;

-- top x best or worst reviewed items in last t days based on choice of a restaurant (Using aggregate function)
CREATE PROCEDURE bestReviewedItemRestaurant(
    IN countParam INT,
	IN dayParam INT,
    IN resIdParam VARCHAR(100)
)
WITH ReviewedMenuItems(menu_id, name, average_rating) AS (
SELECT M.menu_id,
M.name,
AVG(R.rating) AS average_rating
FROM Menu AS M
JOIN Reviews AS R ON M.menu_id = R.menu_id
JOIN Orders AS O ON R.order_id = O.order_id
WHERE M.restaurant_id = resIdParam
AND R.rating IS NOT NULL
AND O.order_date >= DATE_SUB(CURDATE(), INTERVAL dayParam DAY)
GROUP BY M.menu_id,
M.name
HAVING COUNT(R.rating) > 0
)
SELECT *
FROM ReviewedMenuItems
ORDER BY average_rating DESC
LIMIT countParam;

-- customers who have ordered at least x times in last t days (Using aggregate function)
CREATE PROCEDURE repeatedCustomerRate(
    IN countParam INT,
	IN dayParam INT,
    IN resIdParam VARCHAR(100)
)
SELECT user_email, order_count
FROM (
SELECT user_email, COUNT(*) AS order_count
FROM Orders
WHERE restaurant_id = resIdParam
AND order_date >= DATE_SUB(CURDATE(), INTERVAL dayParam DAY)
GROUP BY user_email
HAVING order_count >= countParam
) AS repeated_customers;


-- id of the delivery agent who has minimum pending orders to deliver (Using aggregate function)
SELECT
    employee_id
FROM
    Employee
WHERE
    job = 'Delivery_Agent'
        AND availability_status = (SELECT
            MIN(availability_status)
        FROM
            Employee)
LIMIT 1;

-- see all orders as an admin
SELECT O.order_id,
O.restaurant_id,
O.user_email,
O.order_date,
O.total_price,
O.delivery_fee,
O.delivery_time,
OS.delivery_taken,
OS.delivered_to_customer,
OS.payment_status,
OS.cancellation_status
FROM Orders AS O
JOIN OrderStatus AS OS ON O.order_id = OS.order_id
ORDER BY O.order_date DESC;

-- see pending orders as an admin
SELECT O.order_id,
O.restaurant_id,
O.user_email,
O.order_date,
O.total_price,
O.delivery_fee,
O.delivery_time,
OS.delivery_taken,
OS.delivered_to_customer,
OS.payment_status,
OS.cancellation_status
FROM Orders AS O
JOIN OrderStatus AS OS ON O.order_id = OS.order_id WHERE
OS.delivered_to_customer IS NULL AND OS.cancellation_status
IS NULL
ORDER BY O.order_date DESC;

-- see canceled orders as an admin
SELECT O.order_id,
O.restaurant_id,
O.user_email,
O.order_date,
O.total_price,
O.delivery_fee,
O.delivery_time,
OS.delivery_taken,
OS.delivered_to_customer,
OS.payment_status,
OS.cancellation_status
FROM Orders AS O
JOIN OrderStatus AS OS ON O.order_id = OS.order_id WHERE
OS.cancellation_status IS NOT NULL
ORDER BY O.order_date DESC;

-- average time for delivering an order
SELECT
    AVG(TIMESTAMPDIFF(MINUTE,
        Orders.order_date,
        OrderStatus.delivered_to_customer)) / 60 AS avg_delivery_time
FROM
    Orders
        JOIN
    OrderStatus ON Orders.order_id = OrderStatus.order_id
WHERE
    OrderStatus.cancellation_status IS NULL
        AND OrderStatus.delivered_to_customer IS NOT NULL;

-- percentage of late deliveries (using aggregate function)
WITH temp_table AS (
  SELECT
    o.order_id,
    o.order_date,
    o.delivery_time,
    os.delivered_to_customer,
    os.cancellation_status
  FROM Orders AS o
  JOIN OrderStatus AS os ON o.order_id = os.order_id
  WHERE (os.cancellation_status IS NOT NULL
        AND TIMESTAMPDIFF(MINUTE, o.order_date, os.cancellation_status) > o.delivery_time) OR
        os.delivered_to_customer IS NOT NULL
        OR (os.delivered_to_customer IS NULL AND os.cancellation_status IS NULL)
)
SELECT
(SUM(
 (cancellation_status IS NULL AND delivered_to_customer IS NULL AND TIMESTAMPDIFF(MINUTE, order_date, CURDATE()) > delivery_time)
OR (delivered_to_customer IS NOT NULL AND cancellation_status IS NULL AND TIMESTAMPDIFF(MINUTE, order_date, delivered_to_customer) > delivery_time)
 OR (cancellation_status IS NOT NULL AND delivered_to_customer IS NULL AND TIMESTAMPDIFF(MINUTE, order_date, cancellation_status) > delivery_time)
) /
COUNT(*)
  ) * 100
  AS result
FROM temp_table;





-- calling procedures
CALL canceledLateOrdersAdmin(200);
CALL maxOrderResAdmin(1000, 100);
CALL listResForAdmin(1000,'','add1');
CALL filterForOrder('','res1','100','','3');
CALL listUsersAdmin(200);
CALL pendingOrdersDeliveryAgent('id_2');
CALL pendingOrdersOfUser('user1@gmail.com');
CALL lastXOrderStatusUser(200, 'user1@gmail.com');
CALL allOrdersOfRestaurantsOfAOwner('user1@gmail.com');
CALL pendingOrdersOfRestaurantsOfAOwner('user1@gmail.com');
CALL canceledOrdersOfRestaurantsOfAOwner('user1@gmail.com');
CALL ordersWithoutReviewUser('user1@gmail.com');
CALL bestSellingItemRestaurant(5, 200, '1');
CALL bestReviewedItemRestaurant(5, 200, '1');
CALL repeatedCustomerRate(2, 200, '5');