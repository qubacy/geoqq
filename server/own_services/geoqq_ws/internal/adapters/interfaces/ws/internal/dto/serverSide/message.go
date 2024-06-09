package serverSide

const (
	EventUpdatedPublicUser = "updated_public_user"

	EventAddedMateChat   = "added_mate_chat"
	EventUpdatedMateChat = "updated_mate_chat"

	EventAddedMateRequest = "added_mate_request"
	EventAddedMateMessage = "added_mate_message"

	EventAddedGeoMessage = "added_geo_message"
)

const (
	EventGeneralError = "general_error"

	PostfixSucceeded = "succeeded"
	PostfixFailed    = "failed"
)

func MakeEventWithPostfix(eventName, postfixName string) string {
	return eventName + "_" + postfixName
}

type Message struct {
	Event   string `json:"event"`
	Payload any    `json:"payload"`
}

func MakeMessage(eventName string, payload any) Message {
	return Message{
		Event:   eventName,
		Payload: payload,
	}
}
