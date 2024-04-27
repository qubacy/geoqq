package impl

// affects http status!

type GuiltySide int // ?

const (
	Unknown int = iota
	Server
	Client

	// other sides...
)

func GuiltySideToString(guiltySide int) string {
	switch guiltySide {
	case Server:
		return "server"
	case Client:
		return "client"
	}

	return "unknown"
}
