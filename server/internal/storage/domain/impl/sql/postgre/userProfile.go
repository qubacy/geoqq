package postgre

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type UserProfileStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newUserProfileStorage(pool *pgxpool.Pool) *UserProfileStorage {
	return &UserProfileStorage{
		pool: pool,
	}
}

// template
// -----------------------------------------------------------------------

var (
	templateGetUserProfile = utility.RemoveAdjacentWs(`
		SELECT 
			"UserEntry"."Id" AS "Id",
			"Username", "Description",
			"AvatarId", "HitMeUp"
		FROM "UserEntry"
		INNER JOIN "UserDetails"
			ON "UserDetails"."UserId" = "UserEntry"."Id"
		INNER JOIN "UserOptions" 
			ON "UserOptions"."UserId" = "UserEntry"."Id"
		WHERE "UserEntry"."Id" = $1`)
)

// public
// -----------------------------------------------------------------------

func (s *UserProfileStorage) GetUserProfile(ctx context.Context, id uint64) (
	domain.UserProfile, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return domain.UserProfile{},
			utility.NewFuncError(s.GetUserProfile, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		templateGetUserProfile+`;`, id,
	)

	userProfile := domain.UserProfile{}
	err = row.Scan(&userProfile.Id, &userProfile.Username, &userProfile.Description,
		&userProfile.AvatarId, &userProfile.Privacy.HitMeUp,
	)

	if err != nil {
		return domain.UserProfile{},
			utility.NewFuncError(s.GetUserProfile, err)
	}

	return userProfile, nil
}
