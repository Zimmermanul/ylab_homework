# Habit Execution Endpoints

## Track Habit Executions

### Record Habit Execution

Track the completion status of a habit for a specific date.

**Endpoint:** `POST /habit-executions/track/{habitId}`

**Path Parameters:**

- habitId: ID of the habit being tracked

**Request:**

```json
{
  "date": "2024-10-25",
  "completed": true,
  "notes": "Completed morning workout - 30 minutes"  // Optional
}
```

**Validation Rules:**

- Date must not be in the future
- Date must not be older than habit creation date
- Notes field is optional and limited to 500 characters

**Success Response:** (200 OK)

```json
{
  "id": 1,
  "habitId": 1,
  "date": "2024-10-25",
  "completed": true,
  "notes": "Completed morning workout - 30 minutes",
  "timestamp": "2024-10-25T08:30:00Z"
}
```

**Error Responses:**

- 400 Bad Request

```json
{
  "message": "Cannot track execution for future date",
  "timestamp": 1698247485123
}
```

```json
{
  "message": "Date cannot be before habit creation date",
  "timestamp": 1698247485123
}
```

### Bulk Track Executions

Record multiple habit executions at once.

**Endpoint:** `POST /habit-executions/track/bulk`

**Request:**

```json
{
  "executions": [
    {
      "habitId": 1,
      "date": "2024-10-25",
      "completed": true
    },
    {
      "habitId": 2,
      "date": "2024-10-25",
      "completed": false
    }
  ]
}
```

**Success Response:** (200 OK)

```json
{
  "successful": [
    {
      "habitId": 1,
      "date": "2024-10-25",
      "status": "recorded"
    }
  ],
  "failed": [
    {
      "habitId": 2,
      "date": "2024-10-25",
      "error": "Invalid habit ID"
    }
  ]
}
```

## Retrieve Execution Data

### Get Execution History

Retrieve execution history for a specific habit.

**Endpoint:** `GET /habit-executions/history/{habitId}`

**Query Parameters:**

- startDate: Start date for history (YYYY-MM-DD)
- endDate: End date for history (YYYY-MM-DD)
- completed (optional): Filter by completion status (true/false)
- page (optional): Page number (default: 1)
- size (optional): Page size (default: 20)

**Success Response:** (200 OK)

```json
{
  "content": [
    {
      "id": 1,
      "habitId": 1,
      "date": "2024-10-25",
      "completed": true,
      "notes": "Completed morning workout - 30 minutes",
      "timestamp": "2024-10-25T08:30:00Z"
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

### Get Statistics

Get detailed statistics for a habit within a date range.

**Endpoint:** `GET /habit-executions/statistics/{habitId}`

**Query Parameters:**

- startDate: Start date for statistics (YYYY-MM-DD)
- endDate: End date for statistics (YYYY-MM-DD)

**Success Response:** (200 OK)

```json
{
  "habit": {
    "id": 1,
    "name": "Morning Exercise"
  },
  "statistics": {
    "currentStreak": 5,
    "successPercentage": 85.5,
    "totalExecutions": 20,
    "completedExecutions": 17,
    "missedExecutions": 3,
    "completionsByDay": {
      "MONDAY": 3,
      "TUESDAY": 4,
      "WEDNESDAY": 3,
      "THURSDAY": 2,
      "FRIDAY": 3,
      "SATURDAY": 1,
      "SUNDAY": 1
    },
    "streaks": {
      "longest": 10,
      "average": 4.5,
      "current": 5
    },
    "timeOfDay": {
      "morning": 12,
      "afternoon": 5,
      "evening": 0
    }
  }
}
```

### Get Progress Report

Get a detailed progress report with analysis and suggestions.

**Endpoint:** `GET /habit-executions/progress/{habitId}`

**Query Parameters:**

- startDate: Start date for report (YYYY-MM-DD)
- endDate: End date for report (YYYY-MM-DD)
- includeAnalysis (optional): Include detailed analysis (default: true)
- includeSuggestions (optional): Include improvement suggestions (default: true)

**Success Response:** (200 OK)

```json
{
  "habit": {
    "id": 1,
    "name": "Morning Exercise"
  },
  "period": {
    "start": "2024-10-01",
    "end": "2024-10-25"
  },
  "report": {
    "overview": "Detailed analysis of your habit progress...",
    "improvingTrend": true,
    "longestStreak": 7,
    "analysis": {
      "strengths": [
        "Consistent morning completion",
        "Strong weekday performance"
      ],
      "weaknesses": [
        "Weekend completion rate lower",
        "Occasional skips after long streaks"
      ]
    },
    "suggestions": [
      "Try completing this habit earlier in the day",
      "You're more successful on weekdays, consider adjusting weekend routine",
      "Setting a reminder might help maintain streaks"
    ]
  },
  "trends": {
    "weeklyCompletion": [
      {
        "week": "2024-W40",
        "completionRate": 85.7
      }
    ],
    "monthlyProgress": {
      "previousMonth": 75.0,
      "currentMonth": 85.5,
      "improvement": 10.5
    }
  }
}
```

### Update Execution

Modify an existing habit execution record.

**Endpoint:** `PUT /habit-executions/{executionId}`

**Request:**

```json
{
  "completed": true,
  "notes": "Updated workout notes"
}
```

**Success Response:** (200 OK)

```json
{
  "id": 1,
  "habitId": 1,
  "date": "2024-10-25",
  "completed": true,
  "notes": "Updated workout notes",
  "timestamp": "2024-10-25T08:30:00Z",
  "lastModified": "2024-10-25T09:00:00Z"
}
```

### Delete Execution

Remove a habit execution record.

**Endpoint:** `DELETE /habit-executions/{executionId}`

**Success Response:** (204 No Content)

## General Notes

### Time Zones

- All dates are handled in UTC
- Client should convert to/from local timezone
- Timestamps include timezone information

### Data Validation

- Future dates are not allowed
- Dates before habit creation are not allowed
- Bulk operations limited to 50 records
- Notes field has maximum length

### Caching

- Statistics are cached for 1 hour
- Progress reports are cached for 6 hours
- Cache is invalidated when new executions are recorded

### Performance

- Bulk operations should be used for multiple records
- Pagination is required for large result sets
- Heavy statistical calculations are done asynchronously

### Security

- Users can only access their own execution data
- Modification window is limited to 24 hours
- All changes are logged for audit purposes