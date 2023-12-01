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
    "content": "<base64-string>"
}
```

## GET /api/image

### Parameters
#### Required
- accessToken=`"<jwt-string>"`

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
    "images": [
        {
            "id": "<id>",
            "content": "<base64-string>"
        },
        ...
    ]
}
```