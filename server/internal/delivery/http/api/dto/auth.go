package dto

// POST /api/sign-in
// -----------------------------------------------------------------------

type SignInPostReq struct {
	Login        string
	HashPassword string
}

type SignInPostRes struct {
	SignedTokens
}

// POST /api/sign-up
// -----------------------------------------------------------------------

type SignUpPostReq struct {
	Login        string
	HashPassword string
}

type SignUpPostRes struct {
	SignedTokens
}

// PUT /api/sign-in
// -----------------------------------------------------------------------

type SignUpPutReq struct {
	RefreshToken string
}

type SignUpPutRes struct {
	SignedTokens
}

// -----------------------------------------------------------------------

type SignedTokens struct {
	AccessToken  string `json:"access-token"`
	RefreshToken string `json:"refresh-token"`
}
