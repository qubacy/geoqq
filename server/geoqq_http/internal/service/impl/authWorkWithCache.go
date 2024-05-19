package impl

import (
	"context"
	"fmt"
	utl "geoqq_http/pkg/utility"
)

// Keys
// -----------------------------------------------------------------------

type authCacheKeyFormatter struct{}

func (*authCacheKeyFormatter) DeletedUser(id uint64) string {
	return fmt.Sprintf("deleted_user_%v", id)
}

func (*authCacheKeyFormatter) SignInFailedAttemptCount(login string) string {
	return fmt.Sprintf("sin_failed_attempts_by_%v", login)
}

func (*authCacheKeyFormatter) SignInByLoginBlocked(login string) string {
	return fmt.Sprintf("sin_by_%v_blocked", login)
}

func (*authCacheKeyFormatter) SignUpByIpAddrBlocked(ipAddr string) string {
	return fmt.Sprintf("sup_by_ip_%v_blocked", ipAddr)
}

var authCacheKey = authCacheKeyFormatter{}

// Actions
// -----------------------------------------------------------------------

func (a *AuthService) updateSignInCache(ctx context.Context, login string) error {
	sourceFunc := a.updateHashRefreshToken

	keyLoginBlocked := authCacheKey.SignInByLoginBlocked(login)
	keyAttemptCount := authCacheKey.SignInFailedAttemptCount(login)

	singleChar := "1"
	maxAttemptCount := a.authParams.SignIn.FailedAttemptCount
	attemptTtl := a.authParams.SignIn.FailedAttemptTtl
	blockingTime := a.authParams.SignIn.BlockingTime

	// ***

	attemptCountExists, err := a.cache.Exists(ctx, keyAttemptCount)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}

	var strCount string
	if attemptCountExists { // user has already made a mistake!
		strCount, err = a.cache.Get(ctx, keyAttemptCount)
		if err != nil {
			return utl.NewFuncError(sourceFunc, err)
		}

		if uint64(len(strCount)) == maxAttemptCount-1 {
			err = a.cache.SetWithTTL(ctx, keyLoginBlocked, singleChar, blockingTime)
			if err != nil {
				return utl.NewFuncError(sourceFunc, err)
			}
			if err = a.cache.Del(ctx, keyAttemptCount); err != nil {
				return utl.NewFuncError(sourceFunc, err)
			}
			return nil // OK
		}
	}

	err = a.cache.SetWithTTL(ctx, keyAttemptCount, strCount+singleChar, attemptTtl)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

func (a *AuthService) updateSignUpCache(ctx context.Context, ipAddr string) error {
	err := a.cache.SetWithTTL(ctx,
		authCacheKey.SignUpByIpAddrBlocked(ipAddr), "1",
		a.authParams.SignUp.BlockingTime,
	)
	if err != nil {
		return utl.NewFuncError(a.updateSignUpCache, err)
	}
	return nil
}
