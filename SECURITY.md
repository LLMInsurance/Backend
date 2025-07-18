# 🔒 보안 가이드

## ⚠️ 깃허브 업로드 전 필수 확인사항

### 1. 환경 변수 설정 확인
프로덕션 환경에서는 다음 환경 변수들이 **반드시** 설정되어야 합니다:

```bash
# JWT 시크릿 (32글자 이상 강력한 키)
export JWT_SECRET=$(openssl rand -hex 32)

# 데이터베이스 패스워드
export DB_PASSWORD=your_very_secure_password

# OpenAI API 키
export OPENAI_API_KEY=your_openai_api_key
```

### 2. 하드코딩된 비밀번호 제거 ✅
- ✅ `application-prod.yaml`의 하드코딩된 JWT 시크릿 제거 완료
- ✅ `deploy-to-cloudrun.sh`의 하드코딩된 데이터베이스 패스워드 제거 완료
- ✅ `cloudbuild.yaml`의 보안 정보 주석 처리 완료

### 3. .gitignore 설정 ✅
다음 파일들이 올바르게 제외되어 있습니다:
- `.env*` 파일들
- `application-secrets.*` 파일들
- 인증서 파일들 (`*.key`, `*.pem`, etc.)

### 4. 안전한 기본값 사용 ✅
모든 설정 파일에서 환경 변수를 사용하고 있습니다:
- `${JWT_SECRET}` (기본값 없음)
- `${DATABASE_PASSWORD}` (기본값 없음)
- `${OPENAI_API_KEY}` (기본값 없음)

## 🔄 GitHub Actions 보안 설정

### GitHub Secrets 설정 가이드
GitHub 저장소 → Settings → Secrets and variables → Actions에서 설정:

#### 필수 Secrets
1. **GCP_SA_KEY**: GCP 서비스 계정 JSON 키
   ```bash
   # 서비스 계정 생성 후 JSON 키 다운로드
   gcloud iam service-accounts keys create github-actions-key.json \
       --iam-account=github-actions@PROJECT_ID.iam.gserviceaccount.com
   
   # 파일 내용 전체를 복사하여 GitHub Secret에 등록
   cat github-actions-key.json
   ```

2. **DB_PASSWORD**: PostgreSQL 데이터베이스 비밀번호
   - 강력한 비밀번호 사용 (최소 12자, 대소문자+숫자+특수문자)
   - 예: `MySecurePassword2024!`

3. **JWT_SECRET**: JWT 토큰 서명 키
   ```bash
   # 안전한 랜덤 키 생성
   openssl rand -hex 32
   ```

#### 권한 최소화 원칙
서비스 계정에는 필요한 최소 권한만 부여:
- `roles/run.admin`: Cloud Run 서비스 관리
- `roles/storage.admin`: Container Registry 접근
- `roles/cloudsql.client`: Cloud SQL 연결

### 보안 모니터링
- GitHub Actions 실행 로그에서 비밀 정보 노출 확인
- 서비스 계정 키 정기적 교체 (3개월마다)
- 배포 실패 시 로그에서 민감 정보 확인

## 🛡️ 보안 체크리스트

### ✅ Git 커밋 전 확인
- [ ] `.env` 파일이 `.gitignore`에 포함되어 있는가?
- [ ] 하드코딩된 API 키나 비밀번호가 없는가?
- [ ] 프로덕션 JWT 시크릿이 환경 변수로만 설정되어 있는가?
- [ ] 테스트 코드에 실제 비밀번호가 포함되어 있지 않은가?

### ✅ GitHub Actions 설정 확인
- [ ] GitHub Secrets에 모든 필수 값이 설정되어 있는가?
- [ ] GCP 서비스 계정에 최소 권한만 부여되어 있는가?
- [ ] GitHub Actions 워크플로우에서 환경변수가 올바르게 참조되고 있는가?
- [ ] 배포 로그에 비밀 정보가 노출되지 않는가?

### ✅ 배포 전 확인
- [ ] 모든 환경 변수가 설정되어 있는가?
- [ ] JWT 시크릿이 충분히 강력한가? (32글자 이상)
- [ ] 데이터베이스 패스워드가 강력한가?
- [ ] CORS 설정이 프로덕션에 적합한가?

## 🔧 안전한 환경 변수 생성

### JWT 시크릿 생성
```bash
# 방법 1: OpenSSL 사용
openssl rand -hex 32

# 방법 2: 온라인 생성기
# https://generate-secret.vercel.app/32

# 방법 3: uuidgen 사용
uuidgen | tr -d '-' | head -c 32
```

### 강력한 패스워드 생성
```bash
# 방법 1: OpenSSL 사용
openssl rand -base64 32

# 방법 2: pwgen 사용
pwgen -s 32 1
```

## 🚨 보안 사고 대응

### 만약 비밀번호가 Git에 커밋된 경우:
1. **즉시 조치**
   ```bash
   # 커밋 히스토리에서 제거
   git filter-branch --force --index-filter \
     'git rm --cached --ignore-unmatch path/to/file' \
     --prune-empty --tag-name-filter cat -- --all
   
   # 강제 푸시
   git push origin --force --all
   ```

2. **새로운 비밀번호 생성**
   - 노출된 모든 키/패스워드 즉시 변경
   - 새로운 강력한 키 생성 및 배포

3. **보안 검토**
   - 접근 로그 확인
   - 다른 취약점 점검

## 📞 문의
보안 문제 발견 시 즉시 알려주세요. 