package com.manga.ovh.controller;

import com.manga.ovh.dto.ApiResponse;
import com.manga.ovh.dto.UserUpdateRequest;
import com.manga.ovh.entity.User;
import com.manga.ovh.service.S3Service;
import com.manga.ovh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final S3Service s3Service;
    private final UserService userService;

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<Void>> uploadAvatar(
            @RequestParam MultipartFile file,
            Principal principal
    ) {
        User user = userService.getByUsername(principal.getName());
        String imageUrl = s3Service.uploadFile(file, "avatars");

        user.setAvatarUrl(imageUrl);
        userService.updateUser(user, null, null);

        return ResponseEntity.ok(new ApiResponse<>(200, imageUrl, null));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<User>> me(Principal principal) {
        User user = userService.getByUsername(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(200, "Успешно", user));
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestBody UserUpdateRequest request,
            Principal principal
    ) {
        User user = userService.getByUsername(principal.getName());
        userService.updateUser(user, request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(200, "Профиль обновлён", null));
    }

    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteSelf(Principal principal) {
        userService.deleteByUsername(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(200, "Ваш аккаунт удалён", null));
    }

    @PostMapping("/admin/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAny(@RequestParam String username) {
        userService.deleteByUsername(username);
        return ResponseEntity.ok(new ApiResponse<>(200, "Аккаунт удалён админом", null));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(200, "Список пользователей", users));
    }
}
