# Client Side 📱

The client application sends to the server.

## `UserLocation` (after an interval of time)

```json
{    
    "route": "update_user_location",
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
    "route": "add_geo_message",
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
    "route": "add_mate_message",
    "access-token": "<jwt-string>",

    "payload": {
        "chat-id": "<id>",
        "text": "<string>"
    }
}
```
