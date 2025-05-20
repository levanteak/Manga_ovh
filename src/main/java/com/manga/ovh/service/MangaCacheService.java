package com.manga.ovh.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MangaCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String CACHE_KEY = "manga::popular";

    public void cachePopularManga(List<Object> mangaList) {
        redisTemplate.opsForValue().set(CACHE_KEY, mangaList, 1, TimeUnit.HOURS);
    }

    public List<Object> getPopularMangaFromCache() {
        return (List<Object>) redisTemplate.opsForValue().get(CACHE_KEY);
    }

    public void clearPopularCache() {
        redisTemplate.delete(CACHE_KEY);
    }

    public void testRedis() {
        redisTemplate.opsForValue().set("test:key", "Hello Redis", 5, TimeUnit.MINUTES);
        Object value = redisTemplate.opsForValue().get("test:key");
        System.out.println("Redis Value: " + value);
    }

    @PostConstruct
    public void init() {
        testRedis();  // будет вызван при старте приложения
    }
}
