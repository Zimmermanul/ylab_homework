# Habit Management Endpoints

## Create and Manage Habits

### Create New Habit

Create a new habit for the current user.

**Endpoint:** `POST /habits`

**Request:**

```json
{
  "name": "Morning Exercise",
  "description": "30 minutes of exercise every morning",
  "frequency": "DAILY"
}
```

**Validation Rules:**

- Name is required and must be 1-100 characters
- Description is optional but must not exceed 500 characters
- Frequency must be one of: "DAILY", "WEEKLY"
- User cannot exceed maximum habit limit (default: 20 active habits)

**Success Response:** (201 Created)

```json
{
  "id": 1,
  "userId": 1,
  "name": "Morning Exercise",
  "description": "30 minutes of exercise every morning",
  "frequency": "DAILY",
  "creationDate": "2024-10-25",
  "isActive": true
}
```

**Error Responses:**

- 400 Bad Request

```json
{
  "message": "Habit name is required",
  "timestamp": 1698247485123
}
```

```json
{
  "message": "Invalid frequency value",
  "timestamp": 1698247485123
}
```

```json
{
  "message": "Maximum number of active habits reached",
  "timestamp": 1698247485123
}
```

### Get All Habits

Retrieve all habits for the current user with optional filtering.

**Endpoint:** `GET /habits`

**Query Parameters:**

- date (optional): Filter by date (YYYY-MM-DD)
- active (optional): Filter by active status (true/false)
- frequency (optional): Filter by frequency (DAILY/WEEKLY)
- search (optional): Search in name and description
- sort (optional): Sort field (name, creationDate, lastExecution)
- order (optional): Sort order (asc/desc)
- page (optional): Page number (default: 1)
- size (optional): Page size (default: 20)

**Success Response:** (200 OK)

```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "name": "Morning Exercise",
      "description": "30 minutes of exercise every morning",
      "frequency": "DAILY",
      "creationDate": "2024-10-25",
      "isActive": true,
      "lastExecution": "2024-10-25",
      "currentStreak": 5,
      "totalCompletions": 15
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

### Get Single Habit

Retrieve detailed information about a specific habit.

**Endpoint:** `GET /habits/{habitId}`

**Path Parameters:**

- habitId: ID of the habit to retrieve

**Success Response:** (200 OK)

```json
{
  "id": 1,
  "userId": 1,
  "name": "Morning Exercise",
  "description": "30 minutes of exercise every morning",
  "frequency": "DAILY",
  "creationDate": "2024-10-25",
  "isActive": true,
  "statistics": {
    "currentStreak": 5,
    "longestStreak": 10,
    "totalCompletions": 15,
    "completionRate": 75.5,
    "lastExecution": "2024-10-25"
  }
}
```

**Error Response:**

- 404 Not Found

```json
{
  "message": "Habit not found",
  "timestamp": 1698247485123
}
```

### Update Habit

Update an existing habit.

**Endpoint:** `PUT /habits/{habitId}`

**Path Parameters:**

- habitId: ID of the habit to update

**Request:**

```json
{
  "name": "Morning Yoga",
  "description": "45 minutes of yoga every morning",
  "frequency": "DAILY"
}
```

**Validation Rules:**

- Same as create habit
- Cannot update habits that have been deleted
- Partial updates are allowed (only include fields to update)

**Success Response:** (200 OK)

```json
{
  "id": 1,
  "userId": 1,
  "name": "Morning Yoga",
  "description": "45 minutes of yoga every morning",
  "frequency": "DAILY",
  "creationDate": "2024-10-25",
  "isActive": true
}
```

**Error Responses:**

- 404 Not Found

```json
{
  "message": "Habit not found",
  "timestamp": 1698247485123
}
```

- 400 Bad Request

```json
{
  "message": "Invalid update data",
  "timestamp": 1698247485123
}
```

### Delete Habit

Mark a habit as deleted (soft delete).

**Endpoint:** `DELETE /habits/{habitId}`

**Path Parameters:**

- habitId: ID of the habit to delete

**Success Response:** (204 No Content)

**Notes:**

- Habit is marked as inactive but preserved for historical data
- Associated executions are preserved
- Habit can be restored by admin if needed

### Bulk Operations

### Bulk Create Habits

Create multiple habits at once.

**Endpoint:** `POST /habits/bulk`

**Request:**

```json
{
  "habits": [
    {
      "name": "Morning Exercise",
      "description": "30 minutes exercise",
      "frequency": "DAILY"
    },
    {
      "name": "Read Books",
      "description": "30 minutes reading",
      "frequency": "DAILY"
    }
  ]
}
```

**Success Response:** (201 Created)

```json
{
  "created": [
    {
      "id": 1,
      "name": "Morning Exercise",
      "frequency": "DAILY"
    },
    {
      "id": 2,
      "name": "Read Books",
      "frequency": "DAILY"
    }
  ],
  "failed": []
}
```

### Bulk Delete Habits

Delete multiple habits at once.

**Endpoint:** `DELETE /habits/bulk`

**Request:**

```json
{
  "habitIds": [1, 2, 3]
}
```

**Success Response:** (200 OK)

```json
{
  "deleted": [1, 2, 3],
  "failed": []
}
```

## Category Management

### Add Habit to Category

Add a habit to a category for better organization.

**Endpoint:** `PUT /habits/{habitId}/category/{categoryId}`

**Success Response:** (200 OK)

```json
{
  "message": "Habit added to category successfully"
}
```

### Remove Habit from Category

Remove a habit from a category.

**Endpoint:** `DELETE /habits/{habitId}/category/{categoryId}`

**Success Response:** (204 No Content)

## General Notes

### Permissions

- Users can only access their own habits
- Admins can access all habits
- Bulk operations are limited to 50 items per request

### Rate Limits

- Standard endpoints: 100 requests per minute
- Bulk endpoints: 10 requests per minute

### Data Retention

- Deleted habits are preserved for 30 days
- Historical data is maintained indefinitely
- Habit metadata is cached for performance

### Validation

- All text fields are sanitized
- Dates must be in YYYY-MM-DD format
- IDs must be positive integers