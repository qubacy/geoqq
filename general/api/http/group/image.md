# Image ⬛️

## GET /api/image/{`id`}

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

### Responses
- *200*
```json
{
    "id": "<id>",
    "ext": "<int>",
    "content": "<base64-string>"
}
```

## GET /api/image

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
    "images": [
        {
            "id": "<id>",
            "ext": "<int>",
            "content": "<base64-string>"
        },
        ...
    ]
}
```

<!-- -------------------------------------------- -->

## POST /api/image

### Request body
```json
{
    "access-token": "<jwt-string>",
    "avatar": {
        "ext": "<int>",
        "content": "<base64-string>"
    }
}
```

### Responses
- *200*
```json
{
    "id": "<id>"
}
```