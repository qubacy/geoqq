package impl

import (
	"context"
	"fmt"
	utl "geoqq/pkg/utility"
)

// Keys
// -----------------------------------------------------------------------

func signInFailedAttemptCount(username string) string {
	return fmt.Sprintf("failed_sin_attempts_by_%v", username)
}

func signInByNameBlocked(username string) string {
	return fmt.Sprintf("sin_by_%v_blocked", username)
}

func signUpByIpAddrBlocked(ipAddr string) string {
	return fmt.Sprintf("sup_by_ip_%v_blocked", ipAddr)
}

// Actions
// -----------------------------------------------------------------------

func (a *AuthService) updateSignInCache(ctx context.Context, username string) error {
	sourceFunc := a.updateHashRefreshToken

	loginBlockedKey := signInByNameBlocked(username)
	attemptCountKey := signInFailedAttemptCount(username)

	singleChar := "1"
	maxAttemptCount := a.authParams.SignIn.FailedAttemptCount
	attemptTtl := a.authParams.SignIn.FailedAttemptTtl
	blockingTime := a.authParams.SignIn.BlockingTime

	// ***

	attemptCountExists, err := a.cache.Exists(ctx, attemptCountKey)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	var strCount string
	if attemptCountExists { // user has already made a mistake!
		strCount, err = a.cache.Get(ctx, attemptCountKey)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		if uint32(len(strCount)) == maxAttemptCount-1 {
			err = a.cache.SetWithTTL(ctx, loginBlockedKey, singleChar, blockingTime)
			if err != nil {
				return utl.NewFuncError(sourceFunc, err)
			}
			if err = a.cache.Del(ctx, attemptCountKey); err != nil {
				return utl.NewFuncError(sourceFunc, err)
			}
			return nil // OK
		}
	}

	err = a.cache.SetWithTTL(ctx, attemptCountKey, strCount+singleChar, attemptTtl)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

func (a *AuthService) updateSignUpCache(ctx context.Context, ipAddr string) error {
	err := a.cache.SetWithTTL(ctx,
		signUpByIpAddrBlocked(ipAddr), "1",
		a.authParams.SignUp.BlockingTime,
	)
	if err != nil {
		return utl.NewFuncError(a.updateSignUpCache, err)
	}
	return nil
}
