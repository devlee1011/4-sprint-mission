# 빌드 단계: 도구/캐시 전용
FROM gradle:8.8-jdk17 AS builder
WORKDIR /build

# 1. 변경 빈도 낮은 Gradle 메타 먼저 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
RUN ./gradlew --no-daemon build -x test || true \
 && ./gradlew --no-daemon dependencies || true


# 2. 소스 코드는 나중에 복사함 - 의존성 재다운로드 방지
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test


# 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 실행 단계: 경량 런타임만 포함
FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar build/libs/
EXPOSE 80
ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar \"$@\"", "--"]