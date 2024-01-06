package service

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/internal/service/dto"
)

type AuthService interface {
	SignIn(ctx context.Context, input dto.SignInInp) (dto.SignInOut, error)
	SignUp(ctx context.Context, input dto.SignUpInp) (dto.SignUpOut, error)
	RefreshTokens(ctx context.Context, refreshToken string) (dto.RefreshTokensOut, error)
}

// work with profiles, public users, ...
type UserService interface {
	GetProfileById(ctx context.Context, value uint64) (domain.Profile, error)
	GetUserById(ctx context.Context, value uint64) (domain.PublicUser, error)
	GetUsersByIds(ctx context.Context, values []uint64) (domain.PublicUserList, error)

	UpdateProfileById(ctx context.Context, input dto.UpdateProfileInp) error
}

type MateService interface {
}

type ImageService interface {
}

type GeoService interface {
}

// -----------------------------------------------------------------------

type Services interface {
	AuthService
	UserService
	MateService
	ImageService
	GeoService
}
