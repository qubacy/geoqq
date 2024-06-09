package clientSide

const (
	ActionUpdateUserLocation = "update_user_location"
	ActionAddGeoMessage      = "add_geo_message"
	ActionAddMateMessage     = "add_mate_message"
)

type Message struct {
	Action      string `json:"action"`
	AccessToken string `json:"access-token"`
	Payload     any    `json:"payload"`
}
