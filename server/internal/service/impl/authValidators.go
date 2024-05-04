package impl

import (
	"geoqq/pkg/logger"
	utl "geoqq/pkg/utility"
	"regexp"
)

func (a *AuthService) initializeValidators() error {

	// can create global variables!
	sourceRegexp := map[string]string{
		"login":    a.authParams.LoginPattern,
		"password": a.authParams.PasswordPattern,
	}

	// ***

	a.validators = make(map[string]*regexp.Regexp)
	for fieldName, sourceRe := range sourceRegexp {
		re, err := regexp.Compile(sourceRe)
		if err != nil {
			return utl.NewFuncError(a.initializeValidators, err)
		}

		a.validators[fieldName] = re
		logger.Info("%v pattern: %v", fieldName, re.String())
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
			return ErrIncorrectLoginWithPattern(
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
