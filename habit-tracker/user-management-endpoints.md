# User Management Endpoints

## Profile Management

### Get Current User Profile

Retrieve the profile of the currently authenticated user.

**Endpoint:** `GET /users/profile`

**Required Headers:**

- Session cookie from login

**Success Response:** (200 OK)

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "isAdmin": false,
  "isBlocked": false,
  "createdAt": "2024-10-25T10:30:00"
}
```

### Update User Email

Update the email address of the current user.

**Endpoint:** `PUT /users/email`

**Request:**

```json
{
  "newEmail": "newemail@example.com",
  "password": "SecurePass123!"
}
```

**Validation Rules:**

- New email must be valid format
- Password must be correct
- New email must not be already in use

**Success Response:** (200 OK)

```json
{
  "id": 1,
  "email": "newemail@example.com",
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
  "message": "Email already in use",
  "timestamp": 1698247485123
}
```

- 401 Unauthorized

```json
{
  "message": "Incorrect password",
  "timestamp": 1698247485123
}
```

### Update User Name

Update the name of the current user.

**Endpoint:** `PUT /users/name`

**Request:**

```json
{
  "newName": "John Smith"
}
```

**Validation Rules:**

- Name must be at least 2 characters long
- Name must not contain special characters

**Success Response:** (200 OK)

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Smith",
  "isAdmin": false,
  "isBlocked": false
}
```

### Update Password

Update the user's password.

**Endpoint:** `PUT /users/password`

**Request:**

```json
{
  "currentPassword": "SecurePass123!",
  "newPassword": "NewSecurePass456!"
}
```

**Validation Rules:**

- Current password must be correct
- New password must meet password requirements:
    - At least 8 characters
    - At least one uppercase letter
    - At least one lowercase letter
    - At least one number
    - At least one special character
- New password must be different from current password

**Success Response:** (200 OK)

```json
{
  "message": "Password updated successfully"
}
```

**Error Responses:**

- 400 Bad Request

```json
{
  "message": "New password does not meet security requirements",
  "timestamp": 1698247485123
}
```

- 401 Unauthorized

```json
{
  "message": "Current password is incorrect",
  "timestamp": 1698247485123
}
```

### Delete User Account

Delete the user's own account.

**Endpoint:** `DELETE /users/{email}`

**Path Parameters:**

- email: The email of the account to delete (must be current user's email unless admin)

**Success Response:** (204 No Content)

**Notes:**

- Deletes all associated data (habits, executions, etc.)
- Session is invalidated after successful deletion
- Requires re-authentication for any further actions

## Administrative Endpoints

These endpoints require admin privileges.

### Get All Users

Retrieve a list of all users in the system.

**Endpoint:** `GET /users`

**Query Parameters:**

- page (optional): Page number (default: 1)
- size (optional): Page size (default: 20)
- sort (optional): Sort field (default: "id")
- order (optional): Sort order ("asc" or "desc", default: "asc")
- search (optional): Search term for email or name

**Success Response:** (200 OK)

```json
{
  "content": [
    {
      "id": 1,
      "email": "user@example.com",
      "name": "John Doe",
      "isAdmin": false,
      "isBlocked": false,
      "createdAt": "2024-10-25T10:30:00"
    }
  ],
  "pagination": {
    "page": 1,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### Block User

Block a user account.

**Endpoint:** `PUT /users/block`

**Request:**

```json
{
  "email": "user@example.com"
}
```

**Success Response:** (200 OK)

```json
{
  "message": "User blocked successfully"
}
```

**Error Responses:**

- 403 Forbidden

```json
{
  "message": "Cannot block admin user",
  "timestamp": 1698247485123
}
```

- 404 Not Found

```json
{
  "message": "User not found",
  "timestamp": 1698247485123
}
```

### Unblock User

Unblock a user account.

**Endpoint:** `PUT /users/unblock`

**Request:**

```json
{
  "email": "user@example.com"
}
```

**Success Response:** (200 OK)

```json
{
  "message": "User unblocked successfully"
}
```

### Admin Delete User

Delete any user account (admin only).

**Endpoint:** `DELETE /users/{email}`

**Path Parameters:**

- email: The email of the account to delete

**Success Response:** (204 No Content)

**Notes:**

- Can delete any user except other admins
- Deletes all associated data
- Operation cannot be undone

## General Notes

### Authorization

- Regular users can only modify their own profile
- Admin users can modify any profile except other admins
- Some operations require password confirmation
- Session token must be included in all requests

### Rate Limiting

- API calls are rate-limited per user
- Default limits:
    - 10 requests per second
    - 1000 requests per hour
- Admin endpoints have separate limits

### Data Validation

- All email addresses are normalized and validated
- Passwords are checked against common password lists
- Names are sanitized to prevent XSS
- All inputs are trimmed of whitespace

### Audit Trail

- All administrative actions are logged
- User profile changes are tracked
- Failed login attempts are recorded