package impl

import (
	"encoding/json"
	"geoqq/pkg/token"
	"geoqq/pkg/utility"
	"time"

	"github.com/cristalhq/jwt/v5"
)

type TokenManager struct {
	signingKey string

	// TODO: or do this?
	/*
		accessTokenTTL time.Duration
		updateTokenTTL time.Duration
	*/
}

func NewTokenManager(signingKey string) (*TokenManager, error) {
	if len(signingKey) == 0 {
		return nil, ErrSigningKeyIsEmpty
	}

	return &TokenManager{
		signingKey: signingKey,
	}, nil
}

// TokenCreator
// -----------------------------------------------------------------------

func (s *TokenManager) New(payload token.Payload, duration time.Duration) (string, error) {
	key := []byte(s.signingKey)
	signer, err := jwt.NewSignerHS(jwt.HS256, key)
	if err != nil {
		return "", utility.CreateCustomError(s.New, err)
	}

	claims := &userClaims{
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(
				time.Now().UTC().Add(duration)), // !
		},
		Payload: payload,
	}

	builder := jwt.NewBuilder(signer)
	token, err := builder.Build(claims)
	if err != nil {
		return "", utility.CreateCustomError(s.New, err)
	}
	return token.String(), nil
}

// TokenExtractor
// -----------------------------------------------------------------------

func (s *TokenManager) Parse(tokenValue string) (token.Payload, error) {
	tokenObject, _, err := prepareAndCheck(s.signingKey, tokenValue)
	if err != nil {
		return token.Payload{},
			utility.CreateCustomError(s.Parse, err)
	}

	// ***

	claims, err := extractAndCheck(tokenObject)
	if err != nil {
		return token.Payload{},
			utility.CreateCustomError(s.Parse, err)
	}

	// ***

	return claims.Payload, nil
}

func (s *TokenManager) Validate(tokenValue string) error {
	_, verifier, err := prepareAndCheck(s.signingKey, tokenValue)
	if err != nil {
		return utility.CreateCustomError(prepareAndCheck, err)
	}

	// parse only claims!
	var claims userClaims
	err = jwt.ParseClaims([]byte(tokenValue), verifier, &claims)
	if err != nil {
		return utility.CreateCustomError(prepareAndCheck, err)
	}

	if err = claims.validate(); err != nil {
		return utility.CreateCustomError(prepareAndCheck, err)
	}
	return nil
}

// private
// -----------------------------------------------------------------------

func prepareAndCheck(signingKey, tokenValue string) (*jwt.Token, *jwt.HSAlg, error) {
	// with user-friendly errors?
	//           |
	//           V
	verifierHs, err := jwt.NewVerifierHS(jwt.HS256, []byte(signingKey))
	if err != nil {
		return nil, nil, utility.CreateCustomError(prepareAndCheck, err)
	}

	tokenObject, err := jwt.Parse([]byte(tokenValue), verifierHs)
	if err != nil {
		return nil, nil, utility.CreateCustomError(prepareAndCheck, err)
	}

	// ***

	err = verifierHs.Verify(tokenObject)
	if err != nil {
		return nil, nil, utility.CreateCustomError(prepareAndCheck, err)
	}
	return tokenObject, verifierHs, nil
}

func extractAndCheck(tokenObject *jwt.Token) (userClaims, error) {
	var claims userClaims
	err := json.Unmarshal(tokenObject.Claims(), &claims)
	if err != nil {
		return userClaims{},
			utility.CreateCustomError(extractAndCheck, err)
	}

	// ***

	if err = claims.validate(); err != nil {
		return userClaims{},
			utility.CreateCustomError(extractAndCheck, err)
	}

	return claims, nil
}
