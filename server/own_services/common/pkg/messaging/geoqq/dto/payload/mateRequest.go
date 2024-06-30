package payload

type MateRequest struct {
	TargetUserId float64 `json:"target-user-id"`
	Id           float64 `json:"id"`
	UserId       float64 `json:"user-id"` // from!
}
