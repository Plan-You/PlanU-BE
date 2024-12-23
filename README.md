# PlanU-BE

초기 설정

1. DockerFile : 개발환경 용으로 변경 (배포환경 주석처리, 개발환경 주석풀기)
2. gradlew : 오른쪽 하단에 CRLF -> LF 로 변경
3. .env : 빈칸 작성하기
4. 개발환경에서 util -> CookieUtil -> createCookie() 메서드에서 '.domain("https://localhost:5173")' 주석 처리하기
   - 주석 처리된 상태로 배포 환경에 적용되지 않도록 주의

main에 merge 후 init.sql 변경되면 볼륨 초기화하기
