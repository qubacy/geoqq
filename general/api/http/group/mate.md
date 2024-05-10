# Mate

### General Headers
| Name | Value Type | 
| ---- | -----------|
| Authorization | Bearer `<jwt-string>` |

## GET /api/mate/chat

### Parameters
#### Required
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

### Responses
- *200*

<!-- -------------------------------------------- -->

## GET /api/mate/chat/{`id`}/message

### Parameters
#### Required
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
| accepted | `bool` | 

### Responses
- *200*

<!-- -------------------------------------------- -->

# For debug

## POST /api/mate/chat/{id}/message

### Request body
```json
{
    "text": "<string>"
}
```

### Responses
- *200*