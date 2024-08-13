package serverSide

// repeat with those in rabbit mq
//    but it doesn't matter!

const (
	EventUpdatedPublicUser = "updated_public_user"

	EventAddedMateChat   = "added_mate_chat"
	EventUpdatedMateChat = "updated_mate_chat"

	EventAddedMateRequest = "added_mate_request"
	EventAddedMateMessage = "added_mate_message"

	EventAddedGeoMessage = "added_geo_message"
)

const (
	EventServerError = "server_error" // critical!?

	EventParseError = "parse_error"

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

func MakeEventsOkAndFl(name string) (string, string) {
	return MakeEventSucceeded(name),
		MakeEventFailed(name)
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

func NewMessage(eventName string, payload any) *Message {
	m := MakeMessage(eventName, payload)
	return &m
}

func MakeEmptyPayload() any {
	return struct{}{}
}
