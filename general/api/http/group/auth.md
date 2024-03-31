# Auth 🚧

## POST /api/sign-in

### Request body
#### Type: *application/x-www-form-urlencoded*
| Key | Value Type |
|-----|------------|
| login    | `string` | 
| password | `hash-in-hex-string` | 

### Responses
- *200*
```json
{
    "access-token": "<jwt-string>",
    "refresh-token": "<jwt-string>"
}
```
- *400*, *500*, etc.
```json
{
    "error": {
        "id": "<int>"
    }
}
```

<!-- -------------------------------------------- -->

## POST /api/sign-up

### Request body
#### Type: *application/x-www-form-urlencoded*
| Key | Value Type |
|-----|------------|
| login    | `string` | 
| password | `hash-in-hex-string` | 

### Responses
- *200*
```json
{
    "access-token": "<jwt-string>",
    "refresh-token": "<jwt-string>"
}
```
- *400*, *500*, etc.
```json
{
    "error": {
        "id": "<int>"
    }
}
```

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
- *400*, *500*, etc.
```json
{
    "error": {
        "id": "<int>"
    }
}
```