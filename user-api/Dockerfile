# OpenJDK 21 기반 이미지 사용
FROM openjdk:21-jdk

# 빌드된 JAR 파일 복사
COPY build/libs/*.jar app.jar

# 이미지 빌드 명령
ENTRYPOINT ["java", "-jar", "app.jar"]