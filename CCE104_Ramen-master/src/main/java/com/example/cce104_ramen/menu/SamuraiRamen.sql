-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 16, 2024 at 08:45 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

-- phpMyAdmin SQL Dump
-- version 5.2.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- Database: `samurairamen`

-- --------------------------------------------------------

-- Table structure for table `beverages`
CREATE TABLE `beverages` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `name` varchar(100) NOT NULL,
                             `price` decimal(10,2) NOT NULL,
                             `quantity` int(11) NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `beverages` (`id`, `name`, `price`, `quantity`) VALUES
                                                                (1, 'Matcha Latte', 90.00, 30),
                                                                (2, 'Bubble Tea', 110.00, 20),
                                                                (3, 'Coca-Cola', 50.00, 20),
                                                                (4, 'Pepsi', 50.00, 30),
                                                                (5, 'Sake', 170.00, 23),
                                                                (6, 'Sprite', 50.00, 23),
                                                                (7, 'Iced Tea', 50.00, 23),
                                                                (8, 'Coke Zero', 60.00, 23);

-- --------------------------------------------------------

-- Table structure for table `desserts`
CREATE TABLE `desserts` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `name` varchar(100) NOT NULL,
                            `price` decimal(10,2) NOT NULL,
                            `quantity` int(11) NOT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `desserts` (`id`, `name`, `price`, `quantity`) VALUES
                                                               (1, 'Matcha Ice Cream', 140.00, 30),
                                                               (2, 'Matcha Tiramisu', 140.00, 20),
                                                               (3, 'Mochi', 120.00, 50),
                                                               (4, 'Dorayaki', 140.00, 40),
                                                               (5, 'Yokan', 140.00, 25),
                                                               (6, 'Warabi', 140.00, 10);

-- --------------------------------------------------------

-- Table structure for table `meals`
CREATE TABLE `meals` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `name` varchar(100) NOT NULL,
                         `price` decimal(10,2) NOT NULL,
                         `quantity` int(11) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `meals` (`id`, `name`, `price`, `quantity`) VALUES
                                                            (1, 'Samurai Sushi Set', 160.00, 30),
                                                            (2, 'Teriyaki Rice Bowl', 160.00, 20),
                                                            (3, 'Chicken Katsu Curry', 170.00, 20),
                                                            (4, 'Beef Gyudon', 160.00, 30),
                                                            (5, 'Samurai Salmon Special', 180.00, 23),
                                                            (6, 'Unagi Donburi', 170.00, 24);

-- --------------------------------------------------------

-- Table structure for table `ramen`
CREATE TABLE `ramen` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `name` varchar(100) NOT NULL,
                         `price` decimal(10,2) NOT NULL,
                         `quantity` int(11) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `ramen` (`id`, `name`, `price`, `quantity`) VALUES
                                                            (1, 'Tonkotsu Ramen', 160.00, 30),
                                                            (2, 'Miso Ramen', 160.00, 20),
                                                            (3, 'Shoyu Ramen', 160.00, 20),
                                                            (4, 'Spicy Kimchi Ramen', 180.00, 30),
                                                            (5, 'Vegan Ramen', 170.00, 23);

-- --------------------------------------------------------

-- Table structure for table `orders`
CREATE TABLE `orders` (
                          `Order_ID` int(11) NOT NULL AUTO_INCREMENT,
                          PRIMARY KEY (`Order_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- New table to normalize ordered items
CREATE TABLE `order_items` (
                               `OrderItem_ID` int(11) NOT NULL AUTO_INCREMENT,
                               `Order_ID` int(11) NOT NULL,
                               `item_name` varchar(100) NOT NULL,
                               `category` ENUM('ramen', 'beverages', 'meals', 'desserts') NOT NULL,
                               `price` decimal(10,2) NOT NULL,
                               FOREIGN KEY (`Order_ID`) REFERENCES `orders`(`Order_ID`) ON DELETE CASCADE,
                               PRIMARY KEY (`OrderItem_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Example of normalized data
INSERT INTO `orders` (`Order_ID`) VALUES (1), (2), (3);
INSERT INTO `order_items` (`Order_ID`, `item_name`, `category`, `price`) VALUES
                                                                             (1, 'Tonkotsu Ramen', 'ramen', 160.00),
                                                                             (1, 'Bubble Tea', 'beverages', 110.00),
                                                                             (2, 'Samurai Sushi Set', 'meals', 160.00),
                                                                             (3, 'Matcha Ice Cream', 'desserts', 140.00);

-- --------------------------------------------------------

-- Table structure for table `transactions`
CREATE TABLE `transactions` (
                                `Transaction_ID` int(11) NOT NULL AUTO_INCREMENT,
                                `Order_ID` int(11) NOT NULL,
                                `TotalAmount` int(11) DEFAULT NULL,
                                PRIMARY KEY (`Transaction_ID`),
                                FOREIGN KEY (`Order_ID`) REFERENCES `orders` (`Order_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `transactions` (`Transaction_ID`, `Order_ID`, `TotalAmount`) VALUES
                                                                             (1, 1, 270),
                                                                             (2, 2, 160),
                                                                             (3, 3, 140);

COMMIT;