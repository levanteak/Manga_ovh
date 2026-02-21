package com.manga.ovh.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Вспомогательный контроллер для инициирования OAuth2 авторизации через Google.
 * Фронтенд переходит на GET /api/auth/oauth2/google, а бэкенд
 * перенаправляет на стандартный Spring Security OAuth2 authorization endpoint.
 */
@RestController
@RequestMapping("/api/auth/oauth2")
public class OAuth2RedirectController {

    @GetMapping("/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}
