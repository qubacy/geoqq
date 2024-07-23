package payload

type MateRequest struct {
	Id     float64 `json:"id"`
	UserId float64 `json:"user-id"`
}

func MakeMateRequest(id, userId float64) MateRequest {
	return MateRequest{
		Id:     id,
		UserId: userId,
	}
}
