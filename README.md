# LLM Insurance Backend

## 🏗️ 기술 스택

- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: PostgreSQL (Cloud SQL), H2 (개발용)
- **Authentication**: JWT + Spring Security
- **AI Integration**: OpenAI API
- **Deployment**: Google Cloud Run
- **Build**: Gradle
- **Containerization**: Docker

## 🚀 배포 환경

### 로컬 개발 환경
- H2 인메모리 데이터베이스 사용
- 개발 편의를 위한 H2 Console 제공

### 프로덕션 환경 (Google Cloud Platform)
- **Cloud Run**: 서버리스 컨테이너 플랫폼
- **Cloud SQL**: 관리형 PostgreSQL
- **Container Registry**: Docker 이미지 저장

## 📦 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/LLMInsurance_Backend/
│   │   ├── controller/         # REST API 컨트롤러
│   │   ├── service/           # 비즈니스 로직
│   │   ├── domain/
│   │   │   ├── entity/        # JPA 엔티티
│   │   │   ├── dto/           # 데이터 전송 객체
│   │   │   └── repository/    # 데이터 접근 계층
│   │   └── global/
│   │       ├── config/        # 설정 클래스
│   │       ├── exception/     # 예외 처리
│   │       └── utils/         # 유틸리티
│   └── resources/
│       ├── application.yaml   # 설정 파일
│       └── application-test.yaml
└── test/                      # 테스트 코드
```

## 🛠️ 로컬 개발 환경 설정

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd LLMInsurance_Backend
```

### 2. 애플리케이션 실행
```bash
# Gradle을 통한 실행
./gradlew bootRun

# 또는 JAR 파일 빌드 후 실행
./gradlew bootJar
java -jar build/libs/LLMInsurance_Backend-0.0.1-SNAPSHOT.jar
```

### 3. H2 Console 접속
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (빈 값)

## 📱 안드로이드 개발자용 API 가이드

### 서버 정보
- **배포 URL**: `https://llminsurance-backend-llqm7zqpqa-uc.a.run.app`
- **로컬 개발 URL**: `http://localhost:8080`

### 공통 설정
- **Content-Type**: `application/json`
- **JWT 인증**: `Authorization: Bearer {accessToken}` (로그인 후)

### 1. Health Check (서버 상태 확인)
```http
GET /actuator/health
```
**응답 예시:**
```json
{"status":"UP"}
```

### 2. 회원가입
```http
POST /api/v1/auth/signup
Content-Type: application/json

{
  "userId": "testuser123",
  "password": "TestPassword123",
  "email": "test@example.com",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "birthDate": "1990-01-01",
  "gender": "남",
  "isMarried": false,
  "job": "개발자",
  "diseases": [],
  "subscriptions": []
}
```
**응답 예시:**
```json
"회원가입이 완료되었습니다."
```

### 3. 로그인
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "userId": "testuser123",
  "password": "TestPassword123"
}
```
**응답 예시:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "userId": "testuser123",
  "email": "test@example.com",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "birthDate": "1990-01-01",
  "gender": "남",
  "job": "개발자",
  "diseases": [],
  "subscriptions": [],
  "married": false,
  "login": true
}
```

### 4. 사용자 프로필 조회 (인증 필요)
```http
GET /api/v1/auth/profile
Authorization: Bearer {accessToken}
```

### 5. 사용자 프로필 수정 (인증 필요)
```http
PUT /api/v1/auth/profile
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "name": "수정된 이름",
  "phoneNumber": "010-9876-5432",
  "job": "수정된 직업"
}
```

### 6. 사용자 ID 중복 확인
```http
GET /api/v1/auth/check-userid/{userId}
```

### 7. 로그아웃 (인증 필요)
```http
POST /api/v1/auth/logout
Authorization: Bearer {accessToken}
```

### 🔧 안드로이드 구현 팁

