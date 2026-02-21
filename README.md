# MangaOVH — Платформа для чтения манги

MangaOVH — это полноценная веб-платформа для чтения манги, манхвы и манхуа. Вдохновлена [inkstory.net](https://inkstory.net).

Пользователи могут читать мангу, оставлять оценки и комментарии, добавлять в закладки и следить за историей чтения. Издатели загружают мангу и главы. Администраторы управляют всем контентом.

---

## Что умеет сайт

### Для читателей
- Просмотр каталога манги с фильтрацией по жанру, статусу, категории, стране, году
- Полнотекстовый поиск через Elasticsearch
- Чтение глав с постраничным просмотром
- Оценки от 1 до 10
- Лайки и комментарии
- Закладки (Читаю / Прочитано / Запланировано / Брошено)
- История чтения с сохранением последней страницы
- Личный профиль с аватаром

### Для издателей
- Панель издателя: загрузка манги, обложек, глав и страниц
- Управление своими тайтлами

### Для администраторов
- Панель администратора: управление всеми тайтлами, пользователями и тегами

### Авторизация
- Регистрация и вход по логину/паролю
- Вход через Google аккаунт (OAuth2)
- Вход через Telegram (Login Widget)

---

## Стек технологий

### Бэкенд
| Технология | Версия | Назначение |
|---|---|---|
| Java | 17 | Язык |
| Spring Boot | 3.4.5 | Фреймворк |
| Spring Security | — | JWT + OAuth2 |
| PostgreSQL | 16 | База данных |
| Redis | 7 | Кеширование |
| Elasticsearch | 8.13.2 | Полнотекстовый поиск |
| AWS S3 | — | Хранение обложек и страниц |
| Docker | — | Контейнеризация |

### Фронтенд
| Технология | Назначение |
|---|---|
| Vue 3 | UI фреймворк |
| Pinia | Управление состоянием |
| Vue Router 4 | Маршрутизация |
| Vite 7 | Сборщик |
| Axios | HTTP клиент |

---

## Быстрый старт

### Требования
- Docker Desktop (запущен)
- Node.js 18+
- Git

---

### 1. Запуск бэкенда

```bash
git clone <repo-url>
cd Manga_ovh
docker compose up --build
```

Docker автоматически соберёт JAR и запустит все сервисы. Первый запуск ~5-10 минут.

| Сервис | URL |
|---|---|
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5433 |
| Redis | localhost:6379 |
| Elasticsearch | http://localhost:9200 |
| Kibana | http://localhost:5601 |

---

### 2. Запуск фронтенда

```bash
cd manga-ovh-frontend
npm install
npm run dev
```

Фронт запустится на http://localhost:3000

---

### 3. Настройка OAuth2 (опционально)

#### Google
1. Зайди в [Google Cloud Console](https://console.cloud.google.com)
2. APIs & Services → Credentials → OAuth 2.0 Client
3. Добавь Authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
4. Пропиши в `src/main/resources/application.yml`:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ВАШ_CLIENT_ID
            client-secret: ВАШ_CLIENT_SECRET
```

#### Telegram
1. Создай бота через `@BotFather` в Telegram
2. Получи токен бота
3. Для локальной разработки запусти публичный тоннель:
```bash
ssh -R 80:localhost:3000 serveo.net
```
4. В `@BotFather` выполни `/setdomain` → укажи домен из тоннеля (без https://)
5. Пропиши в `application.yml`:
```yaml
telegram:
  bot-token: ВАШ_BOT_TOKEN
```
6. В `src/views/LoginView.vue` замени имя бота:
```js
script.setAttribute('data-telegram-login', 'ИМЯ_ВАШЕГО_БОТА')
```

---

## Полезные команды Docker

```bash
# Статус контейнеров
docker compose ps

# Логи бэкенда в реальном времени
docker compose logs -f backend

# Пересобрать только бэкенд
docker compose up --build backend

# Остановить всё (данные сохранятся)
docker compose down

# Остановить и удалить все данные
docker compose down -v
```

---

## Структура проекта

```
Manga_ovh/                          ← Бэкенд (Spring Boot)
├── src/main/java/com/manga/ovh/
│   ├── config/                     — CORS, Security, Cache, Elasticsearch
│   ├── controller/                 — REST контроллеры
│   ├── dto/                        — Request/Response объекты
│   ├── entity/                     — JPA сущности
│   ├── enums/                      — MangaStatus, Role, BookmarkStatus
│   ├── exception/                  — GlobalExceptionHandler
│   ├── repository/                 — Spring Data JPA + Specification
│   ├── security/                   — JWT, OAuth2, Telegram
│   └── service/                    — Бизнес-логика
├── docker-compose.yml
├── Dockerfile
└── Manga_OVH.postman_collection.json

manga-ovh-frontend/                 ← Фронтенд (Vue 3)
├── src/
│   ├── api/                        — axios клиенты для каждого ресурса
│   ├── components/                 — NavBar, MangaCard, StarRating, ...
│   ├── router/                     — маршруты с role-guard
│   ├── stores/                     — auth, theme (Pinia)
│   └── views/                      — страницы приложения
└── vite.config.js
```

---

## API — краткий справочник

Формат всех ответов:
```json
{ "status": 200, "message": "...", "data": {} }
```

| Метод | URL | Описание | Доступ |
|---|---|---|---|
| POST | /api/auth/register | Регистрация | Публичный |
| POST | /api/auth/login | Вход | Публичный |
| GET | /api/auth/oauth2/google | Вход через Google | Публичный |
| POST | /api/auth/telegram | Вход через Telegram | Публичный |
| GET | /api/manga | Список манг с фильтрами | Публичный |
| GET | /api/manga/{id} | Манга по ID | Публичный |
| POST | /api/manga | Создать мангу | PUBLISHER |
| PUT | /api/manga/{id} | Обновить мангу | ADMIN |
| DELETE | /api/manga/{id} | Удалить мангу | ADMIN |
| GET | /api/chapters/{mangaId} | Список глав | Публичный |
| POST | /api/chapters/{mangaId} | Создать главу | PUBLISHER |
| GET | /api/pages/{chapterId} | Страницы главы | Публичный |
| POST | /api/pages/{chapterId} | Загрузить страницы | PUBLISHER |
| POST | /api/ratings/{mangaId} | Поставить оценку | USER |
| GET | /api/ratings/{mangaId} | Рейтинг манги | Публичный |
| POST | /api/likes/{mangaId} | Лайкнуть | USER |
| POST | /api/comments/{mangaId} | Добавить комментарий | USER |
| GET | /api/comments/{mangaId} | Комментарии | Публичный |
| POST | /api/bookmarks/{mangaId} | Добавить закладку | USER |
| GET | /api/bookmarks | Мои закладки | USER |
| POST | /api/history | Сохранить прогресс | USER |
| GET | /api/history | История чтения | USER |
| GET | /api/search?keyword= | Поиск по названию | Публичный |
| GET | /api/tags | Все теги | Публичный |
| POST | /api/tags | Создать тег | ADMIN |
| GET | /api/user/me | Мой профиль | USER |
| POST | /api/user/avatar | Загрузить аватар | USER |
| GET | /api/user/all | Все пользователи | ADMIN |

Полная документация: http://localhost:8080/swagger-ui.html

---

## Переменные окружения (для продакшена)

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/manga_ovh
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=secret
AWS_S3_ACCESS_KEY=your_key
AWS_S3_SECRET_KEY=your_secret
JWT_SECRET=your_256bit_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
TELEGRAM_BOT_TOKEN=your_bot_token
OAUTH2_FRONTEND_REDIRECT=https://yourdomain.com/oauth2/callback
```
