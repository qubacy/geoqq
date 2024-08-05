# Messaging

From `geoqq http` to `geoqq ws`.

```go
const (
	EventUpdatedPublicUser = "updated_public_user"

	EventAddedMateChat   = "added_mate_chat"
	EventUpdatedMateChat = "updated_mate_chat"

	EventAddedMateRequest = "added_mate_request"
	EventAddedMateMessage = "added_mate_message"

	EventAddedGeoMessage = "added_geo_message"
)
```

<!-- -------------------------------------------- -->

## `PublicUser`

### Updated

```json
{
    "event": "updated_public_user",
    "payload": {
        "id": "<id>"
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
        "target-user-id": "<id>",
        "id": "<id>"
    }
}
```

## `MateRequest`

### Added

```json
{
    "event": "added_mate_request",
    "payload": {
        "target-user-id": "<id>", 
        "id": "<id>",             // request id
        "user-id": "<id>"         // requester user id
    }
}
```

## `MateMessage`

### Added

```json
{
    "event": "added_mate_message",
    "payload": {
        "target-user-id": "<id>",
        "id": "<id>",
        "chat-id": "<id>",
        "text": "<string>",
        "time": "<int>",
        "user-id": "<id>",
        "read": "<bool>",
    }
}
```

## `GeoMessage`

### Added

```json
{
    "event": "added_geo_message",
    "payload": {
        "id": "<id>",
        "text": "<string>",
        "time": "<int>",
        "user-id": "<id>",
        "latitude": "<real>",
        "longitude": "<real>"
    }
}
```