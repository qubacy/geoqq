# Client Side 📱

The client application sends to the server.

## `UserLocation` (after an interval of time)

```json
{    
    "route": "/user/location",
    "access-token": "<jwt-string>",

    "payload": {
        "longitude": "<real>",
        "latitude": "<real>",
        "radius": "<int>"
    }
}
```

## `GeoMessage`

```json
{
    "route": "/geo/chat/message",
    "access-token": "<jwt-string>",

    "payload": {
        "text": "<string>",
        "longitude": "<real>",
        "latitude": "<real>"
    }
}
```

## `MateMessage`

```json
{
    "route": "/mate/chat/message",
    "access-token": "<jwt-string>",

    "payload": {
        "id": "<id>",
        "text": "<string>"
    }
}
```
