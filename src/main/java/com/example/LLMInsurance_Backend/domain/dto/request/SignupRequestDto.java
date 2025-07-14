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
public class SignupRequestDto {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Size(min = 4, max = 20, message = "사용자 ID는 4자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자 ID는 영문, 숫자, 언더스코어만 사용 가능합니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$"
            , message = "비밀번호는 숫자 및 영문이 포함되어야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)")
    private String phoneNumber;

    @NotNull(message = "생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthDate;

    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "^(남|여)$", message = "성별은 남 또는 여 이어야 합니다.")
    private String gender;

    @NotNull(message = "결혼 여부는 필수입니다.")
    private Boolean isMarried;

    @NotBlank(message = "직업은 필수입니다.")
    @Size(min = 2, max = 100, message = "직업은 2자 이상 100자 이하여야 합니다.")
    private String job;

    private String[] diseases;

    private String[] subscriptions;
} 