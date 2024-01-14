DELIMITER // CREATE PROCEDURE filterForOrder(
    IN dishNameParam VARCHAR(100),
    IN resNameParam VARCHAR(100),
    IN priceParam VARCHAR(100),
    IN categoryParam VARCHAR(255),
    IN ratingParam VARCHAR(255),
) BEGIN WITH orders(
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
        LOWER(menu_name) LIKE LOWER(% dishNameParam %)
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
END;
// DELIMITER;
SELECT restaurant_id,
    name,
    res_owner,
    address,
    phone_number,
    registration_date
FROM Restaurants
WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
    AND (
        ? = ''
        OR res_owner = ?
    )
    AND (
        ? = ''
        OR address = ?
    );
-- max order res admin
SELECT r.restaurant_id,
    IFNULL(COUNT(o.order_id), 0) AS order_count
FROM Restaurants AS r
    LEFT JOIN Orders AS o ON r.restaurant_id = o.restaurant_id
WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
    OR order_date IS NULL
GROUP BY restaurant_id
ORDER BY order_count DESC
LIMIT ?;
-- canceledLate orders admin
SELECT O.delivery_agent AS delivery_agent_id,
    COUNT(*) AS canceled_late_orders
FROM Orders AS O
    JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE OS.cancellation_status IS NOT NULL
    AND TIMESTAMPDIFF(MINUTE, O.order_date, OS.cancellation_status) > O.delivery_time
    AND O.order_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
GROUP BY O.delivery_agent
ORDER BY canceled_late_orders DESC;
-- list users admin
SELECT email,
    username,
    phone_number,
    usertype,
    registration_date
FROM Users
WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY);
-- pending orders of delivery agent
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
WHERE O.delivery_agent = ?
    AND OS.delivered_to_customer IS NULL
    AND OS.cancellation_status IS NULL
ORDER BY O.order_date DESC;
-- deliveryAgentWithMinimumOrders
SELECT employee_id
FROM Employee
WHERE job = 'Delivery_Agent'
    AND availability_status = (
        SELECT MIN(availability_status)
        FROM Employee
    )
LIMIT 1;
-- cancelableOrdersOfUser
SELECT DISTINCT O.order_id
FROM Orders AS O
    INNER JOIN OrderStatus AS OS ON O.order_id = OS.order_id
WHERE O.user_email = ?
    AND OS.delivered_to_customer IS NULL
    AND OS.cancellation_status IS NULL;
-- updateDeliveyCount
UPDATE Employee
SET availability_status = availability_status - 1
WHERE employee_id = (
        SELECT delivery_agent
        FROM Orders
        WHERE order_id = ?
    );