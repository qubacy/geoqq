package dto

// POST /api/sign-in
// -----------------------------------------------------------------------

type SignInPostReq struct {
	Login                string
	PasswordHashInBase64 string
}

type SignInPostRes struct {
	SignedTokens
}

func MakeSignInPostRes(access, refresh string) SignInPostRes {
	return SignInPostRes{
		SignedTokens: SignedTokens{
			AccessToken:  access,
			RefreshToken: refresh,
		},
	}
}

// POST /api/sign-up
// -----------------------------------------------------------------------

type SignUpPostReq struct {
	Login                string
	PasswordHashInBase64 string
}

type SignUpPostRes struct {
	SignedTokens
}

func MakeSignUpPostRes(access, refresh string) SignUpPostRes {
	return SignUpPostRes{
		SignedTokens: SignedTokens{
			AccessToken:  access,
			RefreshToken: refresh,
		},
	}
}

// PUT /api/sign-in
// -----------------------------------------------------------------------

type SignUpPutReq struct {
	RefreshToken string
}

type SignUpPutRes struct {
	SignedTokens
}

func MakeSignUpPutRes(access, refresh string) SignUpPutRes {
	return SignUpPutRes{
		SignedTokens: SignedTokens{
			AccessToken:  access,
			RefreshToken: refresh,
		},
	}
}

// -----------------------------------------------------------------------

type SignedTokens struct {
	AccessToken  string `json:"access-token"`
	RefreshToken string `json:"refresh-token"`
}
