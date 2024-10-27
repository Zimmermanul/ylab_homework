# Authentication Endpoints

## Register New User

Create a new user account in the system.

**Endpoint:** `POST /users`

**Request:**

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "name": "John Doe"
}
```

**Validation Rules:**

- Email must be in valid format
- Password must:
    - Be at least 8 characters long
    - Contain at least one uppercase letter
    - Contain at least one lowercase letter
    - Contain at least one number
    - Contain at least one special character
- Name must be at least 2 characters long

**Success Response:** (201 Created)

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "isAdmin": false,
  "isBlocked": false
}
```

**Error Responses:**

- 400 Bad Request

```json
{
  "message": "Invalid email format",
  "timestamp": 1698247485123
}
```

```json
{
  "message": "Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter, and one special character",
  "timestamp": 1698247485123
}
```

```json
{
  "message": "Email already exists",
  "timestamp": 1698247485123
}
```

## User Login

Authenticate a user and create a session.

**Endpoint:** `POST /users/login`

**Request:**

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Success Response:** (200 OK)

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "isAdmin": false,
  "isBlocked": false
}
```

**Error Responses:**

- 401 Unauthorized

```json
{
  "message": "Invalid credentials",
  "timestamp": 1698247485123
}
```

```json
{
  "message": "Account is blocked",
  "timestamp": 1698247485123
}
```

**Notes:**

- Successful login creates a session
- Session cookie is returned in response headers
- Session cookie should be included in subsequent requests

## User Logout

End the current user session.

**Endpoint:** `POST /users/logout`

**Request Body:** None required

**Success Response:** (204 No Content)

**Notes:**

- Invalidates the current session
- No response body returned
- Subsequent requests will require new login

## Password Reset Request

Request a password reset link (if implemented).

**Endpoint:** `POST /users/password-reset-request`

**Request:**

```json
{
  "email": "user@example.com"
}
```

**Success Response:** (200 OK)

```json
{
  "message": "If an account exists with this email, a password reset link has been sent"
}
```

**Notes:**

- For security reasons, returns same response whether email exists or not
- Reset link is sent via email (if email service is configured)
- Reset link typically expires after a set time period

## Validate Session

Check if current session is valid.

**Endpoint:** `GET /users/session`

**Success Response:** (200 OK)

```json
{
  "valid": true,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "isAdmin": false,
    "isBlocked": false
  }
}
```

**Error Response:** (401 Unauthorized)

```json
{
  "message": "Session expired or invalid",
  "timestamp": 1698247485123
}
```

## General Notes on Authentication

### Session Management

- Sessions are managed via HTTP cookies
- Default session timeout is 30 minutes
- Session is automatically extended with activity
- Multiple concurrent sessions are not allowed

### Security Considerations

- Passwords are hashed using secure algorithms
- Failed login attempts may trigger account lockout
- Session tokens are randomly generated and secure
- All authentication endpoints use HTTPS
- Rate limiting may be applied to prevent brute force attacks

### Headers

All requests should include:

```
Content-Type: application/json
Accept: application/json
```

Response includes session cookie:

```
Set-Cookie: JSESSIONID=<session-id>; Path=/; HttpOnly; Secure
```