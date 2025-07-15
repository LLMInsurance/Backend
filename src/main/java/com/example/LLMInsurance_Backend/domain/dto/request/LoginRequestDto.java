package com.example.LLMInsurance_Backend.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
public class LoginRequestDto {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }
} 