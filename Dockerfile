# 1. Amazon Corretto 17 이미지 사용
FROM amazoncorretto:17

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 프로젝트 파일 복사 (.dockerignore로 불필요한 파일 제외)
COPY . .

# 4. Gradle Wrapper로 빌드
RUN ./gradlew clean build -x test

# 5. 포트 노출
EXPOSE 80

# 6. 환경 변수 (프로젝트 정보)
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# 7. 환경 변수 (JVM 옵션, 기본값은 빈 문자열)
ENV JVM_OPTS=""

# 8. 실행 명령어 (환경 변수 활용)
CMD ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
