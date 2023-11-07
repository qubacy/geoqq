# Mate

## GET /api/mate/chat

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
    "chats": [
        {
            "id": "<id>",
            "user-id": "<id>",
            
            "new-message-count": "<int>",
            "last-message": {
                "id": "<id>",
                "text": "<string>",
                "time": "<int>",
                "user-id": "<id>"
            }
        },
        ...
    ]
}
```

## DELETE /api/mate/chat/{`id`}

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*

<!-- -------------------------------------------- -->

## GET /api/mate/chat/{`id`}/message

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
    "messages": [
        {
            "id": "<id>",
            "text": "<string>",
            "time": "<int>",
            "user-id": "<id>"
        },
        ...
    ]
}
```

## POST /api/mate/chat/{`id`}/message

### Request body
```json
{
    "access-token": "<jwt-string>",
    "message": {
        "text": "<string>"
    }
}
```

### Responses
- *200*

<!-- -------------------------------------------- -->

## GET /api/mate/request

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
    "requests": [
        {
            "id": "<id>",
            "user": {
                "id": "<id>",
                "username": "<string>",
                "description": "<string>",
                "avatar-id": "<id>"
            }
        },
        ...
    ]
}
```

## POST /api/mate/request

### Request body
```json
{
    "access-token": "<jwt-string>",
    "user-id": "<id>",
}
```

### Responses
- *200*

## PUT /api/mate/request/{`id`}

### Request body
#### Type: *application/x-www-form-urlencoded*
| Key | Value Type |
|-----|------------|
| access-token | `jwt-string` | 
| accepted | `bool` | 

### Responses
- *200*