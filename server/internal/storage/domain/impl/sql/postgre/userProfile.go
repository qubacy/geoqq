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

func (self *UserProfileStorage) GetUserProfile(ctx context.Context, id uint64) (
	domain.UserProfile, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return domain.UserProfile{},
			utility.NewFuncError(self.GetUserProfile, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT "Username", "Description", "AvatarId", "HitMeUp"
			FROM "UserEntry"
		LEFT JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
		LEFT JOIN "UserOptions" ON "UserOptions"."UserId" = "UserEntry"."Id"
		WHERE "UserEntry"."Id" = $1;`, id,
	)

	userProfile := domain.UserProfile{}
	err = row.Scan(&userProfile.Username, &userProfile.Description,
		&userProfile.AvatarId, &userProfile.Privacy.HitMeUp)

	if err != nil {
		return domain.UserProfile{},
			utility.NewFuncError(self.GetUserProfile, err)
	}

	return userProfile, nil
}
