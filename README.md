# For run Docker :

# 1. Пересобери .jar
```
./mvnw clean package -DskipTests
```

# 2. Перезапусти контейнер
```
docker compose down
docker compose up --build
```
