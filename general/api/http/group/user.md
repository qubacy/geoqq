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
    "avatar-id": "<id>",
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
    "avatar-id": "<id>",
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
    "deleted": "<bool>",
    "username": "<string>",
    "description": "<string>",
    "avatar-id": "<id>",
    "is-mate": "<bool>"
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
            "deleted": "<bool>",
            "username": "<string>",
            "description": "<string>",
            "avatar-id": "<id>",
            "is-mate": "<bool>"
        },
        ...
    ]
}
```