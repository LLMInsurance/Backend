# LLM Insurance Backend API

## 🚀 기술 스택

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Gradle**

## 🔒 보안 설정

### ⚠️ 중요: 환경 변수 설정 필수

```bash
# JWT 설정 (필수)
JWT_SECRET=your_strong_jwt_secret_here

# 데이터베이스 연결
DATABASE_URL=jdbc:postgresql://localhost:5432/llminsurance
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password

# OpenAI API
OPENAI_API_KEY=your_openai_api_key
```

### 🛡️ 로컬 개발 환경 설정

1. **환경 변수 파일 생성**
   ```bash
   cp env.local.example .env
   # .env 파일을 편집하여 실제 값 입력
   ```

2. **강력한 JWT Secret 생성**
   ```bash
   openssl rand -hex 32
   ```

3. **보안 검사 실행**
   ```bash
   ./gradlew dependencyCheckAnalyze
   ```

참고사항 -> [SECURITY.md](SECURITY.md)

## 📋 주요 기능(추후 추가 예정)

- 사용자 회원가입/로그인
- JWT 토큰 기반 인증
- 사용자 프로필 관리
- 사용자 ID 중복 확인
- 로그아웃

## 🔧 서버 설정

### 로컬 개발 환경
```
Base URL: http://localhost:8080
```

## 🔑 인증 방식

JWT(JSON Web Token) 기반 인증을 사용

### Authorization Header
```
Authorization: Bearer {access_token}
```

## 📱 Android에서 API 사용하기

### 1. HTTP 클라이언트 설정 (Retrofit 사용 예시)

**build.gradle (Module: app)**
```gradle
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
}
```

**ApiService.kt**
```kotlin
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<String>
    
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("api/v1/auth/check-userid/{userId}")
    suspend fun checkUserIdDuplicate(@Path("userId") userId: String): Response<String>
    
    @GET("api/v1/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>
    
    @PUT("api/v1/auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: ProfileUpdateRequest
    ): Response<ProfileResponse>
    
    @POST("api/v1/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<String>
}
```

### 2. 데이터 클래스 정의

**SignupRequest.kt**
```kotlin
data class SignupRequest(
    val userId: String,           
    val password: String,         
    val email: String,           
    val name: String,             
    val phoneNumber: String,      
    val birthDate: String,        
    val gender: String,           
    val isMarried: Boolean,       
    val job: String,              
    val diseases: Array<String>?, 
    val subscriptions: Array<String>? 
)
```

**LoginRequest.kt**
```kotlin
data class LoginRequest(
    val userId: String,
    val password: String
)
```

**LoginResponse.kt**
```kotlin
data class LoginResponse(
    val accessToken: String,
    val tokenType: String,        // "Bearer"
    val userId: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val birthDate: String,
    val gender: String,
    val isMarried: Boolean,
    val job: String,
    val diseases: Array<String>?,
    val subscriptions: Array<String>?,
    val isLogin: Boolean
)
```

**ProfileUpdateRequest.kt**
```kotlin
data class ProfileUpdateRequest(
    val email: String?,
    val name: String?,
    val phoneNumber: String?,      
    val birthDate: String?,        
    val gender: String?,           
    val isMarried: Boolean?,
    val job: String?,
    val diseases: Array<String>?,
    val subscriptions: Array<String>?
)
```

**ProfileResponse.kt**
```kotlin
data class ProfileResponse(
    val userId: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val birthDate: String,
    val gender: String,
    val isMarried: Boolean,
    val job: String,
    val diseases: Array<String>?,
    val subscriptions: Array<String>?,
    val isLogin: Boolean
)
```

## 📚 API 엔드포인트

### 1. 회원가입
```
POST /api/v1/auth/signup
Content-Type: application/json
```

**Request Body:**
```json
{
    "userId": "testuser123",
    "password": "password123",
    "email": "test@example.com",
    "name": "홍길동",
    "phoneNumber": "010-1234-5678",
    "birthDate": "1990-01-01",
    "gender": "남",
    "isMarried": false,
    "job": "개발자",
    "diseases": ["고혈압"],
    "subscriptions": ["국민건강보험"]
}
```

**Response:**
```json
"회원가입이 완료되었습니다."
```

