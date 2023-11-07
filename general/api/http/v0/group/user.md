# User 👨‍💻

## GET /api/my-profile

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
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
    "avatar": "<base64-string>",
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

## GET /api/user/{`userId`}

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
    "username": "<string>",
    "description": "<string>",
    "avatar-id": "<id>",
    "is-mate": "<bool>"
}
```