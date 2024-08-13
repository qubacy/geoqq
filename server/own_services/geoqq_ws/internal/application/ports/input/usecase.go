package input

type Event = uint64 // will need to be converted!

const (
	EventAdded Event = iota
	EventUpdated
)

type UserIdWithEvent interface {
	GetUserId() uint64
	GetEvent() Event
}

// -----------------------------------------------------------------------

type UserIdWithEventImpl struct {
	UserId uint64
	Event  Event
}

func (u UserIdWithEventImpl) GetUserId() uint64 {
	return u.UserId
}
func (u UserIdWithEventImpl) GetEvent() Event {
	return u.Event
}

func (u UserIdWithEventImpl) WithUserId(userId uint64) UserIdWithEventImpl {
	u.UserId = userId
	return u
}

func MakeUserIdWithEvent(userId uint64, event Event) UserIdWithEventImpl {
	return UserIdWithEventImpl{
		UserId: userId,
		Event:  event,
	}
}

// -----------------------------------------------------------------------
