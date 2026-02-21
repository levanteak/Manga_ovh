package com.manga.ovh.security;

import com.manga.ovh.entity.User;
import com.manga.ovh.enums.Role;
import com.manga.ovh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String oauthId = oAuth2User.getAttribute("sub");
        String pictureUrl = oAuth2User.getAttribute("picture");

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Email не получен от Google");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            String username = generateUniqueUsername(email);
            log.info("Создание нового OAuth2 пользователя: email={}, username={}", email, username);

            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .oauthProvider("google")
                    .oauthId(oauthId)
                    .avatarUrl(pictureUrl)
                    .build();
            return userRepository.save(newUser);
        });

        // Если пользователь уже существовал (например, регистрировался вручную),
        // обновляем OAuth данные если их ещё не было
        if (user.getOauthProvider() == null) {
            user.setOauthProvider("google");
            user.setOauthId(oauthId);
            if (user.getAvatarUrl() == null && pictureUrl != null) {
                user.setAvatarUrl(pictureUrl);
            }
            userRepository.save(user);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                Map.of(
                        "sub", oauthId != null ? oauthId : "",
                        "email", email,
                        "name", name != null ? name : user.getUsername(),
                        "username", user.getUsername()
                ),
                "sub"
        );
    }

    private String generateUniqueUsername(String email) {
        String base = email.split("@")[0];
        // Убираем спецсимволы из username
        base = base.replaceAll("[^a-zA-Z0-9_]", "");
        if (base.isBlank()) {
            base = "user";
        }

        String username = base;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + suffix;
            suffix++;
        }
        return username;
    }
}
