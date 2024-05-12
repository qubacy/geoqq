# API 🛣

## Group

- Auth [here](./group/auth.md)
- User [here](./group/user.md)
- Image [here](./group/image.md)
- Mate [here](./group/mate.md)
- Geo [here](./group/geo.md)

## Some details

- `"<id>"` is a special unsigned type for keys.
- Time field is in UTC (Unix Time Stamp) format.
- `"<base64-string>"` is the standard base64 encoding, as defined in RFC 4648. 

### Error response

- *400*, *500*, etc.
```json
{
    "error": {
        "id": "<int>",
        "text": "<string>",

        "trace": "<string>"
    }
}
```
