package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.AuthResponse;
import com.manga.ovh.dto.TelegramAuthRequest;
import com.manga.ovh.entity.User;
import com.manga.ovh.security.JwtTokenProvider;
import com.manga.ovh.service.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class TelegramAuthController {

    private final TelegramAuthService telegramAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/telegram")
    public ResponseEntity<ApiResponse<AuthResponse>> telegramAuth(@RequestBody TelegramAuthRequest request) {
        try {
            User user = telegramAuthService.verifyAndGetUser(request);
            String token = jwtTokenProvider.generateToken(user);

            log.info("Telegram аутентификация успешна для пользователя: {}", user.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(200, "Telegram авторизация успешна", new AuthResponse(token)));

        } catch (IllegalArgumentException e) {
            log.warn("Telegram auth validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, e.getMessage(), null));

        } catch (SecurityException e) {
            log.warn("Telegram auth security check failed: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(401, "Неверная подпись Telegram", null));

        } catch (Exception e) {
            log.error("Telegram auth error", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(500, "Ошибка Telegram авторизации", null));
        }
    }
}
