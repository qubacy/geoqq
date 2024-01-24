package hash

// Output string has the pattern [a-fA-F0-9]?

type HashManager interface {
	New(value string) (string, error)
}
