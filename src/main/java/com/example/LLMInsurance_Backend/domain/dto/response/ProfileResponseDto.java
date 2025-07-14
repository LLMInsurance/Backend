package com.example.LLMInsurance_Backend.domain.dto.response;

import com.example.LLMInsurance_Backend.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    private LocalDate birthDate;
    private String gender;
    private Boolean isMarried;
    private String job;
    private String[] diseases;
    private String[] subscriptions;
    private Boolean isLogin;

    public static ProfileResponseDto from(User user) {
        return ProfileResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .isMarried(user.isMarried())
                .job(user.getJob())
                .diseases(user.getDiseases())
                .subscriptions(user.getSubscriptions())
                .isLogin(user.isLogin())
                .build();
    }
} 