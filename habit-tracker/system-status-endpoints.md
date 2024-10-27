# System Status Endpoints

## System Health and Status

### Get System Status

Get current application status and user session information.

**Endpoint:** `GET /system/status`

**Required Permissions:** None

**Success Response:** (200 OK)

```json
{
  "status": "running",
  "timestamp": "2024-10-25T10:30:00Z",
  "startupTime": "2024-10-25T10:00:00Z",
  "uptime": {
    "days": 0,
    "hours": 0,
    "minutes": 30,
    "seconds": 0
  },
  "authenticated": true,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "isAdmin": false
  },
  "version": {
    "application": "1.0.0",
    "api": "v1"
  }
}
```

**Error Response:** (503 Service Unavailable)

```json
{
  "status": "maintenance",
  "message": "System is under maintenance",
  "estimatedResolution": "2024-10-25T11:30:00Z"
}
```

### Health Check

Check the health status of all system components.

**Endpoint:** `GET /system/health`

**Required Permissions:** None (Detailed view requires Admin)

**Success Response:** (200 OK)

```json
{
  "status": "healthy",
  "timestamp": "2024-10-25T10:30:00Z",
  "components": {
    "database": {
      "status": "up",
      "responseTime": 100,
      "connections": {
        "active": 5,
        "idle": 10,
        "max": 20
      }
    },
    "session": {
      "status": "up",
      "activeSessions": 150
    },
    "memory": {
      "status": "healthy",
      "total": 1073741824,
      "free": 715827200,
      "max": 1610612736,
      "usedPercentage": 65.5
    },
    "cache": {
      "status": "up",
      "hitRatio": 0.95
    }
  },
  "metrics": {
    "requestsPerMinute": 250,
    "averageResponseTime": 150,
    "errorRate": 0.02
  }
}
```

**Error Response:** (503 Service Unavailable)

```json
{
  "status": "unhealthy",
  "timestamp": "2024-10-25T10:30:00Z",
  "components": {
    "database": {
      "status": "down",
      "error": "Connection timeout",
      "lastSuccessful": "2024-10-25T10:25:00Z"
    }
  }
}
```

### System Information

Get detailed system and application information.

**Endpoint:** `GET /system/info`

**Required Permissions:** Basic info (None), Detailed info (Admin)

**Success Response:** (200 OK)

```json
{
  "application": {
    "name": "Habit Tracker",
    "version": "1.0.0",
    "environment": "production",
    "startTime": "2024-10-25T10:00:00Z",
    "timezone": "UTC"
  },
  "system": {
    "javaVersion": "17.0.2",
    "os": {
      "name": "Linux",
      "version": "4.19.0",
      "arch": "amd64"
    },
    "processors": 8,
    "memory": {
      "total": "8GB",
      "available": "5.5GB"
    }
  },
  "database": {
    "version": "PostgreSQL 13.4",
    "size": "1.2GB",
    "tables": 15,
    "migrations": "up-to-date"
  },
  "metrics": {
    "activeUsers": 150,
    "totalHabits": 1500,
    "totalExecutions": 25000,
    "dailyActiveUsers": 75
  }
}
```

### Get System Metrics

Retrieve detailed system performance metrics.

**Endpoint:** `GET /system/metrics`

**Required Permissions:** Admin

**Query Parameters:**

- period: Time period for metrics (1h, 24h, 7d, 30d)
- type: Metric type (performance, usage, errors, all)

**Success Response:** (200 OK)

```json
{
  "timestamp": "2024-10-25T10:30:00Z",
  "period": "24h",
  "performance": {
    "responseTime": {
      "average": 150,
      "p95": 250,
      "p99": 400
    },
    "throughput": {
      "requestsPerSecond": 25,
      "peakRequestsPerSecond": 50
    },
    "memory": {
      "used": "5.5GB",
      "max": "8GB",
      "gcCollections": 150
    }
  },
  "usage": {
    "activeUsers": {
      "current": 150,
      "peak": 300,
      "average": 175
    },
    "apiCalls": {
      "total": 1500000,
      "byEndpoint": {
        "/api/habits": 500000,
        "/api/executions": 750000
      }
    },
    "storage": {
      "used": "1.2GB",
      "available": "10GB"
    }
  },
  "errors": {
    "total": 150,
    "byType": {
      "4xx": 120,
      "5xx": 30
    },
    "topErrors": [
      {
        "code": 404,
        "count": 75,
        "message": "Resource not found"
      }
    ]
  }
}
```

### Get System Configuration

Retrieve current system configuration (Admin only).

**Endpoint:** `GET /system/config`

**Required Permissions:** Admin

**Success Response:** (200 OK)

```json
{
  "application": {
    "maxUsersPerTenant": 1000,
    "maxHabitsPerUser": 20,
    "retentionPeriod": "365d"
  },
  "security": {
    "sessionTimeout": "30m",
    "passwordPolicy": {
      "minLength": 8,
      "requireSpecialChar": true,
      "requireNumbers": true
    },
    "rateLimiting": {
      "enabled": true,
      "maxRequests": 100,
      "window": "1m"
    }
  },
  "features": {
    "bulkOperations": true,
    "analytics": true,
    "reporting": true
  }
}
```

## Maintenance Endpoints

### Get Maintenance Status

Check current maintenance status and scheduled maintenance windows.

**Endpoint:** `GET /system/maintenance`

**Success Response:** (200 OK)

```json
{
  "currentStatus": "operational",
  "scheduledMaintenance": [
    {
      "startTime": "2024-11-01T02:00:00Z",
      "endTime": "2024-11-01T04:00:00Z",
      "description": "Database optimization",
      "impact": "System will be read-only"
    }
  ],
  "lastMaintenance": {
    "completedAt": "2024-10-20T03:30:00Z",
    "duration": "2h",
    "type": "system upgrade"
  }
}
```

## General Notes

### Access Control

- Basic status endpoints are public
- Detailed metrics require admin access
- Health check details vary by user role

### Rate Limiting

- Public endpoints: 60 requests per minute
- Admin endpoints: 300 requests per minute
- Status endpoints bypass rate limiting during incidents

### Data Retention

- Metrics are retained for 30 days
- Detailed logs for 7 days
- Aggregated statistics for 1 year

### Monitoring

- All endpoints are monitored for uptime
- Response times are tracked
- Error rates trigger alerts
- Resource usage is continuously monitored