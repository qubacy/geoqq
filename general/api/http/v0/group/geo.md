# Geo 🗺

## GET /api/geo/chat/message

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
            "user": {
                "id": "<id>"
            },
            "text": "<string>",
            "time": "<int>"
        },
        ...
    ],
    "users": [
        {
            "id": "<id>",
            "username": "<string>"
        },
        ...
    ]
}
```
- Other error codes.