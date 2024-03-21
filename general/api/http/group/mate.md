# Mate

## GET /api/mate/chat

### Parameters
#### Required
- accessToken=`"<jwt-string>"`
- offset=`"<int>"`
- count=`"<int>"`

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

## GET /api/mate/chat/{`id`}

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
    "user-id": "<id>",

    "new-message-count": "<int>",
    "last-message": {
        "id": "<id>",
        "text": "<string>",
        "time": "<int>",
        "user-id": "<id>"
    }
}
```

<!-- -------------------------------------------- -->

## DELETE /api/mate/chat/{`id`}

### Request body
```json
{
    "access-token": "<jwt-string>",
}
```

### Responses
- *200*

<!-- -------------------------------------------- -->

## GET /api/mate/chat/{`id`}/message

### Parameters
#### Required
- accessToken=`"<jwt-string>"`
- offset=`"<int>"`
- count=`"<int>"`

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

## GET /api/mate/chat/{`id`}/new-message/count

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
    "count": "<int>"
}
```

<!-- -------------------------------------------- -->

## GET /api/mate/request

### Parameters
#### Required
- accessToken=`"<jwt-string>"`
- offset=`"<int>"`
- count=`"<int>"`

### Responses
- *200*
```json
{
    "requests": [
        {
            "id": "<id>",
            "user-id": "<id>"
        },
        ...
    ]
}
```

## GET /api/mate/request/count

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
    "count": "<int>"
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

<!-- -------------------------------------------- -->

# For debug

## POST /api/mate/chat/{id}/message

### Request body
```json
{
    "access-token": "<jwt-string>",
    "text": "<string>"
}
```

### Responses
- *200*