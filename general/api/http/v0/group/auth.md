# Auth 🚧

## POST /api/sign-in

### Request body
#### Type: *application/x-www-form-urlencoded*
| Key | Value Type |
|-----|------------|
| login    | `string`      | 
| password | `hash-string` | 

### Responses
- *200*
```json
{
    "access-token": "<jwt-string>",
    "refresh-token": "<jwt-string>"
}
```
- Other error codes.

<!-- -------------------------------------------- -->

## POST /api/sign-up

### Request body
#### Type: *application/x-www-form-urlencoded*
| Key | Value Type |
|-----|------------|
| login    | `string`      | 
| password | `hash-string` | 

### Responses
- *200*
```json
{
    "access-token": "<jwt-string>",
    "refresh-token": "<jwt-string>"
}
```
- Other error codes.

<!-- -------------------------------------------- -->

## PUT /api/sign-in

### Request body
#### Type: *application/x-www-form-urlencoded*
| Key | Value Type |
|-----|------------|
| refresh-token | `string` | 

### Responses
- *200*
```json
{
    "access-token": "<jwt-string>",
    "refresh-token": "<jwt-string>"
}
```
- Other error codes.

# Useful links:
- https://habr.com/ru/articles/210760/
- https://stackoverflow.com/questions/6306185/can-http-put-request-have-application-x-www-form-urlencoded-as-the-content-type