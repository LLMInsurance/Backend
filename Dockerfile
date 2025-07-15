# Multi-stage build
FROM gradle:8.5-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 파일들 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 의존성 다운로드 (캐싱 최적화)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM openjdk:17-jre-slim

# 애플리케이션 사용자 생성
RUN addgroup --system spring && adduser --system spring --ingroup spring

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 소유권 변경
RUN chown spring:spring app.jar

# 사용자 전환
USER spring:spring

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"] 