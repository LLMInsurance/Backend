package com.example.LLMInsurance_Backend.service;

import com.example.LLMInsurance_Backend.domain.dto.request.LoginRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.request.ProfileUpdateRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.request.SignupRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.response.LoginResponseDto;
import com.example.LLMInsurance_Backend.domain.dto.response.ProfileResponseDto;
import com.example.LLMInsurance_Backend.domain.entity.User;

public interface UserService {
    
    /**
     * 사용자 회원가입
     */
    void signup(SignupRequestDto signupRequestDto);
    
    /**
     * 사용자 로그인
     */
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    
    /**
     * 사용자 ID로 사용자 조회
     */
    User findByUserId(String userId);
    
    /**
     * 비밀번호 검증
     */
    boolean checkPassword(String rawPassword, String encodedPassword);
    
    /**
     * 비밀번호 암호화
     */
    String encodePassword(String rawPassword);
    
    /**
     * 사용자 프로필 조회
     */
    ProfileResponseDto getProfile(String userId);
    
    /**
     * 사용자 로그아웃
     */
    void logout(String userId);
    
    /**
     * 사용자 프로필 수정
     */
    ProfileResponseDto updateProfile(String userId, ProfileUpdateRequestDto profileUpdateRequestDto);
    
    /**
     * 사용자 ID 중복 확인
     */
    boolean checkUserIdDuplicate(String userId);
}
