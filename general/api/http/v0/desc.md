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

# Useful links:
- Как надо хэшировать пароли и как не надо // d0znpp / URL: https://habr.com/ru/articles/210760/
- Can HTTP PUT request have application/x-www-form-urlencoded as the Content-Type? // stack overflow / URL: https://stackoverflow.com/questions/6306185/can-http-put-request-have-application-x-www-form-urlencoded-as-the-content-type
- Authorization // mdn web docs / URL: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Authorization