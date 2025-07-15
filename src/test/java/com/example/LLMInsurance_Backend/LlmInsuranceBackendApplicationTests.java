package com.example.LLMInsurance_Backend;

import com.example.LLMInsurance_Backend.domain.dto.request.SignupRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.request.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LlmInsuranceBackendApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
		// 컨텍스트가 정상적으로 로드되는지 확인
	}

	@Test
	void 회원가입_API_테스트() {
		// Given
		String baseUrl = "http://localhost:" + port + "/api/v1/auth";
		
		SignupRequestDto signupRequest = new SignupRequestDto();
		signupRequest.setUserId("apitest123");
		signupRequest.setPassword("password123");
		signupRequest.setEmail("apitest@example.com");
		signupRequest.setName("API 테스트 사용자");
		signupRequest.setPhoneNumber("010-1111-2222");
		signupRequest.setBirthDate(LocalDate.of(1995, 5, 15));
		signupRequest.setGender("여");
		signupRequest.setIsMarried(true);
		signupRequest.setJob("테스터");
		signupRequest.setDiseases(new String[]{});
		signupRequest.setSubscriptions(new String[]{});

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<SignupRequestDto> entity = new HttpEntity<>(signupRequest, headers);

		// When
		ResponseEntity<String> response = restTemplate.postForEntity(
				baseUrl + "/signup", entity, String.class);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("회원가입이 완료되었습니다");
	}

	@Test
	void 로그인_API_테스트() {
		// Given - 먼저 회원가입
		String baseUrl = "http://localhost:" + port + "/api/v1/auth";
		
		SignupRequestDto signupRequest = new SignupRequestDto();
		signupRequest.setUserId("logintest123");
		signupRequest.setPassword("password123");
		signupRequest.setEmail("logintest@example.com");
		signupRequest.setName("로그인 테스트 사용자");
		signupRequest.setPhoneNumber("010-3333-4444");
		signupRequest.setBirthDate(LocalDate.of(1988, 8, 8));
		signupRequest.setGender("남");
		signupRequest.setIsMarried(false);
		signupRequest.setJob("개발자");
		signupRequest.setDiseases(new String[]{});
		signupRequest.setSubscriptions(new String[]{});

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		// 회원가입 실행
		restTemplate.postForEntity(baseUrl + "/signup", 
				new HttpEntity<>(signupRequest, headers), String.class);

		// 로그인 요청
		LoginRequestDto loginRequest = new LoginRequestDto();
		loginRequest.setUserId("logintest123");
		loginRequest.setPassword("password123");

		HttpEntity<LoginRequestDto> loginEntity = new HttpEntity<>(loginRequest, headers);

		// When
		ResponseEntity<String> response = restTemplate.postForEntity(
				baseUrl + "/login", loginEntity, String.class);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("accessToken");
		assertThat(response.getBody()).contains("Bearer");
		assertThat(response.getBody()).contains("logintest123");
	}

	@Test
	void Health_Check_테스트() {
		// When
		ResponseEntity<String> response = restTemplate.getForEntity(
				"http://localhost:" + port + "/actuator/health", String.class);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("UP");
	}
}
