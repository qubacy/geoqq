package impl

// interpretation on client application!

const clientErrorOffset = 1_000
const errorGroupSize = 200

const (
	NoError = 0
)

const ( // only for: HTTP 500
	ServerError = errorGroupSize + iota // those rare moments...
	HashManagerError
	TokenManagerError
	AvatarGeneratorError
	DomainStorageError
	FileStorageError
)

// Parse
// -----------------------------------------------------------------------

const (
	ParseError = clientErrorOffset + iota // 1000

	/*
		Internal function (Request.ParseForm)
		for form validation.
	*/
	ParseAnyFormFailed

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

/*
	Client App:

		GENERAL(ErrorDomainType.SHARED), 	// 1200
		AUTH(ErrorDomainType.SHARED), 		// 1400
		USER(ErrorDomainType.SHARED), 	 	// 1600
		MY_PROFILE(ErrorDomainType.SHARED),	// 1800
		IMAGE(ErrorDomainType.SHARED),		// 2000
		MATE(ErrorDomainType.SHARED), 		// 2200
		GEO(ErrorDomainType.SHARED); 		// 2400
*/

const ( // general (middleware, ...)
	/*
		The token is not valid for one of the reasons:
			- incorrect format;
			- expired...;

			- when a user is deleted.
	*/
	GeneralError = clientErrorOffset + errorGroupSize + iota // 1200
	ValidateAccessTokenFailed

	UserWasPreviouslyDeleted
	CountMoreThanPermissible
	/*
		Actions:
			- sign in;
			- update profile.
	*/
	PasswordHashIsNotHex
)

const ( // Auth
	AuthError = clientErrorOffset + errorGroupSize*2 + iota // 1400

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

	/*
		Actions:
			- registration.
	*/
	UserWithNameAlreadyExists
	/*
		Actions:
			- sign in.
	*/
	UserByCredentialsNotFound // 1404
)

const ( // User
	UserError = clientErrorOffset + errorGroupSize*3 + iota // 1600
	UserNotFound
	OneOrMoreUsersNotFound
)

const ( // My Profile (User Profile?)
	MyProfileError = clientErrorOffset + errorGroupSize*4 + iota // 1800
	IncorrectPasswordWhenUpdate

	// TODO: Invalid Avatar id
)

const ( // Image
	ImageError = clientErrorOffset + errorGroupSize*5 + iota // 2000

	/*
		Actions:
			- user adds a new image;
			- update user profile with attached image (deprecated);
	*/
	UnknownImageExtension
	ImageContentIsEmpty
	ImageBodyIsNotBase64

	ImageNotFound
	OneOrMoreImagesNotFound
)

const ( // Mate
	MateError = clientErrorOffset + errorGroupSize*6 + iota // 2200
	UnknownMateRequestResult

	MateRequestAlreadySentFromYou
	MateRequestAlreadySentToYou

	MateRequestNotFound
	MateRequestNotWaiting

	MateRequestToSelf
	AlreadyAreMates

	TargetUserDeleted
	TargetUserNotFound

	MateChatNotFound     // 2210
	MateChatNotAvailable // 2211
)

const ( // Geo
	GeoError = clientErrorOffset + errorGroupSize*7 + iota // 2400

	WrongLatitude
	WrongLongitude
)
