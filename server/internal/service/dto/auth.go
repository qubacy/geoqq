package dto

// sign-in
// -----------------------------------------------------------------------

type SignInInp struct {
	Login        string
	PasswordHash string
}

func MakeSignInInp(login, passwordHash string) SignInInp {
	return SignInInp{
		Login:        login,
		PasswordHash: passwordHash,
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
	Login        string
	PasswordHash string
}

func MakeSignUpInp(login, passwordHash string) SignUpInp {
	return SignUpInp{
		Login:        login,
		PasswordHash: passwordHash,
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

func MakeRefreshTokensOut(accessToken, refreshToken string) RefreshTokensOut {
	return RefreshTokensOut{
		SignedTokens: SignedTokens{
			AccessToken:  accessToken,
			RefreshToken: refreshToken,
		},
	}
}

func MakeRefreshTokensOutEmpty() RefreshTokensOut {
	return RefreshTokensOut{
		SignedTokens: SignedTokens{},
	}
}

// parts
// -----------------------------------------------------------------------

type SignedTokens struct {
	AccessToken  string
	RefreshToken string
}
