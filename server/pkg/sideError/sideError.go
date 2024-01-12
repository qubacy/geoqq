package sideError

type SideError interface {
	Side() uint
	Unwrap() error
	error
}
