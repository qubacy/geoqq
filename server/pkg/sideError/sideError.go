package sideError

type SideError interface {
	Unwrap() error
	UnwrapToLast() error

	Side() uint
	error
}
