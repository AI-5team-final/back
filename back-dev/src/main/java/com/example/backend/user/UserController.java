package com.example.backend.user;
import com.example.backend.dto.sign.SecurityUserDto;
import com.example.backend.dto.sign.SignUpRequestDTO;
import com.example.backend.dto.UrlResponseDTO;
import com.example.backend.entity.User;
import com.example.backend.swagger.UserControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UrlResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO) {

        userService.saveUser(signUpRequestDTO); // 회원가입 진행 (DB 저장)

        System.out.println("signUpRequestDTO: " + signUpRequestDTO);

        return ResponseEntity.ok(
                UrlResponseDTO.builder()
                        .url("/auth/login") // 회원 가입이 완료된 후 로그인 페이지로 이동
                        .message("회원가입을 성공했습니다.")
                        .build()
        );
    }

    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestBody String email) {
        if(email==null||email.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        email = email.replaceAll("^\"|\"$", ""); // 앞뒤 큰따옴표 제거
        boolean exists = userService.existsByEmail(email.trim());
        return ResponseEntity.ok(!exists);
    }

    /**
     *
     * front에서 이런식으로 보내시면 됩니다.
     *
     * fetch('/auth/tutorial', {
     *   method: 'PUT',
     *   headers: {
     *     'Authorization': 'Bearer ' + accessToken
     *   }
     * })
     *   .then(response => response.json())
     *   .then(data => {
     *     console.log('튜토리얼 상태 업데이트 성공:', data);
     *   })
     *   .catch(error => {
     *     console.error('튜토리얼 업데이트 에러:', error);
     *   });
     *
     * **/
    @PostMapping("/tutorial")
    public ResponseEntity<?> updateTutorialStatus(@AuthenticationPrincipal SecurityUserDto authenticatedUser) {
        // 인증 정보 확인
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        try {
            // 튜토리얼 상태 업데이트 로직 실행
            userService.tutorialComplete(authenticatedUser);
            return ResponseEntity.ok("튜토리얼 상태가 업데이트되었습니다.");
        } catch (UsernameNotFoundException e) {
            // 예: 사용자 정보가 DB에 없을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        } catch (IllegalStateException e) {
            // 예: 이미 튜토리얼이 완료된 상태라면
            return ResponseEntity.status(HttpStatus.CONFLICT).body("튜토리얼 상태 업데이트 중 충돌 발생: " + e.getMessage());
        } catch (Exception e) {
            // 그 외의 예상치 못한 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 에러가 발생하였습니다: " + e.getMessage());
        }
    }

    // 모든 사용자 조회 API (GET 요청)
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 특정 이름으로 사용자 조회 API (GET 요청)
    @GetMapping("/{name}")
    public ResponseEntity<List<User>> getUserByName(@PathVariable String name) {
        return ResponseEntity.ok(userService.getUsersByName(name));
    }
    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("비사아아아아앙!!");
    }
}