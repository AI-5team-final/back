package com.example.backend.user;

import com.example.backend.entity.User;
import lombok.RequiredArgsConstructor;
import com.example.backend.config.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserService userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username + " not found");
        }

        return new CustomUserDetails(user.get()); // 이 안에 password가 암호화된 상태여야 함
    }
}
