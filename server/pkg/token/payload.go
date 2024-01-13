package token

type Payload struct {
	UserId uint64 `json:"user-id"`
}

func MakePayload(userId uint64) Payload {
	return Payload{
		UserId: userId,
	}
}
