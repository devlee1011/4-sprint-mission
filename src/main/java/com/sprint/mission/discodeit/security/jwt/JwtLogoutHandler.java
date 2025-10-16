package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new RuntimeException("cookies is null");

        Optional<Cookie> optionalRefreshCookie = Arrays.stream(cookies)
                .filter(c -> "REFRESH_TOKEN".equals(c.getName()))
                .findFirst();
        if (optionalRefreshCookie.isEmpty()) throw new RuntimeException("refresh cookie is empty");
        Cookie refreshCookie = optionalRefreshCookie.get();

        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        response.addCookie(refreshCookie);
    }
}
