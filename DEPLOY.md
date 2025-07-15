# LLM Insurance Backend - GCP 배포 가이드

이 문서는 Spring Boot 기반의 LLM Insurance Backend 애플리케이션을 Google Cloud Platform(GCP)에 배포하는 방법을 설명합니다.

## 📋 사전 요구사항

1. **GCP 계정 및 프로젝트**
   - Google Cloud Platform 계정
   - 새로운 GCP 프로젝트 생성
   - 결제 계정 연결

2. **로컬 환경**
   - Docker Desktop 설치
   - Google Cloud SDK (gcloud) 설치
   - Java 17 이상
   - Git

## 🚀 배포 단계

### 1. 로컬 환경 설정

```bash
# Google Cloud SDK 인증
gcloud auth login
gcloud auth configure-docker

# 프로젝트 설정
gcloud config set project YOUR_PROJECT_ID
```

### 2. 환경 변수 설정

`env.example` 파일을 참고하여 `.env` 파일을 생성하고 실제 값으로 설정:

```bash
cp env.example .env
# .env 파일을 편집하여 실제 값 입력
```

### 3. 로컬 테스트 (선택사항)

```bash
# Docker Compose를 사용한 로컬 테스트
docker-compose up --build
```

### 4. Cloud SQL 설정

```bash
# Cloud SQL PostgreSQL 인스턴스 생성
./setup-cloudsql.sh
```

스크립트 실행 전에 다음 값들을 수정하세요:
- `PROJECT_ID`: 실제 GCP 프로젝트 ID
- `DB_PASSWORD`: 강력한 데이터베이스 비밀번호

### 5. Cloud Run 배포

```bash
# 애플리케이션 배포
./deploy-gcp.sh
```

배포 스크립트 실행 전에 다음 값들을 수정하세요:
- `PROJECT_ID`: 실제 GCP 프로젝트 ID

## ⚙️ 환경 변수 설정

배포 후 GCP Console에서 Cloud Run 서비스의 환경 변수를 설정해야 합니다:

### 필수 환경 변수

```bash
# OpenAI API
OPENAI_API_KEY=your_openai_api_key

# JWT 설정
JWT_SECRET=your_strong_jwt_secret
JWT_EXPIRATION=86400000

# 데이터베이스 (Cloud SQL 사용 시)
DATABASE_URL=jdbc:postgresql://google/llminsurance?cloudSqlInstance=PROJECT_ID:REGION:INSTANCE_NAME&socketFactory=com.google.cloud.sql.postgres.SocketFactory
DATABASE_USERNAME=anhongbeom
DATABASE_PASSWORD=your_secure_password

# CORS 설정 (프론트엔드 도메인)
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

### GCP Console에서 환경 변수 설정

1. GCP Console → Cloud Run
2. 배포된 서비스 선택
3. "편집 및 새 수정버전 배포" 클릭
4. "변수 및 보안 비밀" 탭
5. 환경 변수 추가

### gcloud 명령어로 환경 변수 설정

```bash
gcloud run services update llminsurance-backend \
    --region=asia-northeast3 \
    --update-env-vars="OPENAI_API_KEY=your_key,JWT_SECRET=your_secret"
```

## 🗄️ 데이터베이스 옵션

### Option 1: Cloud SQL (권장)
- 완전 관리형 PostgreSQL
- 자동 백업 및 복구
- 고가용성 지원

### Option 2: 외부 데이터베이스
- 기존 PostgreSQL 서버 사용
- `DATABASE_URL` 환경 변수에 외부 DB 연결 정보 설정

## 🔒 보안 설정

### 1. JWT Secret 보안
```bash
# 강력한 JWT Secret 생성
openssl rand -hex 32
```

### 2. 데이터베이스 보안
- 강력한 비밀번호 사용
- IP 허용 목록 설정 (필요시)

### 3. CORS 설정
- 프로덕션에서는 와일드카드(*) 대신 정확한 도메인 지정

## 📊 모니터링 및 로깅

### Health Check 엔드포인트
```
GET /actuator/health
```

### 로그 확인
```bash
# Cloud Run 로그 확인
gcloud logs read --limit=50 --service=llminsurance-backend
```

### 메트릭 확인
GCP Console → Cloud Run → 서비스 선택 → 메트릭 탭

## 🛠️ 문제 해결

### 1. 빌드 실패
```bash
# 로컬에서 빌드 테스트
./gradlew build

# Docker 빌드 테스트
docker build -t test-image .
```

### 2. 데이터베이스 연결 실패
- Cloud SQL 인스턴스 상태 확인
- 환경 변수 확인
- 네트워크 설정 확인

### 3. 메모리 부족
```bash
# Cloud Run 서비스 메모리 증가
gcloud run services update llminsurance-backend \
    --region=asia-northeast3 \
    --memory=2Gi
```

## 🔄 업데이트 배포

코드 변경 후 재배포:

```bash
# 새로운 이미지 빌드 및 배포
./deploy-gcp.sh
```

## 💰 비용 최적화

1. **Cloud Run 설정**
   - `--min-instances=0`: 트래픽이 없을 때 인스턴스 0개
   - `--max-instances=10`: 최대 인스턴스 수 제한

2. **Cloud SQL 설정**
   - `db-f1-micro`: 소규모 서비스용 최소 사양
   - 필요에 따라 스케일 업/다운

## 📞 지원

문제가 발생하면 다음을 확인하세요:
1. Cloud Run 로그
2. Cloud SQL 연결 상태
3. 환경 변수 설정
4. GCP 서비스 상태

---

**참고**: 이 가이드는 기본적인 배포 방법을 다룹니다. 프로덕션 환경에서는 추가적인 보안 설정, 로드 밸런싱, CDN 설정 등을 고려해야 합니다. 