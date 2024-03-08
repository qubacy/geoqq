# Geo 🗺

## GET /api/geo/chat/message/all

### Parameters
#### Required
- accessToken=`"<jwt-string>"`
- radius=`"<int>"`
- lon=`"<real>"`
- lat=`"<real>"`

### Responses
- *200*
```json
{
    "messages":
    [
        {
            "id": "<id>",
            "user-id": "<id>",
            "text": "<string>",
            "time": "<int>"
        },
        ...
    ]
}
```

## GET /api/geo/chat/message

### Parameters
#### Required
- accessToken=`"<jwt-string>"`
- radius=`"<int>"`
- lon=`"<real>"`
- lat=`"<real>"`
- offset=`"<int>"`
- count=`"<int>"`

### Responses
- *200*
```json
{
    "messages":
    [
        {
            "id": "<id>",
            "user-id": "<id>",
            "text": "<string>",
            "time": "<int>"
        },
        ...
    ]
}
```

<!-- -------------------------------------------- -->

# For debug

## POST /api/geo/chat/message

### Request body
```json
{
    "access-token": "<jwt-string>",
    "text": "<string>",
    "longitude": "<real>",
    "latitude": "<real>"
}
```

### Responses
- *200*