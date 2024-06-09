package dto

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

	PostfixSucceeded = "_succeeded"
	PostfixFailed    = "_failed"
)

func MakeEventWithSucceeded(eventName string) string {
	return eventName + PostfixSucceeded
}

func MakeEventWithFailed(eventName string) string {
	return eventName + PostfixFailed
}
