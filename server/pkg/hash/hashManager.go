package hash

// Output string has the pattern [a-fA-F0-9]?

type HashManager interface {
	NewFromString(value string) (string, error)
	NewFromBytes(bytes []byte) (string, error)
}
