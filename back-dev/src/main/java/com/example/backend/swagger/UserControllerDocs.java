package com.example.backend.swagger;

import com.example.backend.dto.sign.SecurityUserDto;
import com.example.backend.dto.sign.SignUpRequestDTO;
import com.example.backend.dto.UrlResponseDTO;
import com.example.backend.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "User", description = "회원 관련 API")
public interface UserControllerDocs {

    @Operation(
            summary = "회원가입",
            description = "사용자가 회원가입을 진행합니다.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = UrlResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "요청 형식 오류"),
                    @ApiResponse(responseCode = "500", description = "서버 에러")
            }
    )
    ResponseEntity<UrlResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO);

    @Operation(
            summary = "이메일 중복 확인",
            description = "입력한 이메일이 이미 사용 중인지 확인합니다. (중복이 아니라면 true 반환)",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(
                            type = "string",
                            description = "검사할 이메일 주소",
                            example = "\"test@example.com\""
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "중복 확인 완료",
                            content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러")
            }
    )
    ResponseEntity<Boolean> checkEmail(@RequestBody String email);

    @Operation(
            summary = "튜토리얼 상태 업데이트",
            description = "인증된 사용자의 튜토리얼 상태를 업데이트합니다. (토큰 기반 인증 필요)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "튜토리얼 상태가 업데이트되었습니다.",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "401", description = "인증 정보가 없습니다.",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "409", description = "튜토리얼 상태 업데이트 중 충돌 발생",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러가 발생하였습니다.",
                            content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    ResponseEntity<?> updateTutorialStatus(@AuthenticationPrincipal SecurityUserDto authenticatedUser);

    @Operation(summary = "모든 사용자 조회", description = "전체 사용자 목록을 반환합니다.")
    ResponseEntity<List<User>> getAllUsers();

    @Operation(summary = "이름으로 사용자 조회", description = "이름으로 사용자 검색")
    ResponseEntity<List<User>> getUserByName(@PathVariable String name);

    @Operation(summary = "에러 테스트", description = "500 에러 테스트용 API")
    String testError();
}