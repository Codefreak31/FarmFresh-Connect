For this project you need to create sql tables this is how it is done:

first login to sql using your details from powershell and then:

mysql>create database user_db;
mysql>use user_db;
--for user table creation:
mysql> CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    phone VARCHAR(15),
    address VARCHAR(255),
    total_amount DECIMAL(10, 2),
    order_items TEXT
);

--for farmer table creation:

CREATE TABLE farmers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    phone_number VARCHAR(15),
    address VARCHAR(255),
    user_id INT
);

--for completed order:

CREATE TABLE completed_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    farmer_id INT,
    order_status VARCHAR(50)
);

--for delivery boys

CREATE TABLE delivery_boys (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50)
);


and how to run my project?
for the customer app:
javac -cp ".;mysql-connector-j-9.1.0.jar" MainApp.java
java -cp ".;mysql-connector-j-9.1.0.jar" MainApp

for the farmer app:
javac -cp ".;mysql-connector-j-9.1.0.jar" FarmerApp.java
java -cp ".;mysql-connector-j-9.1.0.jar" FarmerApp

for the delivery boy app:
javac -cp ".;mysql-connector-j-9.1.0.jar" DeliveryBoyApp.java
java -cp ".;mysql-connector-j-9.1.0.jar" DeliveryBoyApp

Make sure to have the MySQL connector jar file in the same directory of your project else you may get errors..
also make sure to change the localhost address and database address for the sql in java project.

