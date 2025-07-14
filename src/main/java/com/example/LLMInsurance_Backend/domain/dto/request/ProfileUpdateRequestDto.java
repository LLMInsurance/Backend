package com.example.LLMInsurance_Backend.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequestDto {
    
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    
    @Size(min = 1, max = 50, message = "이름은 1자 이상 50자 이하여야 합니다.")
    private String name;
    
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String phoneNumber;
    
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthDate;
    
    @Pattern(regexp = "^(남|여)$", message = "성별은 남 또는 여 이어야 합니다.")
    private String gender;
    
    private Boolean isMarried;
    
    @Size(min = 1, max = 50, message = "직업은 1자 이상 50자 이하여야 합니다.")
    private String job;
    
    private String[] diseases;
    
    private String[] subscriptions;
} 