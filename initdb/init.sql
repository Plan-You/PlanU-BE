CREATE DATABASE IF NOT EXISTS planu;

CREATE TABLE IF NOT EXISTS `USER` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL,
    profileImgUrl VARCHAR(255),
    birthDate DATE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO `USER` (username, password, name, email, role, profileImgUrl, birthDate)
VALUES
    ('user', 'password', 'name', 'user@example.com', 'ROLE_USER', NULL, '1990-01-01');
