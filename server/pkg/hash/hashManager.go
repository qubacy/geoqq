package hash

type HashManager interface {
	New(value string) (string, error)
}
