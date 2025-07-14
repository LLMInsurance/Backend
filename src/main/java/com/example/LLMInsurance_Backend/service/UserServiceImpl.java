package com.example.LLMInsurance_Backend.service;

import com.example.LLMInsurance_Backend.domain.dto.request.LoginRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.request.ProfileUpdateRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.request.SignupRequestDto;
import com.example.LLMInsurance_Backend.domain.dto.response.LoginResponseDto;
import com.example.LLMInsurance_Backend.domain.dto.response.ProfileResponseDto;
import com.example.LLMInsurance_Backend.domain.entity.User;
import com.example.LLMInsurance_Backend.domain.repository.UserRepository;
import com.example.LLMInsurance_Backend.global.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    // 사용자 회원가입
    @Override
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        // 사용자 ID 중복 검사
        if (userRepository.existsByUserId(signupRequestDto.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 ID입니다.");
        }

        // 사용자 엔티티 생성
        User user = User.builder()
                .userId(signupRequestDto.getUserId())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .name(signupRequestDto.getName())
                .phoneNumber(signupRequestDto.getPhoneNumber())
                .birthDate(signupRequestDto.getBirthDate())
                .gender(signupRequestDto.getGender())
                .isMarried(signupRequestDto.getIsMarried())
                .job(signupRequestDto.getJob())
                .diseases(signupRequestDto.getDiseases())
                .subscriptions(signupRequestDto.getSubscriptions())
                .isLogin(false)
                .isDeleted(false)
                .build();

        try {
            userRepository.save(user);
            log.info("새로운 사용자가 회원가입했습니다: {}", signupRequestDto.getUserId());
        } catch (DataIntegrityViolationException e) {
            log.error("회원가입 중 데이터 무결성 오류 발생: {}", e.getMessage());
            throw new IllegalArgumentException("회원가입 중 오류가 발생했습니다. 입력 정보를 확인해주세요.");
        }
    }

    // 사용자 로그인
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // 사용자 조회
        User user = userRepository.findByUserId(loginRequestDto.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("로그인 정보가 올바르지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("로그인 정보가 올바르지 않습니다");
        }

        // 로그인 상태 업데이트
        user.setLogin(true);
        userRepository.save(user);

        // JWT 토큰 생성
        String accessToken = jwtTokenUtil.generateToken(user.getUserId());

        log.info("사용자 {}가 성공적으로 로그인했습니다", user.getUserId());

        return LoginResponseDto.of(
                accessToken,
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getBirthDate(),
                user.getGender(),
                user.isMarried(),
                user.getJob(),
                user.getDiseases(),
                user.getSubscriptions(),
                user.isLogin()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        if (user.isDeleted()) {
            throw new UsernameNotFoundException("삭제된 사용자입니다: " + userId);
        }
        
        log.info("사용자 {}의 프로필 정보를 조회했습니다", userId);
        
        return ProfileResponseDto.from(user);
    }

    @Override
    @Transactional
    public void logout(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        if (user.isDeleted()) {
            throw new UsernameNotFoundException("삭제된 사용자입니다: " + userId);
        }
        
        // 로그인 상태를 false로 변경
        user.setLogin(false);
        userRepository.save(user);
        
        log.info("사용자 {}가 로그아웃했습니다", userId);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfile(String userId, ProfileUpdateRequestDto profileUpdateRequestDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
        
        if (user.isDeleted()) {
            throw new UsernameNotFoundException("삭제된 사용자입니다: " + userId);
        }
        
        // 수정할 필드들만 업데이트 (null이 아닌 경우에만)
        if (profileUpdateRequestDto.getEmail() != null) {
            user.setEmail(profileUpdateRequestDto.getEmail());
        }
        if (profileUpdateRequestDto.getName() != null) {
            user.setName(profileUpdateRequestDto.getName());
        }
        if (profileUpdateRequestDto.getPhoneNumber() != null) {
            user.setPhoneNumber(profileUpdateRequestDto.getPhoneNumber());
        }
        if (profileUpdateRequestDto.getBirthDate() != null) {
            user.setBirthDate(profileUpdateRequestDto.getBirthDate());
        }
        if (profileUpdateRequestDto.getGender() != null) {
            user.setGender(profileUpdateRequestDto.getGender());
        }
        if (profileUpdateRequestDto.getIsMarried() != null) {
            user.setMarried(profileUpdateRequestDto.getIsMarried());
        }
        if (profileUpdateRequestDto.getJob() != null) {
            user.setJob(profileUpdateRequestDto.getJob());
        }
        if (profileUpdateRequestDto.getDiseases() != null) {
            user.setDiseases(profileUpdateRequestDto.getDiseases());
        }
        if (profileUpdateRequestDto.getSubscriptions() != null) {
            user.setSubscriptions(profileUpdateRequestDto.getSubscriptions());
        }
        
        try {
            userRepository.save(user);
            log.info("사용자 {}의 프로필이 수정되었습니다", userId);
            
            return ProfileResponseDto.from(user);
        } catch (DataIntegrityViolationException e) {
            log.error("프로필 수정 중 데이터 무결성 오류 발생: {}", e.getMessage());
            throw new IllegalArgumentException("프로필 수정 중 오류가 발생했습니다. 입력 정보를 확인해주세요.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkUserIdDuplicate(String userId) {
        boolean isDuplicate = userRepository.existsByUserId(userId);
        log.info("사용자 ID {} 중복 확인 결과: {}", userId, isDuplicate ? "중복됨" : "사용 가능");
        return isDuplicate;
    }
}
