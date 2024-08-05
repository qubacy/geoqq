# API WebSocket 🔌

- API Http [here](../http/desc.md)

## Connect

## GET /api/ws

### Headers
| Name | Value Type | 
| ---- | -----------|
| Authorization | Bearer `<jwt-string>` |

## Group

- Server Side [here](./group/server.md)
- Client Side [here](./group/client.md)

## Error

Errors are presented as eventual reactions to client-side packages expressed in the event's `payload` property as an object. The structure is as follows:

```json
{
    "code": "<int>",
    "error": {
        "id": "<int>",
        "text": "<string>",
        "trace": "<string>"
    }
}
```

**Note**: property `code` provides the client with information about who is in charge of the fail. For instance, 4XX codes say that the client is the one, and 5XX is the server.