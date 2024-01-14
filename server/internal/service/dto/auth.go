package dto

// sign-in
// -----------------------------------------------------------------------

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

func MakeSignInOut(accessToken, refreshToken string) SignInOut {
	return SignInOut{
		SignedTokens: SignedTokens{
			AccessToken:  accessToken,
			RefreshToken: refreshToken,
		},
	}
}

func MakeSignInOutEmpty() SignInOut {
	return SignInOut{}
}

// sign-up
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

// refresh
// -----------------------------------------------------------------------

type RefreshTokensOut struct {
	SignedTokens
}

// parts
// -----------------------------------------------------------------------

type SignedTokens struct {
	AccessToken  string
	RefreshToken string
}
