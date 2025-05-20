package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.AuthRequest;
import com.manga.ovh.dto.AuthResponse;
import com.manga.ovh.dto.RegisterRequest;
import com.manga.ovh.entity.Publisher;
import com.manga.ovh.entity.User;
import com.manga.ovh.enums.Role;
import com.manga.ovh.repository.PublisherRepository;
import com.manga.ovh.repository.UserRepository;
import com.manga.ovh.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final PublisherRepository publisherRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            return ResponseEntity
                    .status(400)
                    .body(new ApiResponse<>(400, "Пользователь уже существует", null));
        }

        Publisher publisher = null;

        if (req.getRole() == Role.PUBLISHER) {
            // Проверяем обязательные поля издательства
            if (isBlank(req.getPublisherName()) || isBlank(req.getPublisherCountry()) || isBlank(req.getPublisherWebsite())) {
                return ResponseEntity
                        .status(400)
                        .body(new ApiResponse<>(400, "Для роли PUBLISHER необходимо заполнить название, страну и сайт издательства", null));
            }

            publisher = publisherRepo.findByName(req.getPublisherName())
                    .orElseGet(() -> publisherRepo.save(
                            Publisher.builder()
                                    .name(req.getPublisherName())
                                    .country(req.getPublisherCountry())
                                    .website(req.getPublisherWebsite())
                                    .build()
                    ));
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .age(req.getAge())
                .publisher(publisher)
                .build();

        userRepo.save(user);
        return ResponseEntity.ok(new ApiResponse<>(200, "Регистрация успешна", null));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            User user = userRepo.findByUsername(req.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                return ResponseEntity
                        .status(401)
                        .body(new ApiResponse<>(401, "Неверный пароль", null));
            }

            String token = jwtProvider.generateToken(user);
            return ResponseEntity.ok(new ApiResponse<>(200, "Успешный вход", new AuthResponse(token)));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(404)
                    .body(new ApiResponse<>(404,"Ошибка", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(new ApiResponse<>(500, "Ошибка входа: ", e.getMessage()));
        }
    }




}
