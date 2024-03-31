package impl

import (
	"geoqq/internal/service/dto"
	utl "geoqq/pkg/utility"
	"regexp"
)

func (a *AuthService) initializeValidators() error {

	// can create global variables!
	sourceRegexp := map[string]string{
		"login":    "^[A-Za-z0-9_]{5,64}$",
		"password": `^[\s\S]+$`, // <--- checked on client!
	}

	// ***

	a.validators = make(map[string]*regexp.Regexp)
	for fieldName, sourceRe := range sourceRegexp {
		re, err := regexp.Compile(sourceRe)
		if err != nil {
			return utl.NewFuncError(a.initializeValidators, err)
		}

		a.validators[fieldName] = re
	}
	return nil
}

func (a *AuthService) validateLoginAndPassword(
	login, passwordHash string) error {

	loginValidator := a.validators["login"]
	passwordValidator := a.validators["password"]

	// ***

	if len(loginValidator.String()) != 0 {
		if !loginValidator.MatchString(login) {
			return ErrIncorrectUsernameWithPattern(
				loginValidator.String())
		}
	}

	if len(passwordValidator.String()) != 0 {
		if !passwordValidator.MatchString(passwordHash) {
			return ErrIncorrectPassword // just an error with no func name!
		}
	}

	return nil
}

// concrete
// -----------------------------------------------------------------------

func (a *AuthService) validateSingUp(input dto.SignUpInp) error {
	return a.validateLoginAndPassword(
		input.Login, input.PasswordHash)
}

func (a *AuthService) validateSingIn(input dto.SignInInp) error {
	return a.validateLoginAndPassword(
		input.Login, input.PasswordHash)
}
