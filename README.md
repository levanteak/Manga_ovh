# Manga OVH — Backend API

Spring Boot 3 бэкенд для платформы чтения манги. Вдохновлён [inkstory.net](https://inkstory.net).

---

## Технологии

| Технология | Версия | Назначение |
|-----------|--------|------------|
| Java | 17 | Язык |
| Spring Boot | 3.4.5 | Фреймворк |
| PostgreSQL | 16 | База данных |
| Redis | 7 | Кеширование |
| Elasticsearch | 8.13.2 | Поиск |
| AWS S3 | — | Хранение файлов (обложки, страницы) |
| Docker | — | Контейнеризация |
| JWT | — | Аутентификация |

---

## Быстрый старт

### Требования
- Docker Desktop установлен и запущен
- Git

### Запуск (одна команда)

```bash
git clone <repo-url>
cd Manga_ovh
docker compose up --build
```

Docker сам соберёт JAR и запустит все сервисы. Первый запуск занимает ~5-10 минут (загрузка зависимостей).

### Что запускается

| Сервис | URL | Описание |
|--------|-----|----------|
| **Backend API** | http://localhost:8080 | Spring Boot |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Документация API |
| **PostgreSQL** | localhost:5433 | БД (postgres/postgres) |
| **Redis** | localhost:6379 | Кеш |
| **Elasticsearch** | http://localhost:9200 | Поиск |
| **Kibana** | http://localhost:5601 | UI для Elasticsearch |

### Полезные команды Docker

```bash
# Статус контейнеров
docker compose ps

# Логи бэкенда в реальном времени
docker compose logs -f backend

# Перезапустить только бэкенд после изменений кода
docker compose up --build backend

# Остановить всё (данные сохранятся)
docker compose down

# Остановить и удалить все данные (БД сбрасывается)
docker compose down -v
```

---

## Импорт Postman коллекции

Файл: `Manga_OVH.postman_collection.json`

1. Открой Postman → **Import**
2. Выбери файл `Manga_OVH.postman_collection.json`
3. После логина токен сохраняется автоматически в `{{token}}`

---

## API — Все эндпоинты

### Формат ответа

Все эндпоинты возвращают единый формат:

```json
{
  "status": 200,
  "message": "Описание",
  "data": {}
}
```

### Базовый URL

```
http://localhost:8080
```

### Аутентификация

Все защищённые эндпоинты требуют заголовок:
```
Authorization: Bearer <token>
```

---

## Auth — `/api/auth`

### POST `/api/auth/register` — Регистрация 🔓

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER",
  "age": 20
}
```

Для роли `PUBLISHER` дополнительно обязательны:
```json
{
  "role": "PUBLISHER",
  "publisherName": "Shueisha",
  "publisherCountry": "Japan",
  "publisherWebsite": "https://shueisha.co.jp"
}
```

**Ответ:** `200 OK` / `400` если пользователь уже существует

---

### POST `/api/auth/login` — Вход 🔓

```json
{
  "username": "john",
  "password": "password123"
}
```

**Ответ:**
```json
{
  "status": 200,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

---

## Manga — `/api/manga`

### GET `/api/manga` — Список манг с фильтрацией 🔓

Все параметры опциональны:

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| `status` | enum | — | `ONGOING` `COMPLETED` `HIATUS` `DROPPED` |
| `category` | enum | — | `MANGA` `MANHWA` `MANHUA` `WEBTOON` `DOUJINSHI` |
| `genre` | string | — | Название жанра |
| `tag` | string | — | Название тега |
| `country` | string | — | Страна |
| `releaseYear` | int | — | Год выхода |
| `sortBy` | string | `createdAt` | `createdAt` `views` `averageRating` `title` |
| `sortDir` | string | `DESC` | `ASC` `DESC` |
| `page` | int | `0` | Номер страницы (с нуля) |
| `size` | int | `20` | Размер страницы |

**Пример:**
```
GET /api/manga?status=ONGOING&genre=Action&sortBy=averageRating&sortDir=DESC&page=0&size=20
```

**Ответ:**
```json
{
  "data": {
    "content": [{ "id": "...", "title": "Naruto" }],
    "totalElements": 150,
    "totalPages": 8,
    "number": 0,
    "size": 20
  }
}
```

---

### GET `/api/manga/{id}` — Получить мангу по ID 🔓

**Ответ — MangaDto:**
```json
{
  "id": "uuid",
  "title": "Naruto",
  "originalTitle": "ナルト",
  "description": "История о ниндзя...",
  "status": "COMPLETED",
  "category": "MANGA",
  "releaseYear": 1999,
  "country": "Japan",
  "author": "Masashi Kishimoto",
  "artist": "Masashi Kishimoto",
  "coverUrl": "https://s3.../cover.jpg",
  "publisherName": "Shueisha",
  "views": 10000,
  "likes": 500,
  "averageRating": 9.1,
  "ratingCount": 234,
  "genres": ["Action", "Adventure", "Shounen"],
  "tags": ["ninja", "friendship"]
}
```

---

### POST `/api/manga` — Создать мангу 🔒

`multipart/form-data`:

| Поле | Тип | Обязательно |
|------|-----|-------------|
| `cover` | file | ✅ |
| `title` | string | ✅ |
| `originalTitle` | string | — |
| `description` | string | ✅ |
| `status` | enum | ✅ |
| `category` | enum | ✅ |
| `releaseYear` | int | ✅ |
| `country` | string | ✅ |
| `author` | string | ✅ |
| `artist` | string | ✅ |
| `publisherName` | string | ✅ |
| `genres` | string[] | ✅ (несколько полей `genres`) |
| `tags` | string[] | — (несколько полей `tags`) |

**Ответ:** `UUID` созданной манги

---

### PUT `/api/manga/{id}` — Обновить мангу 🔒 `ADMIN`

Те же поля что и при создании. Обложка опциональна.

---

### DELETE `/api/manga/{id}` — Удалить мангу 🔒 `ADMIN`

---

## Chapters — `/api/chapters`

### POST `/api/chapters/{mangaId}` — Создать главу 🔒

```json
{
  "number": 1,
  "title": "Начало"
}
```

---

### GET `/api/chapters/{mangaId}` — Список глав 🔓

**Ответ:** массив Chapter с полями `id`, `number`, `title`, `releaseDate`, `views`

---

## Pages — `/api/pages`

### POST `/api/pages/{chapterId}` — Загрузить страницы 🔒

`multipart/form-data`: несколько файлов с ключом `images`

**Ответ:**
```json
[
  { "pageNumber": 1, "imageUrl": "https://s3.../page1.jpg" },
  { "pageNumber": 2, "imageUrl": "https://s3.../page2.jpg" }
]
```

---

### GET `/api/pages/{chapterId}` — Страницы главы 🔓

Тот же формат ответа.

---

## Ratings — `/api/ratings`

### POST `/api/ratings/{mangaId}` — Поставить оценку 🔒

```json
{ "score": 9 }
```

`score` — от `1` до `10`. Если уже ставил — обновляет. Автоматически пересчитывает `averageRating` манги.

**Ответ:**
```json
{
  "averageRating": 8.7,
  "totalRatings": 43,
  "userScore": 9
}
```

---

### GET `/api/ratings/{mangaId}` — Рейтинг манги 🔓

Тот же формат. `userScore` = `null` если не авторизован.

---

## Likes — `/api/likes`

### POST `/api/likes/{mangaId}` — Лайкнуть мангу 🔒

Идемпотентно — повторный вызов не создаёт дубликат.

---

### GET `/api/likes/{mangaId}` — Количество лайков 🔓

**Ответ:** `"data": 423`

---

## Comments — `/api/comments`

### POST `/api/comments/{mangaId}` — Добавить комментарий 🔒

```json
{ "content": "Отличная манга!" }
```

---

### GET `/api/comments/{mangaId}` — Комментарии манги 🔓

**Ответ:**
```json
[
  {
    "username": "john",
    "content": "Отличная манга!",
    "createdAt": "2026-02-20T12:00:00"
  }
]
```

---

## Bookmarks — `/api/bookmarks`

### POST `/api/bookmarks/{mangaId}` — Добавить / изменить закладку 🔒

```json
{ "status": "READING" }
```

Статусы: `READING` `COMPLETED` `PLANNED` `DROPPED`

**Ответ:**
```json
{
  "mangaId": "uuid",
  "mangaTitle": "Naruto",
  "coverUrl": "https://...",
  "status": "READING",
  "updatedAt": "2026-02-20T12:00:00"
}
```

---

### GET `/api/bookmarks` — Мои закладки 🔒

Опциональный параметр `?status=READING` — фильтр по статусу.

---

### DELETE `/api/bookmarks/{mangaId}` — Удалить закладку 🔒

---

## Reading History — `/api/history`

### POST `/api/history` — Сохранить прогресс чтения 🔒

```json
{
  "mangaId": "uuid",
  "chapterId": "uuid",
  "lastPage": 12
}
```

Если запись уже есть — обновляет.

---

### GET `/api/history` — История чтения 🔒

**Ответ:** отсортировано по дате (новые сначала)
```json
[
  {
    "mangaId": "uuid",
    "mangaTitle": "Naruto",
    "coverUrl": "https://...",
    "chapterId": "uuid",
    "chapterNumber": 5,
    "lastPage": 12,
    "updatedAt": "2026-02-20T12:00:00"
  }
]
```

---

### DELETE `/api/history/{mangaId}` — Удалить запись истории 🔒

---

## Tags — `/api/tags`

### POST `/api/tags?name=ninja` — Создать тег 🔒 `ADMIN`

---

### GET `/api/tags` — Все теги 🔓

**Ответ:** `[{ "id": "uuid", "name": "ninja" }]`

---

## Search — `/api/search`

### GET `/api/search?keyword=naruto` — Полнотекстовый поиск 🔓

Поиск через Elasticsearch по названию манги.

**Ответ:**
```json
[
  {
    "id": "uuid",
    "title": "Naruto",
    "description": "...",
    "genres": ["Action", "Shounen"]
  }
]
```

---

### POST `/api/search/add` — Добавить в индекс 🔒

```json
{
  "id": "uuid",
  "title": "Naruto",
  "description": "История о ниндзя",
  "genres": ["Action", "Shounen"]
}
```

---

## User — `/api/user`

### GET `/api/user/me` — Мой профиль 🔒

---

### POST `/api/user/avatar` — Загрузить аватар 🔒

`multipart/form-data`: поле `file`

---

### POST `/api/user/edit` — Изменить профиль 🔒

```json
{
  "email": "new@email.com",
  "password": "newpassword"
}
```

Оба поля опциональны.

---

### POST `/api/user/delete` — Удалить свой аккаунт 🔒

---

### POST `/api/user/admin/delete?username=john` — Удалить пользователя 🔒 `ADMIN`

---

### GET `/api/user/all` — Все пользователи 🔒 `ADMIN`

---

## Условные обозначения

| Иконка | Значение |
|--------|----------|
| 🔓 | Публичный — токен не нужен |
| 🔒 | Требует `Authorization: Bearer <token>` |
| 🔒 `ADMIN` | Только для роли `ADMIN` |

---

## Структура проекта

```
src/main/java/com/manga/ovh/
├── config/          — SecurityConfig, CacheConfig, CorsConfig, ElasticsearchConfig
├── controller/      — REST контроллеры
├── dto/             — Request/Response объекты
├── entity/          — JPA сущности (Manga, User, Chapter, Page, ...)
├── enums/           — MangaStatus, MangaCategory, Role, BookmarkStatus
├── exception/       — GlobalExceptionHandler
├── repository/      — Spring Data JPA репозитории + MangaSpecification
├── security/        — JwtTokenProvider, JwtAuthenticationFilter
└── service/         — Бизнес-логика
```

---

## Переменные окружения (для продакшена)

Перед деплоем вынесите секреты из `application.yml` в env-переменные:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/manga_ovh
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=secret
AWS_S3_ACCESS_KEY=your_key
AWS_S3_SECRET_KEY=your_secret
JWT_SECRET=your_256bit_secret
```
