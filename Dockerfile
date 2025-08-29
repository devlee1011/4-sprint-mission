# 1. Amazon Corretto 17 이미지 사용
FROM amazoncorretto:17

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. gradlew와 필요한 파일 복사 (gradlew 반드시 포함!)
COPY gradlew /app/gradlew
COPY gradle /app/gradle
COPY build.gradle /app/
COPY settings.gradle /app/
COPY src /app/src

# 4. dos2unix 설치 후 gradlew 권한/줄바꿈 변환
RUN yum install -y dos2unix && \
    dos2unix /app/gradlew && \
    chmod +x /app/gradlew

# 5. gradlew 빌드 실행
RUN ./gradlew clean build -x test

# 6. 포트 노출
EXPOSE 80

# 7. 환경 변수 (프로젝트 정보)
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# 8. 환경 변수 (JVM 옵션, 기본값은 빈 문자열)
ENV JVM_OPTS=""

# 9. 실행 명령어
ENTRYPOINT ["sh", "-c", "exec java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar \"$@\"", "--"]