package impl

// interpretation on client application!

const errorOffset = 1_000
const errorGroupSize = 100

const (
	NoError = 0
)

const ( // HTTP 500
	ServerError = errorOffset + iota
	HashManagerError
	TokenManagerError
	AvatarGeneratorError
	DomainStorageError
	FileStorageError
)

// Common
// -----------------------------------------------------------------------

const ( // parse

	/*
		Internal function
		for form validation.
	*/
	ParseAnyFormFailed = errorOffset + errorGroupSize + iota

	/*
		An attempt to bind
		query parameters to structures.

		Consist of checks:
			- availability;
			- type.
	*/
	ParseRequestParamsFailed // uri, x-www-form-urlencoded...

	ParseRequestJsonBodyFailed
	ValidateRequestFailed // any content

	ParseAccessTokenFailed

	// service
	ValidateInputParamsFailed
	CountMoreThanPermissible
)

const ( // middleware

)

// -----------------------------------------------------------------------

// parse

// middleware <--- userIde

// middleware
const (
	ValidateAccessTokenFailed = 1200 + iota
	ValidateRefreshTokenFailed
)

const (
	AuthError = 1500 + iota
	UserAlreadyExist
	UserNotFound // only auth!!!
	OneOrMoreUsersNotFound

	InvalidRefreshToken // when the token hash is updated in database
	InvalidAccessToken  // when a user is deleted

	/*
		dsddddsjdjdsldsldk sd!!!

	*/
	PasswordHashIsNotHex
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
	OneOrMoreImagesNotFound
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
	TargetUserDeleted

	TargetUserNotFound // TODO:!!!

	MateChatNotFound
	MateChatNotAvailable
)

const (
	GeoError = 1900 + iota
	WrongLatitude
	WrongLongitude
)
