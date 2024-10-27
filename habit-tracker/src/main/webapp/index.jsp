<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>API Documentation</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            line-height: 1.6;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            color: #333;
        }

        h1 {
            color: #2c3e50;
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
            margin-top: 40px;
        }

        .endpoint {
            background-color: #f8f9fa;
            border-left: 4px solid #007bff;
            padding: 12px 20px;
            margin: 10px 0;
            border-radius: 0 4px 4px 0;
        }

        .method {
            font-weight: bold;
            color: #28a745;
            display: inline-block;
            width: 70px;
        }

        .path {
            color: #007bff;
            font-family: monospace;
            font-size: 1.1em;
        }

        .admin-only {
            background-color: #fff3cd;
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 0.8em;
            color: #856404;
            margin-left: 10px;
        }

        .description {
            margin-left: 75px;
            color: #666;
        }

        .section {
            margin-bottom: 40px;
        }
    </style>
</head>
<body>
<div class="section">
    <h1>App Status API Endpoints</h1>

    <div class="endpoint">
        <span class="method">GET</span>
        <span class="path">/api/status</span>
        <div class="description">Get application status and current user information</div>
    </div>
</div>

<div class="section">
    <h1>User Management API Endpoints</h1>

    <div class="endpoint">
        <span class="method">GET</span>
        <span class="path">/api/users</span>
        <span class="admin-only">admin only</span>
        <div class="description">List all users</div>
    </div>

    <div class="endpoint">
        <span class="method">GET</span>
        <span class="path">/api/users/current</span>
        <div class="description">Get current user info</div>
    </div>

    <div class="endpoint">
        <span class="method">POST</span>
        <span class="path">/api/users</span>
        <div class="description">Register new user</div>
    </div>

    <div class="endpoint">
        <span class="method">POST</span>
        <span class="path">/api/users/login</span>
        <div class="description">User login</div>
    </div>

    <div class="endpoint">
        <span class="method">POST</span>
        <span class="path">/api/users/logout</span>
        <div class="description">User logout</div>
    </div>

    <div class="endpoint">
        <span class="method">PUT</span>
        <span class="path">/api/users/email</span>
        <div class="description">Update email</div>
    </div>

    <div class="endpoint">
        <span class="method">PUT</span>
        <span class="path">/api/users/name</span>
        <div class="description">Update name</div>
    </div>

    <div class="endpoint">
        <span class="method">PUT</span>
        <span class="path">/api/users/password</span>
        <div class="description">Update password</div>
    </div>

    <div class="endpoint">
        <span class="method">PUT</span>
        <span class="path">/api/users/block</span>
        <span class="admin-only">admin only</span>
        <div class="description">Block user</div>
    </div>

    <div class="endpoint">
        <span class="method">PUT</span>
        <span class="path">/api/users/unblock</span>
        <span class="admin-only">admin only</span>
        <div class="description">Unblock user</div>
    </div>

    <div class="endpoint">
        <span class="method">DELETE</span>
        <span class="path">/api/users/{email}</span>
        <div class="description">Delete user account</div>
    </div>
</div>

<div class="section">
    <h1>Habit Management API Endpoints</h1>

    <div class="endpoint">
        <span class="method">GET</span>
        <span class="path">/api/habits</span>
        <div class="description">List habits with optional filters (date and active status)</div>
    </div>

    <div class="endpoint">
        <span class="method">POST</span>
        <span class="path">/api/habits</span>
        <div class="description">Create new habit</div>
    </div>

    <div class="endpoint">
        <span class="method">PUT</span>
        <span class="path">/api/habits/{habitId}</span>
        <div class="description">Update habit</div>
    </div>

    <div class="endpoint">
        <span class="method">DELETE</span>
        <span class="path">/api/habits/{habitId}</span>
        <div class="description">Delete habit</div>
    </div>
</div>
</body>
</html>
Improve
Explai