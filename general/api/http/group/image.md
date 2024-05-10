# Image (eq Avatar?) ⬛️

### General Headers
| Name | Value Type | 
| ---- | -----------|
| Authorization | Bearer `<jwt-string>` |

## GET /api/image/{`id`}

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