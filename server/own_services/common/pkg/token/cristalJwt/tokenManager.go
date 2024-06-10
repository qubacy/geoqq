package cristalJwt

import (
	"common/pkg/token"
	"common/pkg/utility"
	"encoding/json"
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
		return "", utility.NewFuncError(s.New, err)
	}

	claims := &userClaims{
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(
				time.Now().UTC().Add(duration)), // accuracy: second!
		},
		Payload: payload,
	}

	builder := jwt.NewBuilder(signer)
	token, err := builder.Build(claims)
	if err != nil {
		return "", utility.NewFuncError(s.New, err)
	}
	return token.String(), nil
}

// TokenExtractor
// -----------------------------------------------------------------------

func (s *TokenManager) Parse(tokenValue string) (token.Payload, error) {
	tokenObject, _, err := prepareAndCheck(s.signingKey, tokenValue)
	if err != nil {
		return token.Payload{},
			utility.NewFuncError(s.Parse, err)
	}

	// ***

	claims, err := extractAndCheck(tokenObject)
	if err != nil {
		return token.Payload{},
			utility.NewFuncError(s.Parse, err)
	}

	// ***

	return claims.Payload, nil
}

func (s *TokenManager) ParseAccess(tokenValue string) (token.Payload, error) {
	payload, err := s.Parse(tokenValue)
	if err != nil {
		return token.Payload{},
			utility.NewFuncError(s.ParseAccess, err)
	}

	if payload.Purpose != token.ForAccess {
		return token.Payload{}, ErrTokenIsNotPurposedForAccess
	}

	return payload, nil
}

func (s *TokenManager) ParseRefresh(tokenValue string) (token.Payload, error) {
	payload, err := s.Parse(tokenValue)
	if err != nil {
		return token.Payload{},
			utility.NewFuncError(s.ParseRefresh, err)
	}

	if payload.Purpose != token.ForRefresh {
		return token.Payload{}, ErrTokenIsNotPurposedForRefresh
	}

	return payload, nil
}

// -----------------------------------------------------------------------

func (s *TokenManager) Validate(tokenValue string) error {
	_, verifier, err := prepareAndCheck(s.signingKey, tokenValue)
	if err != nil {
		return utility.NewFuncError(prepareAndCheck, err)
	}

	// parse claims!

	var claims userClaims
	err = jwt.ParseClaims([]byte(tokenValue), verifier, &claims)
	if err != nil {
		return utility.NewFuncError(prepareAndCheck, err)
	}

	if err = claims.validate(); err != nil {
		return utility.NewFuncError(prepareAndCheck, err)
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
		return nil, nil, utility.NewFuncError(prepareAndCheck, err)
	}

	tokenObject, err := jwt.Parse([]byte(tokenValue), verifierHs)
	if err != nil {
		return nil, nil, utility.NewFuncError(prepareAndCheck, err)
	}

	// ***

	err = verifierHs.Verify(tokenObject)
	if err != nil {
		return nil, nil, utility.NewFuncError(prepareAndCheck, err)
	}
	return tokenObject, verifierHs, nil
}

func extractAndCheck(tokenObject *jwt.Token) (userClaims, error) {
	var claims userClaims
	err := json.Unmarshal(tokenObject.Claims(), &claims)
	if err != nil {
		return userClaims{},
			utility.NewFuncError(extractAndCheck, err)
	}

	// ***

	if err = claims.validate(); err != nil {
		return userClaims{},
			utility.NewFuncError(extractAndCheck, err)
	}

	return claims, nil
}
