package com.manga.ovh.service;

import com.manga.ovh.entity.User;
import com.manga.ovh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public void updateUser(User user, String email, String rawPassword) {
        if (email != null) user.setEmail(email);
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        userRepository.save(user);
    }

    public void deleteByUsername(String username) {
        User user = getByUsername(username);
        userRepository.delete(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
