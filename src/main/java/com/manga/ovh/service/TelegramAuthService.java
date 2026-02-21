package com.manga.ovh.service;

import com.manga.ovh.dto.TelegramAuthRequest;
import com.manga.ovh.entity.User;
import com.manga.ovh.enums.Role;
import com.manga.ovh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramAuthService {

    private static final long MAX_AUTH_AGE_SECONDS = 86400;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${telegram.bot-token}")
    private String botToken;

    /**
     * Верифицирует данные Telegram Login Widget и возвращает (или создаёт) пользователя.
     *
     * Алгоритм верификации для Login Widget:
     * 1. Собираем data-check-string из всех полей (кроме hash), отсортированных по ключу, через \n
     * 2. secret_key = SHA256(botToken)
     * 3. computed_hash = HMAC-SHA256(data_check_string, secret_key)
     * 4. Сравниваем computed_hash с переданным hash
     * 5. Проверяем auth_date не старше 86400 секунд
     */
    public User verifyAndGetUser(TelegramAuthRequest request) {
        validateAuthDate(request.authDate());
        validateHash(request);

        String telegramId = String.valueOf(request.id());

        return userRepository.findByOauthProviderAndOauthId("telegram", telegramId)
                .orElseGet(() -> createTelegramUser(request, telegramId));
    }

    private void validateAuthDate(Long authDate) {
        if (authDate == null) {
            throw new IllegalArgumentException("auth_date отсутствует");
        }
        long now = Instant.now().getEpochSecond();
        if (now - authDate > MAX_AUTH_AGE_SECONDS) {
            throw new IllegalArgumentException("Данные Telegram авторизации устарели (старше 24 часов)");
        }
    }

    private void validateHash(TelegramAuthRequest request) {
        try {
            String dataCheckString = buildDataCheckString(request);

            // secret_key = SHA256(botToken) -- для Login Widget
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));

            // computed_hash = HMAC-SHA256(data_check_string, secret_key)
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] hashBytes = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            String computedHash = HexFormat.of().formatHex(hashBytes);

            if (!computedHash.equals(request.hash())) {
                throw new SecurityException("Неверная подпись Telegram данных");
            }
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка верификации Telegram данных", e);
        }
    }

    private String buildDataCheckString(TelegramAuthRequest request) {
        // Собираем все non-null поля в TreeMap (автоматическая алфавитная сортировка)
        TreeMap<String, String> fields = new TreeMap<>();

        fields.put("auth_date", String.valueOf(request.authDate()));
        if (request.firstName() != null) fields.put("first_name", request.firstName());
        if (request.id() != null) fields.put("id", String.valueOf(request.id()));
        if (request.lastName() != null) fields.put("last_name", request.lastName());
        if (request.photoUrl() != null) fields.put("photo_url", request.photoUrl());
        if (request.username() != null) fields.put("username", request.username());

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (var entry : fields.entrySet()) {
            if (!first) sb.append("\n");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return sb.toString();
    }

    private User createTelegramUser(TelegramAuthRequest request, String telegramId) {
        String username = generateUniqueUsername(request.username(), request.firstName());

        log.info("Создание нового Telegram пользователя: telegramId={}, username={}", telegramId, username);

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(Role.USER)
                .oauthProvider("telegram")
                .oauthId(telegramId)
                .avatarUrl(request.photoUrl())
                .build();

        return userRepository.save(user);
    }

    private String generateUniqueUsername(String telegramUsername, String firstName) {
        String base;
        if (telegramUsername != null && !telegramUsername.isBlank()) {
            base = telegramUsername;
        } else if (firstName != null && !firstName.isBlank()) {
            base = firstName;
        } else {
            base = "tg_user";
        }

        base = base.replaceAll("[^a-zA-Z0-9_]", "");
        if (base.isBlank()) {
            base = "tg_user";
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
