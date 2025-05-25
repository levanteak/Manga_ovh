package com.manga.ovh.dto;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String password;
}