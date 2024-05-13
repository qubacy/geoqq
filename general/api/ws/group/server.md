# Server Side 🗄️

The server sends to the client application.

## `PublicUser` 

### Updated

```json
{
    "event": "updated_public_chat",
    "payload": {
        "id": "<id>",
        "username": "<string>",
        "description": "<string>",
        "avatar-id": "<id>",
        "last-action-time": "<int>",
        "is-mate": "<bool>",
        "is-deleted": "<bool>",
        "hit-me-up": "<int>"
    }
}
```

<!-- -------------------------------------------- -->

## `MateChat`

### Added, Updated

```json
{
    "event": "added_mate_chat | updated_mate_chat",
    "payload": {
        "id": "<id>",
        "user-id": "<id>",
        "new-message-count": "<int>",
        "last-message": {
            "id": "<id>",
            "text": "<string>",
            "time": "<int>",
            "user-id": "<id>"
        }
    }
}
```

<!-- -------------------------------------------- -->

## `MateRequest` 

### Added

```json
{
    "event": "added_mate_request",
    "payload": {
        "id": "<id>",
        "user-id": "<id>"
    }
}
```

<!-- -------------------------------------------- -->

## `MateMessage`

### Added

```json
{
    "event": "added_mate_message",
    "payload": {
        "id": "<id>",
        "chat-id": "<id>",
        "text": "<string>",
        "time": "<int>",
        "user-id": "<id>"
    }
}
```

<!-- -------------------------------------------- -->

## `GeoMessage` 

### Added

```json
{
    "event": "added_geo_message",
    "payload": {
        "id": "<id>",
        "user-id": "<id>",
        "text": "<string>",
        "time": "<int>"
    }
}
```

<!-- -------------------------------------------- -->

# Tips For Client App ✨

> What events need to be processed?

## MateChatsScreen 

- `PublicUser` updated
- `MateChat` updated
- `MateChat` added

## MateRequestScreen

- `MateRequests` added
- `PublicUser` updated

## MateChatScreen (With Specific User)

- `PublicUser` updated
- `MateMessage` added

## GeoChatScreen

- `PublicUser` updated
- `GeoMessage` added
