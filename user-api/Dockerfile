# OpenJDK 21 기반 이미지 사용
FROM openjdk:21-jdk

# 빌드된 JAR 파일 복사
COPY build/libs/*.jar app.jar

# 실행할 프로필 지정 prod(=운영)
ENV USE_PROFILE prod

# 이미지 빌드 명령
ENTRYPOINT ["java", "-Dspring.profiles.active=${USE_PROFILE}", "-jar", "app.jar"]