package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.entity.User;
import com.manga.ovh.repository.UserRepository;
import com.manga.ovh.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final S3Service s3Service;
    private final UserRepository userRepository;

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<Void>> uploadAvatar(
            @RequestParam MultipartFile file,
            Principal principal
    ) {
        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String imageUrl = s3Service.uploadFile(file, "avatars");

        user.setAvatarUrl(imageUrl);
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<>(200, imageUrl, null));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> me(Principal principal) {
        System.out.println("🔐 Principal: " + principal.getName());

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return ResponseEntity.ok(new ApiResponse<>(200, "Успешно", user));
    }


}
