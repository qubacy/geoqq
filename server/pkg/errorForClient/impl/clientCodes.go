package impl

// interpretation on client application!

const (
	NoError = 0
)

const (
	ServerError = 1000 + iota
	HashManagerError
	TokenManagerError
	StorageError
)

const (
	ParseRequestParamsFailed = 1100 + iota
	ValidateInputParamsFailed

	ParseAccessTokenFailed
	ValidateAccessTokenFailed
)

// -----------------------------------------------------------------------

const (
	AuthError = 1500 + iota
	UserAlreadyExist
	UserNotFound

	InvalidRefreshToken
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
