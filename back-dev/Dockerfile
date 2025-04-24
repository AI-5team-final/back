# 1️⃣ 빌드 단계: Gradle을 사용하여 JAR 파일 생성
FROM eclipse-temurin:17-jdk as build
WORKDIR /app

# 프로젝트 소스 코드 복사
COPY . .

# gradlew 실행 권한 추가
RUN chmod +x ./gradlew

# Gradle 빌드 실행 (테스트 제외)
RUN ./gradlew clean build -x test

# 3️⃣ 실행 환경 설정
FROM eclipse-temurin:17-jdk
WORKDIR /app

# ✅ .env 파일 로드
COPY .env .env

# ✅ 환경 변수 적용
RUN export $(grep -v '^#' .env | xargs)

COPY --from=build /app/build/libs/*.jar app.jar

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]