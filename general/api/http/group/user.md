# User 👨‍💻

## GET /api/my-profile

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

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
    /* required */
    "access-token": "<jwt-string>",
       
    /* optional */
    "description": "<string>",
    "avatar-id": "<image-id>",
    "privacy": {
        "hit-me-up": "<int>"
    },
    "security": {
        "password": "<hash-string>",
        "new-password": "<hash-string>"
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
    /* required */
    "access-token": "<jwt-string>",
       
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
        "password": "<hash-string>",
        "new-password": "<hash-string>"
    }
}
```

### Responses
- *200*

<!-- -------------------------------------------- -->

## DELETE /api/my-profile

### Request body
```json
{
    "access-token": "<jwt-string>",
}
```

### Responses
- *200*
  
<!-- -------------------------------------------- -->

## GET /api/user/{`id`}

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

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
    "access-token": "<jwt-string>",
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