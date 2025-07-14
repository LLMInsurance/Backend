package com.example.LLMInsurance_Backend.controller;

import com.example.LLMInsurance_Backend.domain.dto.request.LoginRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.request.ProfileUpdateRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.request.SignupRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.response.LoginResponseDto;
import com.example.LLMInsurance_Backend.domain.dto.response.ProfileResponseDto;
import com.example.LLMInsurance_Backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 사용자 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = userService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        userService.signup(signupRequestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 사용자 ID 중복 확인
     */
    @GetMapping("/check-userid/{userId}")
    public ResponseEntity<String> checkUserIdDuplicate(@PathVariable String userId) {
        boolean isDuplicate = userService.checkUserIdDuplicate(userId);
        if (isDuplicate) {
            return ResponseEntity.badRequest().body("이미 사용 중인 사용자 ID입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 사용자 ID입니다.");
        }
    }

    /**
     * 사용자 프로필 조회
     */
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(Authentication authentication) {
        String userId = authentication.getName();
        ProfileResponseDto profile = userService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * 사용자 프로필 수정
     */
    @PutMapping("/profile")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        String userId = authentication.getName();
        ProfileResponseDto updatedProfile = userService.updateProfile(userId, profileUpdateRequestDto);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        String userId = authentication.getName();
        userService.logout(userId);
        return ResponseEntity.ok("로그아웃되었습니다.");
    }
} 