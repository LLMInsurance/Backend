# 멀티스테이지 빌드: 빌드 단계
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Gradle wrapper와 build 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드 (캐시 최적화)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# JAR 파일 빌드
RUN ./gradlew bootJar --no-daemon

# 실행 단계: 경량 JRE 이미지 사용
FROM openjdk:17-jdk-slim

# 애플리케이션 사용자 생성
RUN groupadd -g 1001 spring && \
    useradd -u 1001 -g spring -s /bin/sh spring

# 작업 디렉터리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 파일 소유권 변경
RUN chown spring:spring app.jar

# 비루트 사용자로 전환
USER spring:spring

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 