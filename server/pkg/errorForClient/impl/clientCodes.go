package impl

// interpretation on client application!

const (
	NoError = 0
)

const (
	ServerError = 1000 + iota
	HashManagerError
	TokenManagerError
	AvatarGeneratorError
	DomainStorageError
	FileStorageError
)

const (
	// handler
	ParseAnyFormFailed       = 1100 + iota // form
	ParseRequestParamsFailed               // uri, x-www-form-urlencoded
	ParseRequestJsonBodyFailed
	ValidateRequestFailed // any content

	ParseAccessTokenFailed
	ValidateAccessTokenFailed

	// service
	ValidateInputParamsFailed
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
	IncorrectPasswordWhenUpdate
	UnknownAvatarExtension
	AvatarBodyEmpty
	AvatarBodyIsNotBase64
)

const (
	ImageError = 1700 + iota
	ImageNotFound
)

const (
	MateError = 1800 + iota
	UnknownMateRequestResult
	MateRequestAlreadySentFromYou
	MateRequestAlreadySentToYou
	MateRequestNotFound
	MateRequestNotWaiting
	MateRequestToSelf
	AlreadyAreMates
)

const (
	GeoError = 1900 + iota
)
