package com.example.LLMInsurance_Backend.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDto {

    // JWT 토큰 정보
    private String accessToken;
    private String tokenType;
    
    // 사용자 기본 정보
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    
    // 사용자 프로필 정보
    private LocalDate birthDate;
    private String gender;
    private boolean isMarried;
    private String job;
    private String[] diseases;
    private String[] subscriptions;
    
    private boolean isLogin;

    public static LoginResponseDto of(String accessToken, 
                                    String userId, 
                                    String email,
                                    String name, 
                                    String phoneNumber,
                                    LocalDate birthDate,
                                    String gender,
                                    boolean isMarried,
                                    String job,
                                    String[] diseases,
                                    String[] subscriptions,
                                    boolean isLogin) {
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(userId)
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthDate(birthDate)
                .gender(gender)
                .isMarried(isMarried)
                .job(job)
                .diseases(diseases)
                .subscriptions(subscriptions)
                .isLogin(isLogin)
                .build();
    }
} 