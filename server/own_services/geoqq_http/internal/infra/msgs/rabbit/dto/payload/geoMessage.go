package payload

type GeoMessage struct {
	Id        float64 `json:"id"`
	Text      string  `json:"text"`
	Time      float64 `json:"time"`
	UserId    float64 `json:"user-id"`
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
}
