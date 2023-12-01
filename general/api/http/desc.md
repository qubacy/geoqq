# API 🛣

## Group
- Auth [here](./group/auth.md)
- User [here](./group/user.md)
- Image [here](./group/image.md)
- Mate [here](./group/mate.md)
- Geo [here](./group/geo.md)

## Some details

- `"<id>"` is a special unsigned type for keys.
- Time field is in UTC format.

### Error response

- *400*, *500*, etc.
```json
{
    "error": {
        "id": "<int>"
    }
}
```
