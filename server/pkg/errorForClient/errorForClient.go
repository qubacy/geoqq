package errorForClient

type ErrorForClient interface {
	Unwrap() error
	UnwrapToLast() error

	// details...
	GuiltySide() int
	ClientCode() int

	error
}
