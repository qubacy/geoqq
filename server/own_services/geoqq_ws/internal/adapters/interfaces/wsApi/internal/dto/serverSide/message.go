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
	EventServerError = "server_error"

	EventParseError   = "parse_error"
	EventGeneralError = "general_error" // by domains...
)

const (
	PostfixSucceeded = "succeeded"
	PostfixFailed    = "failed"
)

func MakeEventWithPostfix(name, postfixName string) string {
	return name + "_" + postfixName
}

func MakeEventFailed(name string) string {
	return name + "_" + PostfixFailed
}

func MakeEventSucceeded(name string) string {
	return name + "_" + PostfixSucceeded
}

// -----------------------------------------------------------------------

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
