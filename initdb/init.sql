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
    ROLE          ENUM ('ROLE_USER', 'ROLE_ADMIN'),
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
    LATITUDE       VARCHAR(255),
    LONGITUDE      VARCHAR(255),
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
    LATITUDE       VARCHAR(255),
    LONGITUDE      VARCHAR(255),
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

CREATE TABLE IF NOT EXISTS `GROUP_SCHEDULE_UNREGISTERED_PARTICIPANT`
(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    GROUP_SCHEDULE_ID  BIGINT,
    PARTICIPANT_NAME VARCHAR(255) NOT NULL,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (GROUP_SCHEDULE_ID) REFERENCES GROUP_SCHEDULE (ID) ON DELETE CASCADE  -- CASCADE 추가
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
