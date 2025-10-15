package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.TokenPair;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;
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

    @GetMapping("me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        log.info("내 정보 조회 요청");
        UUID userId = userDetails.getUserDto().id();
        UserDto userDto = userService.find(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
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
          Cookie newCookie = new Cookie("REFRESH_TOKEN", tokenPair.refreshToken());
          newCookie.setHttpOnly(true);
          newCookie.setSecure(true);
          newCookie.setPath("/");
          newCookie.setMaxAge(jwtTokenProvider.getRefreshTokenExpirationMinutes() * 60);
          response.addCookie(newCookie);

          return ResponseEntity.ok(tokenPair.jwtDto());

        } catch (RuntimeException e) {
          log.error("토큰 재발급 실패", e);
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                  .body(new ErrorResponse(e, HttpStatus.UNAUTHORIZED.value()));
        }
    }
}
