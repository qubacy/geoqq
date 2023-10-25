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
            "user": {
                "id": "<id>",
                "username": "<string>",
                "is-mate": "<bool>",
                "avatar": "<base58-string>"
            },
            "new-message-count": "<int>",
            "last-message": {
                "text": "<string>",
                "time": "<int>"
            }
        },
        ...
    ]
}
```
- Other error codes.

## DELETE /api/mate/chat/{`id`}

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
- Other error codes.

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
- Other error codes.

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
- Other error codes.

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
                "avatar": "<base58-string>",
            }
        },
        ...
    ]
}
```
- Other error codes.

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
- Other error codes.

## PUT /api/mate/request/{`id`}

### Request body
```json
{
    "access-token": "<jwt-string>",
    "accepted": "<bool>"
}
```

### Responses
- *200*
- Other error codes.