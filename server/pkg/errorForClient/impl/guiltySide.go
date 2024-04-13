package impl

// affects http status!

type GuiltySide int // ?

const (
	Unknown int = iota
	Server
	Client

	// other sides...
)
