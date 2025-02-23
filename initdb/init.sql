CREATE DATABASE IF NOT EXISTS planu;

USE planu;

CREATE TABLE IF NOT EXISTS `USER`
(
    ID            BIGINT AUTO_INCREMENT PRIMARY KEY,
    USERNAME      VARCHAR(50)                      NOT NULL,
    PASSWORD      VARCHAR(255)                     NOT NULL,
    NAME          VARCHAR(100)                     NOT NULL,
    GENDER        VARCHAR(50),
    EMAIL         VARCHAR(100)                     NOT NULL,
    ROLE          ENUM ('ROLE_USER', 'ROLE_ADMIN','ROLE_GUEST'),
    PROFILE_IMG_URL VARCHAR(255),
    BIRTH_DATE     DATE,
    PROFILE_STATUS VARCHAR(10) DEFAULT 'INCOMPLETE',
    CREATED_DATE     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS USER_TERMS (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID BIGINT NOT NULL,
    IS_PRIVACY_POLICY_AGREED VARCHAR(10) NOT NULL,
    IS_TERMS_OF_SERVICE_AGREED VARCHAR(10) NOT NULL,
    IS_SNS_RECEIVE_AGREED VARCHAR(10) DEFAULT 0,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (USER_ID) REFERENCES USER(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS AVAILABLE_DATE (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID BIGINT NOT NULL,
    POSSIBLE_DATE DATE NOT NULL,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (USER_ID) REFERENCES USER(ID)
    ON DELETE CASCADE
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
    FRIEND_STATUS VARCHAR(10) DEFAULT NULL,

    CREATED_DATE  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (TO_USER_ID) REFERENCES USER (ID),
    FOREIGN KEY (FROM_USER_ID) REFERENCES USER (ID)
);

CREATE TABLE IF NOT EXISTS `NOTIFICATION`
(
    ID           BIGINT AUTO_INCREMENT PRIMARY KEY,
    SENDER_ID    BIGINT       NOT NULL,
    RECEIVER_ID  BIGINT       NOT NULL,
    EVENT_TYPE   VARCHAR(100) NOT NULL,
    CONTENT      VARCHAR(255),
    IS_READ     TINYINT(1) DEFAULT 0,
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
    LATITUDE       DECIMAL(10, 6),
    LONGITUDE      DECIMAL(10, 6),
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

    FOREIGN KEY (SCHEDULE_ID) REFERENCES SCHEDULE (ID) ON DELETE CASCADE,  -- CASCADE 추가
    FOREIGN KEY (USER_ID) REFERENCES USER (id)
);

CREATE TABLE IF NOT EXISTS `UNREGISTERED_PARTICIPANT`
(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    SCHEDULE_ID  BIGINT,
    PARTICIPANT_NAME VARCHAR(255) NOT NULL,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (SCHEDULE_ID) REFERENCES SCHEDULE (ID) ON DELETE CASCADE  -- CASCADE 추가
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
    GROUP_PIN    TIMESTAMP                      DEFAULT NULL,
    GROUP_IS_PIN BOOLEAN                        DEFAULT FALSE,
    VERSION      INT                            DEFAULT 0,
    CREATED_DATE TIMESTAMP                      DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP                      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (USER_ID, GROUP_ID),  -- USER_ID와 GROUP_ID를 기본키로 설정
    FOREIGN KEY (USER_ID) REFERENCES USER (id),
    FOREIGN KEY (GROUP_ID) REFERENCES `GROUP_` (ID)
);


CREATE TABLE IF NOT EXISTS `CHAT_MESSAGE`
(
    ID           BIGINT AUTO_INCREMENT PRIMARY KEY,
    USER_ID      BIGINT NOT NULL,
    GROUP_ID     BIGINT NOT NULL,
    MESSAGE      TEXT,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (USER_ID) REFERENCES USER (id),
    FOREIGN KEY (GROUP_ID) REFERENCES GROUP_ (ID)
);

CREATE TABLE IF NOT EXISTS `MESSAGE_STATUS`
(
    ID          BIGINT AUTO_INCREMENT PRIMARY KEY,
    MESSAGE_ID  BIGINT NOT NULL,
    USER_ID     BIGINT NOT NULL,
    IS_READ   BOOLEAN DEFAULT FALSE,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (MESSAGE_ID) REFERENCES CHAT_MESSAGE (ID),
    FOREIGN KEY (USER_ID) REFERENCES USER (ID)
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
    LATITUDE       DECIMAL(10, 6),
    LONGITUDE      DECIMAL(10, 6),
    CREATED_DATE   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (GROUP_ID) REFERENCES `GROUP_` (ID)
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
    ID                BIGINT AUTO_INCREMENT PRIMARY KEY,
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