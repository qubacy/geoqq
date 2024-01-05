package dto

// POST /api/sign-in
// -----------------------------------------------------------------------

type SignInPostReq struct {
	Login        string `json:"login"`
	HashPassword string `json:"password"`
}

type SignInPostRes struct {
	SignedTokens
}

// POST /api/sign-up
// -----------------------------------------------------------------------

type SignUpPostReq struct {
	Login        string `json:"login"`
	HashPassword string `json:"password"`
}

type SignUpPostRes struct {
	SignedTokens
}

// PUT /api/sign-in
// -----------------------------------------------------------------------

type SignUpPutReq struct {
	RefreshToken string `json:"refresh-token"`
}

type SignUpPutRes struct {
	SignedTokens
}

// -----------------------------------------------------------------------

type SignedTokens struct {
	AccessToken  string `json:"access-token"`
	RefreshToken string `json:"refresh-token"`
}
