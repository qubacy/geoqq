package dto

type SignInInp struct {
	Login    string
	Password string
}

type SignInOut struct {
	SignedTokens
}

type SignUpInp struct {
	Login    string
	Password string
}

type SignUpOut struct {
	SignedTokens
}

type RefreshTokensOut struct {
	SignedTokens
}

type SignedTokens struct {
	AccessToken  string
	RefreshToken string
}
