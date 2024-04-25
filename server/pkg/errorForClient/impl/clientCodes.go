package impl

// interpretation on client application!

const errorOffset = 1_000
const errorGroupSize = 100

const (
	NoError = 0
)

const ( // only for: HTTP 500
	ServerError = errorOffset + iota // those rare moments...
	HashManagerError
	TokenManagerError
	AvatarGeneratorError
	DomainStorageError
	FileStorageError
)

// Parse
// -----------------------------------------------------------------------

const (

	// handler

	/*
		Internal function (Request.ParseForm)
		for form validation.
	*/
	ParseAnyFormFailed = errorOffset + errorGroupSize + iota // 1100

	/*
		An attempt to bind
		`Query Parameters` to structure (or single variables).
			Sometimes internal function (ShouldBindUri)

		Consist of checks:
			- availability;
			- type.
	*/
	ParseRequestQueryParamsFailed // uri, x-www-form-urlencoded... (url.Values?)

	/*
		An attempt to bind
		`Request Body` to structure.
			Sometimes internal function (ShouldBindJSON)
	*/
	ParseRequestJsonBodyFailed // (Request.Body)

	/*
		Weak checks of any parameters
			after binding.

		Examples of checks:
			- empty string;
			-
	*/
	ValidateRequestParamsFailed // any request variable (Body, Url...)
	/*
		Special case for access token.
			Same as the general case (see above!).

		HTTP 401
	*/
	ValidateRequestAccessTokenFailed
)

// By Domains
// -----------------------------------------------------------------------

const ( // general (middleware, ...)
	/*
		The token is not valid for one of the reasons:
			- incorrect format;
			- expired...
	*/
	ValidateAccessTokenFailed = errorOffset + errorGroupSize*2 + iota // 1200

	CountMoreThanPermissible
)

const (
	AuthError = errorOffset + errorGroupSize*4 + iota // 1400

	/*
		When registering or authorizing,
			the login and password do not follow the pattern.

		See: authValidators.go
	*/
	ValidateAuthParamsFailed
	/*
		Invalid token when trying to update a pair.
			Check request: PUT /api/sign-in
	*/
	ValidateRefreshTokenFailed

	UserAlreadyExist
	UserNotFound // only auth!!!
	OneOrMoreUsersNotFound

	InvalidRefreshToken // when the token hash is updated in database
	InvalidAccessToken  // when a user is deleted

	/*
		.

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