#### Retrofit 설정 예시
```kotlin
// RetrofitClient.kt
object RetrofitClient {
    private const val BASE_URL = "https://llminsurance-backend-llqm7zqpqa-uc.a.run.app/"
    
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// ApiService.kt
interface ApiService {
    @POST("api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<String>
    
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("api/v1/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>
}
```

#### 에러 처리
- **400 Bad Request**: 유효성 검사 실패
- **401 Unauthorized**: 인증 토큰 없음/만료
- **403 Forbidden**: 권한 없음
- **409 Conflict**: 중복된 사용자 ID
- **500 Internal Server Error**: 서버 오류

#### JWT 토큰 관리
```kotlin
// SharedPreferences에 토큰 저장
fun saveToken(token: String) {
    sharedPreferences.edit()
        .putString("access_token", token)
        .apply()
}

// Authorization 헤더 추가
fun getAuthHeader(): String {
    val token = sharedPreferences.getString("access_token", "")
    return "Bearer $token"
}
```

#### 데이터 모델 클래스
```kotlin
// SignupRequest.kt
data class SignupRequest(
    val userId: String,
    val password: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val birthDate: String, // "YYYY-MM-DD" 형식
    val gender: String, // "남" 또는 "여"
    val isMarried: Boolean,
    val job: String,
    val diseases: List<String> = emptyList(),
    val subscriptions: List<String> = emptyList()
)

// LoginRequest.kt
data class LoginRequest(
    val userId: String,
    val password: String
)

// LoginResponse.kt
data class LoginResponse(
    val accessToken: String,
    val tokenType: String,
    val userId: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val birthDate: String,
    val gender: String,
    val job: String,
    val diseases: List<String>,
    val subscriptions: List<String>,
    val married: Boolean,
    val login: Boolean
)

// ProfileUpdateRequest.kt
data class ProfileUpdateRequest(
    val name: String? = null,
    val phoneNumber: String? = null,
    val job: String? = null,
    val diseases: List<String>? = null,
    val subscriptions: List<String>? = null
)
```

#### 입력값 유효성 검사 (클라이언트)
```kotlin
fun validateSignupInput(request: SignupRequest): String? {
    return when {
        request.userId.length < 4 || request.userId.length > 20 -> 
            "사용자 ID는 4자 이상 20자 이하여야 합니다."
        !request.userId.matches(Regex("^[a-zA-Z0-9_]+$")) -> 
            "사용자 ID는 영문, 숫자, 언더스코어만 사용 가능합니다."
        request.password.length < 8 || request.password.length > 20 -> 
            "비밀번호는 8자 이상 20자 이하여야 합니다."
        !request.password.matches(Regex("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$")) -> 
            "비밀번호는 숫자 및 영문이 포함되어야 합니다."
        !request.phoneNumber.matches(Regex("^01[0-9]-\\d{3,4}-\\d{4}$")) -> 
            "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)"
        request.gender !in listOf("남", "여") -> 
            "성별은 남 또는 여 이어야 합니다."
        else -> null
    }
}
```

#### 네트워크 보안 설정
```kotlin
// Network Security Config (network_security_config.xml)
// res/xml/network_security_config.xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">llminsurance-backend-llqm7zqpqa-uc.a.run.app</domain>
    </domain-config>
</network-security-config>

// AndroidManifest.xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
</application>
```

#### OkHttp 인터셉터 (토큰 자동 추가)
```kotlin
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()
        
        val newRequest = if (token != null && !originalRequest.url.encodedPath.contains("/auth/login") 
                            && !originalRequest.url.encodedPath.contains("/auth/signup")) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}

// Retrofit 클라이언트에 인터셉터 추가
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor(tokenManager))
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

#### 사용 예시
```kotlin
class AuthRepository(private val apiService: ApiService) {
    
