package com.example.backend.user;

import com.example.backend.dto.sign.SecurityUserDto;
import com.example.backend.dto.sign.SignUpRequestDTO;
import com.example.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final int welcomeCredit = 1000;

    // 사용자 저장
    public void saveUser(SignUpRequestDTO signUpRequestDTO) {
        User user = signUpRequestDTO.toUser(bCryptPasswordEncoder);

        // 튜토리얼 진행하지 않은 상태로 회원가입
        user.setTutorial(false);
        user.setCredit(welcomeCredit);
        String rawPhone = user.getPhone();
        if (rawPhone != null) {
            user.setPhone(rawPhone.replaceAll("-", ""));
        }
        userRepository.save(user);
    }

    public void tutorialComplete(SecurityUserDto authenticatedUser) {
        // 튜토리얼 완료 상태 업데이트
        User user = authenticatedUser.toUser();
        user.setTutorial(true);
        userRepository.save(user);
    }

    public void addCredit(Long userId, Integer credit) {
        User user = userRepository.findById(userId).orElse(null);
        user.setCredit(user.getCredit() + credit);
    }

    // 전체 사용자 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 특정 이름의 사용자 조회
    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }

    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    public boolean existsByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return exists;
    }

    public void rollbackCredit(User user, int cost) {
        // 사용자의 크레딧 차감 금액만큼 롤백
        user.setCredit(user.getCredit() + cost);
        userRepository.save(user); // DB에 저장하여 크레딧 롤백
    }

    public void useCredit(User user, int cost) {

        user.setCredit(user.getCredit() - cost);
        userRepository.save(user);
    }
}
