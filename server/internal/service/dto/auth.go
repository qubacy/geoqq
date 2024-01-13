package dto

type SignInInp struct {
	Login    string
	Password string
}

func MakeSignInInp(login, pass string) SignInInp {
	return SignInInp{
		Login:    login,
		Password: pass,
	}
}

// -----------------------------------------------------------------------

type SignInOut struct {
	SignedTokens
}

func MakeSignInOutEmpty() SignInOut {
	return SignInOut{}
}

// -----------------------------------------------------------------------

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

// -----------------------------------------------------------------------

type SignUpOut struct {
	SignedTokens
}

func MakeSignUpOut(accessToken, refreshToken string) SignUpOut {
	return SignUpOut{
		SignedTokens: SignedTokens{
			AccessToken:  accessToken,
			RefreshToken: refreshToken,
		},
	}
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