    suspend fun signup(request: SignupRequest): Result<String> {
        return try {
            val response = apiService.signup(request)
            if (response.isSuccessful) {
                Result.success(response.body() ?: "회원가입 성공")
            } else {
                Result.failure(Exception("회원가입 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    // 토큰 저장
                    tokenManager.saveToken(loginResponse.accessToken)
                    Result.success(loginResponse)
                } ?: Result.failure(Exception("응답 데이터가 없습니다"))
            } else {
                Result.failure(Exception("로그인 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 🧪 API 테스트

### JUnit 테스트 실행
```bash
./gradlew test
```

## 🚀 배포 방법

### 🔄 방법 1: GitHub Actions 자동 배포

#### 1. GitHub Secrets 설정
GitHub 저장소 → Settings → Secrets and variables → Actions에서 다음 설정:

```
GCP_SA_KEY: GCP 서비스 계정 JSON 키 (전체 JSON 내용)
DB_PASSWORD: LLMInsurance2024 (또는 새로운 보안 비밀번호)
JWT_SECRET: 32자리 이상 랜덤 문자열 (예: openssl rand -hex 32 결과)
```

#### 2. GCP 서비스 계정 생성 및 권한 설정
```bash
# 서비스 계정 생성
gcloud iam service-accounts create github-actions \
    --display-name="GitHub Actions"

# 필요한 권한 부여
gcloud projects add-iam-policy-binding concise-dolphin-465907-s7 \
    --member="serviceAccount:github-actions@concise-dolphin-465907-s7.iam.gserviceaccount.com" \
    --role="roles/run.admin"

gcloud projects add-iam-policy-binding concise-dolphin-465907-s7 \
    --member="serviceAccount:github-actions@concise-dolphin-465907-s7.iam.gserviceaccount.com" \
    --role="roles/storage.admin"

gcloud projects add-iam-policy-binding concise-dolphin-465907-s7 \
    --member="serviceAccount:github-actions@concise-dolphin-465907-s7.iam.gserviceaccount.com" \
    --role="roles/cloudsql.client"

# JSON 키 생성 (이 내용을 GCP_SA_KEY 시크릿에 복사)
gcloud iam service-accounts keys create github-actions-key.json \
    --iam-account=github-actions@concise-dolphin-465907-s7.iam.gserviceaccount.com

cat github-actions-key.json
```

#### 3. 자동 배포 실행
- `main` 또는 `master` 브랜치에 코드 푸시
- GitHub Actions가 자동으로: 테스트 → 빌드 → 배포 실행
- 배포 완료 후 URL 확인

### 🛠️ 방법 2: 수동 배포

#### 전제 조건
- Google Cloud SDK 설치 및 인증
- Docker 설치
- 환경변수 설정

```bash
# 필수 환경변수 설정
export DB_PASSWORD="LLMInsurance2024"
export JWT_SECRET=$(openssl rand -hex 32)

# GCP 인증
gcloud auth login
gcloud config set project concise-dolphin-465907-s7
```

#### 배포 스크립트 실행
```bash
./deploy-to-cloudrun.sh
```

## 🔧 설정 파일

### application.yaml
- `dev` 프로파일: H2 인메모리 데이터베이스
- `prod` 프로파일: Cloud SQL PostgreSQL
- `test` 프로파일: 테스트용 H2 데이터베이스

## 📋 주요 기능

- ✅ JWT 기반 사용자 인증/인가
- ✅ Spring Security 보안 설정
- ✅ 사용자 회원가입/로그인
- ✅ RESTful API 설계
- ✅ JPA를 통한 데이터 영속성
- ✅ 전역 예외 처리
- ✅ API 입력값 검증
- ✅ JUnit 통합 테스트
- ✅ Cloud Run 배포 지원

## 🌐 아키텍처

```
[Client] → [Cloud Run] → [Cloud SQL]
                ↓
         [Container Registry]
                ↓
         [Cloud Build/Deploy]
```

## 📚 참고 문서

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Google Cloud Run](https://cloud.google.com/run/docs)
- [Google Cloud SQL](https://cloud.google.com/sql/docs)
- [JWT.io](https://jwt.io/)# GitHub Actions WIF 권한 수정
