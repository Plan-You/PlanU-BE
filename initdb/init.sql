CREATE DATABASE IF NOT EXISTS planu;

CREATE TABLE IF NOT EXISTS `USER`
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)                      NOT NULL,
    password      VARCHAR(255)                     NOT NULL,
    name          VARCHAR(100)                     NOT NULL,
    gender        ENUM('M','F')                    NOT NULL,
    email         VARCHAR(100)                     NOT NULL,
    role          ENUM ('ROLE_USER', 'ROLE_ADMIN'),
    profileImgUrl VARCHAR(255),
    birthDate     DATE,
    createdAt     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `AVAILABLE_DATE`
(
    USER_ID      BIGINT NOT NULL,
    DATE         DATE   NOT NULL,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (USER_ID) REFERENCES USER (ID)
);

CREATE TABLE IF NOT EXISTS `USER_LOCATION`
(
    USER_ID      BIGINT NOT NULL,
    LATITUDE     DECIMAL(10, 6),
    LONGITUDE    DECIMAL(10, 6),
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (USER_ID) REFERENCES USER (ID)
);

CREATE TABLE IF NOT EXISTS `FRIEND`
(
    TO_USER_ID    BIGINT,
    FROM_USER_ID  BIGINT,
    FRIEND_STATUS TINYINT(1) DEFAULT NULL,

    CREATED_DATE  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (TO_USER_ID) REFERENCES USER (ID),
    FOREIGN KEY (FROM_USER_ID) REFERENCES USER (ID)
);

CREATE TABLE IF NOT EXISTS `NOTIFICATION`
(
    RECEIVER_ID  BIGINT       NOT NULL,
    TITLE        VARCHAR(100) NOT NULL,
    MESSAGE      VARCHAR(255),
    NOT_READ     TINYINT(1) DEFAULT 0,
    SOURCE       VARCHAR(255) NOT NULL,
    CREATED_DATE TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (RECEIVER_ID) REFERENCES USER (ID)
);

CREATE TABLE IF NOT EXISTS `SCHEDULE`
(
    ID             BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID        BIGINT       NOT NULL,
    TITLE          VARCHAR(100) NOT NULL,
    START_DATETIME DATETIME     NOT NULL,
    END_DATETIME   DATETIME     NOT NULL,
    COLOR          VARCHAR(20),
    SCHEDULE_VISIBILITY ENUM('PUBLIC', 'PRIVATE') NOT NULL DEFAULT 'PRIVATE',
    MEMO           TEXT,
    LOCATION       VARCHAR(255),
    CREATED_DATE   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (USER_ID) REFERENCES USER (id)
);

CREATE TABLE IF NOT EXISTS `SCHEDULE_PARTICIPANT`
(
    SCHEDULE_ID  BIGINT,
    USER_ID      BIGINT,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (SCHEDULE_ID) REFERENCES SCHEDULE (ID),
    FOREIGN KEY (USER_ID) REFERENCES USER (id)
);

CREATE TABLE IF NOT EXISTS `GROUP_`
(
    ID           BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME         VARCHAR(50)    NOT NULL,
    PROFILE      VARCHAR(255),
    CREATED_DATE TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `GROUP_USER`
(
    USER_ID      BIGINT NOT NULL,
    GROUP_ID     BIGINT NOT NULL,
    GROUP_ROLE   ENUM ('LEADER', 'PARTICIPANT') DEFAULT 'PARTICIPANT',
    GROUP_STATE  TINYINT(1)                     DEFAULT NULL,
    CREATED_DATE TIMESTAMP                      DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP                      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (USER_ID) REFERENCES USER (id),
    FOREIGN KEY (GROUP_ID) REFERENCES GROUP_ (ID)
);

CREATE TABLE IF NOT EXISTS `CHAT_MESSAGE`
(
    ID           BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID      BIGINT NOT NULL,
    GROUP_ID     BIGINT NOT NULL,
    NOT_READ     INT(3) UNSIGNED,
    MESSAGE      TEXT,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (USER_ID) REFERENCES USER (id),
    FOREIGN KEY (GROUP_ID) REFERENCES GROUP_ (ID)
);

CREATE TABLE IF NOT EXISTS `GROUP_SCHEDULE`
(
    ID             BIGINT AUTO_INCREMENT PRIMARY KEY,
    GROUP_ID       BIGINT,
    TITLE          VARCHAR(100) NOT NULL,
    START_DATETIME DATETIME     NOT NULL,
    END_DATETIME   DATETIME     NOT NULL,
    COLOR          VARCHAR(20),
    SCHEDULE_VISIBILITY ENUM('PUBLIC', 'PRIVATE') NOT NULL DEFAULT 'PRIVATE',
    MEMO           TEXT,
    LOCATION       VARCHAR(255),
    CREATED_DATE   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (GROUP_ID) REFERENCES GROUP_ (ID)
);

CREATE TABLE IF NOT EXISTS `GROUP_SCHEDULE_PARTICIPANT`
(
    USER_ID           BIGINT,
    GROUP_ID          BIGINT,
    GROUP_SCHEDULE_ID BIGINT,
    CREATED_DATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (USER_ID) REFERENCES GROUP_USER (USER_ID),
    FOREIGN KEY (GROUP_ID) REFERENCES GROUP_USER(GROUP_ID),
    FOREIGN KEY (GROUP_SCHEDULE_ID) REFERENCES GROUP_SCHEDULE (ID)
);

CREATE TABLE IF NOT EXISTS `GROUP_SCHEDULE_COMMENT`
(
    USER_ID           BIGINT NOT NULL,
    GROUP_ID          BIGINT NOT NULL,
    GROUP_SCHEDULE_ID BIGINT NOT NULL,
    MESSAGE           TEXT   NOT NULL,
    LIKES             INT(10)   DEFAULT 0,
    UNLIKES           INT(10)   DEFAULT 0,
    CREATED_DATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (USER_ID) REFERENCES GROUP_USER (USER_ID),
    FOREIGN KEY (GROUP_ID) REFERENCES GROUP_USER(GROUP_ID),
    FOREIGN KEY (GROUP_SCHEDULE_ID) REFERENCES GROUP_SCHEDULE (ID)
);
