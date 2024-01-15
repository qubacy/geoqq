package impl

// interpretation on client application!

const (
	ServerError = 1000 + iota
	HashManagerError
	TokenManagerError
	StorageError
)

const (
	InvalidInputParams = 1100 + iota
)

const (
	AuthError = 1500 + iota
	UserAlreadyExist
	UserNotFound
)

const (
	UserError = 1600 + iota
)

const (
	ImageError = 1700 + iota
)

const (
	MateError = 1800 + iota
)

const (
	GeoError = 1900 + iota
)
