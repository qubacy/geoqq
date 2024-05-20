package impl

import (
	"common/pkg/logger"
	utl "common/pkg/utility"
	"regexp"
)

func (u *UserProfileService) initializeValidators() error {

	// can create global variables!
	sourceRegexp := map[string]string{
		"username": u.userParams.NamePattern,
	}

	// ***

	u.validators = make(Validators)
	for fieldName, sourceRe := range sourceRegexp {
		re, err := regexp.Compile(sourceRe)
		if err != nil {
			return utl.NewFuncError(u.initializeValidators, err)
		}

		u.validators[fieldName] = re
		logger.Info("%v pattern: %v", fieldName, re.String())
	}
	return nil
}

func (u *UserProfileService) validateUsername(username string) error {
	usernameValidator := u.validators["username"]
	if len(usernameValidator.String()) != 0 {
		if !usernameValidator.MatchString(username) {
			return ErrIncorrectUsernameWithPattern(
				usernameValidator.String())
		}
	}
	return nil
}
