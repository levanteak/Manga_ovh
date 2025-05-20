package com.manga.ovh.dto;

import lombok.Data;
import com.manga.ovh.enums.Role;


@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
    private Integer age;

    private String publisherName;
    private String publisherCountry;
    private String publisherWebsite;
}

