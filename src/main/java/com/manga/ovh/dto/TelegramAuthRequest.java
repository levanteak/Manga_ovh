package com.manga.ovh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TelegramAuthRequest(
        Long id,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        String username,

        @JsonProperty("photo_url")
        String photoUrl,

        @JsonProperty("auth_date")
        Long authDate,

        String hash
) {}
