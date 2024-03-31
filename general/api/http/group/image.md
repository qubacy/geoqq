# Image (eq Avatar?) ⬛️

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

## POST /api/image

> [!TIP]
> POST request to get some images.

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

## POST /api/image/new

### Request body
```json
{
    "access-token": "<jwt-string>",
    "image": {
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