### 2. 로그인
```
POST /api/v1/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
    "userId": "testuser123",
    "password": "password123"
}
```

**Response:**
```json
{
    "accessToken": "토큰토큰토큰...",
    "tokenType": "Bearer",
    "userId": "testuser123",
    "email": "test@example.com",
    "name": "홍길동",
    "phoneNumber": "010-1234-5678",
    "birthDate": "1990-01-01",
    "gender": "남",
    "isMarried": false,
    "job": "개발자",
    "diseases": ["고혈압"],
    "subscriptions": ["국민건강보험"],
    "isLogin": true
}
```

### 3. 사용자 ID 중복 확인
```
GET /api/v1/auth/check-userid/{userId}
```

**Response (사용 가능):**
```json
"사용 가능한 사용자 ID입니다."
```

**Response (중복):**
```json
"이미 사용 중인 사용자 ID입니다."
```

### 4. 프로필 조회
```
GET /api/v1/auth/profile
Authorization: Bearer {access_token}
```

**Response:**
```json
{
    "userId": "testuser123",
    "email": "test@example.com",
    "name": "홍길동",
    "phoneNumber": "010-1234-5678",
    "birthDate": "1990-01-01",
    "gender": "남",
    "isMarried": false,
    "job": "개발자",
    "diseases": ["고혈압"],
    "subscriptions": ["국민건강보험"],
    "isLogin": true
}
```

### 5. 프로필 수정
```
PUT /api/v1/auth/profile
Authorization: Bearer {access_token}
Content-Type: application/json
```

**Request Body:**
```json
{
    "email": "newemail@example.com",
    "name": "홍길동",
    "phoneNumber": "010-5678-1234",
    "job": "시니어 개발자"
}
```

**Response:**
```json
{
    "userId": "testuser123",
    "email": "newemail@example.com",
    "name": "홍길동",
    "phoneNumber": "010-5678-1234",
    "birthDate": "1990-01-01",
    "gender": "남",
    "isMarried": false,
    "job": "시니어 개발자",
    "diseases": ["고혈압"],
    "subscriptions": ["국민건강보험"],
    "isLogin": true
}
```

### 6. 로그아웃
```
POST /api/v1/auth/logout
Authorization: Bearer {access_token}
```

**Response:**
```json
"로그아웃되었습니다."
```

## ⚠️ 에러 응답

### HTTP 상태 코드
- `200`: 성공
- `400`: 잘못된 요청 (유효성 검사 실패)
- `401`: 인증 실패
- `403`: 권한 없음
- `404`: 리소스를 찾을 수 없음
- `500`: 서버 에러

### 에러 응답 예시
```json
{
    "timestamp": "2024-01-20T10:30:00.000+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "사용자 ID는 필수입니다.",
    "path": "/api/v1/auth/signup"
}
```

## 📋 유효성 검사 규칙

### 회원가입 시 필수 검증 항목
- **userId**: 
- **password**: 
- **email**: 
- **name**: 
- **phoneNumber**: 
- **birthDate**: 
- **gender**: 
- **job**: 

### 프로필 수정 시 검증 항목
- **email**: 올바른 이메일 형식 (선택사항)

## 🔒 보안 고려사항
### 1. SharedPreferences를 이용한 토큰 관리
```kotlin
class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    
    fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }
    
    fun getToken(): String? {
        return prefs.getString("access_token", null)
    }
    
    fun clearToken() {
        prefs.edit().remove("access_token").apply()
    }
}
```

### 2. 네트워크 에러 처리
```kotlin
try {
    val response = apiService.login(loginRequest)
    if (response.isSuccessful) {
        val loginResponse = response.body()
        // 성공 처리
    } else {
        // 에러 처리
        when (response.code()) {
            400 -> "잘못된 요청입니다."
            401 -> "인증에 실패했습니다."
            500 -> "서버 에러가 발생했습니다."
            else -> "알 수 없는 에러가 발생했습니다."
        }
    }
} catch (e: Exception) {
    // 네트워크 에러 처리
}
```

### 3. 날짜 형식 변환
```kotlin
// String to LocalDate
val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val birthDate = LocalDate.parse("1990-01-01", dateFormatter)

// LocalDate to String
val birthDateString = birthDate.format(dateFormatter)
```