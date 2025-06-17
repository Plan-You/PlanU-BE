## 배포환경
#FROM openjdk:17-jdk-alpine
#
## JAR 파일을 컨테이너로 복사
#ARG JAR_FILE=./build/libs/*.jar
#COPY ${JAR_FILE} /app.jar
#
## 애플리케이션 실행 명령어
#ENTRYPOINT ["java", "-jar", "/app.jar"]
#
#
# # 개발환경
#FROM openjdk:17-jdk-slim
#
# # 작업 디렉토리 설정
#WORKDIR /app
#
# # 전체 소스 코드 복사
#COPY . .
#
#RUN chmod +x gradlew
#
# # 애플리케이션 실행 명령어를 gradle bootRun으로 설정
#ENTRYPOINT ["./gradlew", "bootRun"]

FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]




