CREATE TABLE `spring_batch`.`orders` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `order_item` VARCHAR(45) NULL,
    `price` INT NULL,
    `order_date` DATE NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주문';

CREATE TABLE `spring_batch`.`accounts` (
     `id` INT NOT NULL AUTO_INCREMENT,
     `order_item` VARCHAR(45) NULL,
     `price` INT NULL,
     `order_date` DATE NULL,
     `account_date` DATE NULL,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='정산';
