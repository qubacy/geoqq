# User 👨‍💻

### General Headers
| Name | Value Type | 
| ---- | -----------|
| Authorization | Bearer `<jwt-string>` |

## GET /api/my-profile

### Responses
- *200*
```json
{
    "id": "<id>",
    "username": "<string>",
    "description": "<string>",
    "avatar-id": "<image-id>",
    "privacy": {
        "hit-me-up": "<int>"
    }
}
```

## PUT /api/my-profile

### Request body
```json
{       
    /* optional */
    "description": "<string>",
    "avatar-id": "<image-id>",
    "privacy": {
        "hit-me-up": "<int>"
    },
    "security": {
        "password": "<hash-in-hex-string>",
        "new-password": "<hash-in-hex-string>"
    }
}
```

### Responses
- *200*

<!-- -------------------------------------------- -->

## PUT /api/my-profile/with-attached-avatar

### Request body
```json
{       
    /* optional */
    "description": "<string>",
    "avatar": {
        "ext": "<int>",
        "content": "<base64-string>"
    },
    "privacy": {
        "hit-me-up": "<int>"
    },
    "security": {
        "password": "<hash-in-hex-string>",
        "new-password": "<hash-in-hex-string>"
    }
}
```

### Responses
- *200*

<!-- -------------------------------------------- -->

## DELETE /api/my-profile

### Responses
- *200*
  
<!-- -------------------------------------------- -->

## GET /api/user/{`id`}

### Responses
- *200*
```json
{
    "id": "<id>",
    "username": "<string>",
    "description": "<string>",
    "avatar-id": "<id>",
    "last-action-time": "<int>",
    "is-mate": "<bool>",
    "is-deleted": "<bool>",
    "hit-me-up": "<int>"
}
```

## GET /api/user

### Request body
```json
{
    "ids": [
        "<id>",
        ...
    ]
}
```

### Responses
- *200*
```json
{
    "users": [
        {
            "id": "<id>",
            "username": "<string>",
            "description": "<string>",
            "avatar-id": "<id>",
            "last-action-time": "<int>",
            "is-mate": "<bool>",
            "is-deleted": "<bool>",
            "hit-me-up": "<int>"
        },
        ...
    ]
}
```