package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.TokenPair;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    //
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        log.debug("CSRF 토큰 요청");
        log.trace("CSRF 토큰: {}", csrfToken.getToken());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("role")
    public ResponseEntity<UserDto> updateRole(@RequestBody RoleUpdateRequest request) {
        log.info("권한 수정 요청");
        UserDto userDto = authService.updateRole(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Refresh Token으로 Access Token 재발급 요청");
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) throw new RuntimeException("No Cookies found in request");

            Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                    .filter(c -> "REFRESH_TOKEN".equals(c.getName()))
                    .findFirst();

            if (refreshCookie.isEmpty()) {
                throw new RuntimeException("Refresh Token이 쿠키에 존재하지 않음");
            }

            String refreshToken = refreshCookie.get().getValue();
            TokenPair tokenPair = authService.refreshTokens(refreshToken);

            // Refresh Token Rotation
            Cookie newRefreshToken = new Cookie("REFRESH_TOKEN", tokenPair.refreshToken());
            newRefreshToken.setHttpOnly(true);
            newRefreshToken.setSecure(true);
            newRefreshToken.setPath("/");
            newRefreshToken.setMaxAge(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60);
            response.addCookie(newRefreshToken);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(tokenPair.jwtDto());

        } catch (RuntimeException e) {
            log.error("토큰 재발급 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e, HttpStatus.UNAUTHORIZED.value()));
        }
    }
}
