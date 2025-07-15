# 🔒 보안 설정 가이드

이 문서는 LLM Insurance Backend 애플리케이션의 보안 설정 방법을 설명합니다.

## 🔑 환경 변수 설정

### 1. JWT Secret 생성

**절대로 기본값을 그대로 사용하지 말기**

```bash
# 강력한 JWT Secret 생성
openssl rand -hex 32

# 또는 다음 중 하나 사용
openssl rand -base64 32
uuidgen | tr -d '-'
```

### 2. 필수 환경 변수

다음 환경 변수들은 반드시 설정

```bash
# JWT 설정 (필수)
JWT_SECRET=여기에_강력한_32글자_이상의_키_입력

# 데이터베이스 연결 정보
DATABASE_URL=jdbc:postgresql://your-host:5432/your-database
DATABASE_USERNAME=your_secure_username
DATABASE_PASSWORD=your_secure_password

# OpenAI API Key
OPENAI_API_KEY=your_actual_openai_api_key
```

### 3. 환경별 설정

#### 로컬 개발환경
```bash
# .env 파일 생성 (git에 커밋하지 않음)
cp env.local.example .env
# 실제 값으로 편집
```

#### 프로덕션 환경
- Cloud Run: GCP Console에서 환경 변수 설정
- Docker: docker-compose에서 환경 변수 파일 사용
- Kubernetes: Secret 리소스 사용

## 🛡️ 보안 체크리스트

### ✅ 배포 전 확인사항

- [ ] JWT_SECRET이 강력한 키로 설정됨
- [ ] 데이터베이스 비밀번호가 강력함
- [ ] CORS가 적절히 설정됨 (프로덕션에서는 와일드카드 금지)
- [ ] .env 파일이 .gitignore에 포함됨
- [ ] 하드코딩된 비밀번호/키가 없음
- [ ] API 키가 환경 변수로 관리됨

### ✅ 프로덕션 환경 추가 보안

```bash
# CORS 설정 (프로덕션)
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# JPA 설정 (프로덕션)
JPA_DDL_AUTO=validate  # update 대신 validate 사용
JPA_SHOW_SQL=false     # SQL 로그 비활성화
```

## 🚨 보안 위험 요소

### ❌ 절대 하지 말아야 할 것들

1. **기본 JWT Secret 사용**
   ```yaml
   # 나쁜 예
   jwt:
     secret: please-change-this-secret-key-in-production
   ```

2. **하드코딩된 비밀번호**
   ```yaml
   # 나쁜 예
   spring:
     datasource:
       password: mypassword123
   ```

3. **와일드카드 CORS (프로덕션)**
   ```yaml
   # 나쁜 예 (프로덕션에서)
   cors:
     allowed-origins: "*"
   ```

4. **민감한 정보 로깅**
   ```yaml
   # 나쁜 예 (프로덕션에서)
   logging:
     level:
       org.hibernate.SQL: DEBUG
   ```

## 🔧 환경 변수 관리 방법

### 로컬 개발
```bash
# .env 파일 사용
export $(cat .env | xargs)
./gradlew bootRun
```

### Docker
```bash
# docker-compose.yml에서 환경 변수 파일 사용
docker-compose --env-file .env up
```

### GCP Cloud Run
```bash
# gcloud 명령어로 환경 변수 설정
gcloud run services update SERVICE_NAME \
  --update-env-vars="JWT_SECRET=your_secret,OPENAI_API_KEY=your_key"
```

## 🔍 보안 검사 도구

### 1. Git Secret 검사
```bash
# git-secrets 설치 및 설정
git secrets --install
git secrets --register-aws

# 보안 검사 실행
git secrets --scan
```

### 2. 의존성 취약점 검사
```bash
# Gradle 의존성 검사
./gradlew dependencyCheckAnalyze
```

### 3. 코드 정적 분석
```bash
# SpotBugs 실행
./gradlew spotbugsMain
```

## 📱 로테이션 정책

### JWT Secret 로테이션
1. 새로운 강력한 키 생성
2. 환경 변수 업데이트
3. 애플리케이션 재시작
4. 기존 토큰은 만료 시까지 유효

### API Key 로테이션
1. 새로운 API 키 발급
2. 환경 변수 업데이트
3. 애플리케이션 재시작
4. 이전 키 비활성화

## 🆘 보안 사고 대응

### 키 노출 시 대응
1. **즉시 조치**
   - 노출된 키 무효화
   - 새로운 키 생성 및 배포
   - 관련 로그 검토

2. **후속 조치**
   - 보안 검토 수행
   - 접근 로그 분석
   - 프로세스 개선

## 📞 문의

보안 관련 문제나 취약점을 발견하시면 즉시 보고해 주세요.

---

**⚠️ 주의**: 이 가이드는 기본적인 보안 설정을 다룹니다. 실제 프로덕션 환경에서는 추가적인 보안 조치가 필요할 수 있습니다. 