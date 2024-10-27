# Трекер привычек

Приложение для отслеживания и управления личными привычками.

## Как использовать

1. Создайте копию файла .env-localexample, переименуйте его в .env
1. Запустите docker-compose up -d
1. Запустите App.main()
1. Войдите в систему, используя учетную запись администратора, зарегистрируйте новую или используйте ранее зарегистрированную
#### Учетные данные администратора для доступа в админ-меню:
- Электронная почта: admin@example.com
- Пароль: adminpassword
#### Учетные данные пользователя с предзаполненными данными:
- Электронная почта: userOne@example.com
- Пароль: hashedpassword1

### Run database migrations only

java -jar habit-tracker.jar --migrate

### Start in console mode (includes migrations)

java -jar habit-tracker.jar --console

### To deploy to Tomcat:

Build your application:

bashCopymvn clean package

Copy the generated WAR file from target/habit-tracker.war to Tomcat's webapps directory
Start Tomcat:

bashCopy# Windows
%CATALINA_HOME%\bin\startup.bat

# Linux/Mac

$CATALINA_HOME/bin/startup.sh
Your application will be available at:

http://localhost:8080/habit-tracker (if deployed as habit-tracker.war)
API endpoints will be at http://localhost:8080/habit-tracker/api/*

# Habit Tracker API Documentation

## Overview

This documentation provides detailed information about the Habit Tracker API endpoints. The API allows users to manage
their habits, track habit execution, and view progress statistics.

## Base URL

All API URLs described in this documentation have the following base:

```
http://localhost:8080/api
```

## Authentication

Most endpoints require authentication. After successful login, the server creates a session and returns session cookies
that should be included in subsequent requests.

## Response Formats

All responses are in JSON format. Successful responses use standard HTTP status codes (200, 201, 204). Error responses
include an error message and timestamp.

## Common Headers

```
Content-Type: application/json
Accept: application/json
```

## Common Error Responses

Every endpoint may return these error responses:

### 400 Bad Request

Returned when request validation fails.

```json
{
  "message": "Invalid input data",
  "timestamp": 1698247485123
}
```

### 401 Unauthorized

Returned when user is not authenticated.

```json
{
  "message": "User not authenticated",
  "timestamp": 1698247485123
}
```

### 403 Forbidden

Returned when user lacks necessary permissions.

```json
{
  "message": "Insufficient privileges",
  "timestamp": 1698247485123
}
```

### 404 Not Found

Returned when requested resource doesn't exist.

```json
{
  "message": "Resource not found",
  "timestamp": 1698247485123
}
```

### 500 Internal Server Error

Returned when server encounters an error.

```json
{
  "message": "Internal server error occurred",
  "timestamp": 1698247485123
}
```

### 503 Service Unavailable

Returned when service is temporarily unavailable.

```json
{
  "message": "Service temporarily unavailable",
  "timestamp": 1698247485123,
  "details": {
    "component": "database",
    "status": "down"
  }
}
```

## API Sections

1. [Authentication Endpoints](authentication-endpoints.md)
2. [User Management Endpoints](user-management-endpoints.md)
3. [Habit Management Endpoints](habit-management-endpoints.md)
4. [Habit Execution Endpoints](habit-execution-endpoints.md)
5. [System Status Endpoints](system-status-endpoints.md)

## Обзор функциональности

### Регистрация и авторизация пользователей

* Возможность регистрации новых пользователей с уникальным email и паролем.
* Вход в систему с проверкой email и пароля.
* Пароли хранятся в захешированном виде с применением алгоритма SHA-2

### Управление пользователями

* Возможность редактирования профиля пользователя (имя, email, пароль).
* Возможность удаления аккаунта.

### Управление привычками (CRUD)

* Создание привычки: Пользователь может создать новую привычку с указанием названия, описания и частоты (ежедневно, еженедельно).
* Редактирование привычки: Возможность изменения информации о привычке.
* Удаление привычки: Удаление привычки и всей связанной статистики выполнения.
* Просмотр привычек: Возвращение списка всех привычек пользователя с возможностью фильтрации по дате создания или статусу.

###  Отслеживание выполнения привычек

* Пользователь может ежедневно отмечать выполнение привычки.
* Хранение истории выполнения для каждой привычки.
* Генерация статистики выполнения привычки за указанный период

### Статистика и аналитика

* Подсчет текущих серий выполнения привычек (streak).
* Процент успешного выполнения привычек за определенный период.
* Формирование отчета для пользователя по прогрессу выполнения.

### Администрирование

* Возможность блокировки или удаления пользователей.


