# Auth 🚧

## POST /api/sign-in

### Request body
```json
{
    "login": "<string>",
    "password-hash": "<string>",
}
```

### Responses
- *200*
```json
{
    "access-token": "<jwt-string>",
    "update-token": "<jwt-string>",
}
```
- Other error codes.

<!-- -------------------------------------------- -->

## POST /api/sign-up

### Request body
```json
{
    "login": "<string>",
    "password-hash": "<string>",
}
```

### Responses
- *200*
```json
{
    "access-token": "<jwt-string>",
    "update-token": "<jwt-string>",
}
```
- Other error codes.

# Useful links:
- https://habr.com/ru/articles/210760/