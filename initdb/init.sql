CREATE TABLE USER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- 고유 사용자 ID, 자동 증가
    username VARCHAR(50) NOT NULL,           -- 사용자 이름
    password VARCHAR(255) NOT NULL,          -- 비밀번호 (암호화된 상태로 저장)
    name VARCHAR(100) NOT NULL,              -- 실제 이름
    email VARCHAR(100) NOT NULL,             -- 이메일 주소
    role ENUM('USER', 'ADMIN') NOT NULL,     -- 역할 (USER, ADMIN)
    profileImgUrl VARCHAR(255),            -- 프로필 이미지 URL
    birthDate DATE,                         -- 생년월일
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 시간
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 업데이트 시간
);


INSERT INTO USER (username, password, name, email, role, profileImgUrl, birthDate)
VALUES
    ('user', 'password', 'name', 'user@example.com', 'USER', NULL, '1990-01-01');
