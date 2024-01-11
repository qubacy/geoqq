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

func MakeSignUpInp(login, pass string) SignUpInp {
	return SignUpInp{
		Login:    login,
		Password: pass,
	}
}

type SignUpOut struct {
	SignedTokens
}

func MakeSignUpOutEmpty() SignUpOut {
	return SignUpOut{}
}

type RefreshTokensOut struct {
	SignedTokens
}

type SignedTokens struct {
	AccessToken  string
	RefreshToken string
}
