package com.manga.ovh.security;

import com.manga.ovh.entity.User;
import com.manga.ovh.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${oauth2.frontend-redirect-url}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getAttribute("username");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("OAuth2 пользователь не найден: " + username));

        String token = jwtTokenProvider.generateToken(user);

        String redirectUrl = frontendRedirectUrl + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        log.info("OAuth2 успешная аутентификация для пользователя: {}, редирект на фронт", username);

        response.sendRedirect(redirectUrl);
    }
}
