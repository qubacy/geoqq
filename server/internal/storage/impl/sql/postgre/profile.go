package postgre

import (
	"context"
	"geoqq/internal/domain"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type ProfileStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newProfileStorage(pool *pgxpool.Pool) *ProfileStorage {
	return &ProfileStorage{
		pool: pool,
	}
}

func (self *ProfileStorage) GetProfileById(ctx context.Context, id uint64) (
	domain.Profile, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return domain.Profile{},
			utility.NewFuncError(self.GetProfileById, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT "Username", "Description", "AvatarId", "HitMeUp"
			FROM "UserEntry"
		LEFT JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
		LEFT JOIN "UserOptions" ON "UserOptions"."UserId" = "UserEntry"."Id"
		WHERE "UserEntry"."Id" = $1;`, id,
	)

	profile := domain.Profile{}
	err = row.Scan(&profile.Username, &profile.Description,
		&profile.AvatarId, &profile.Privacy.HitMeUp)

	if err != nil {
		return domain.Profile{},
			utility.NewFuncError(self.GetProfileById, err)
	}

	return profile, nil
}
