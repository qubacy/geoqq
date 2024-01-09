package postgre

import (
	"context"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type UserStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newUserStorage(pool *pgxpool.Pool) *UserStorage {
	return &UserStorage{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

func (self *UserStorage) HasUserWithName(ctx context.Context, value string) (bool, error) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return false, utility.CreateCustomError(self.HasUserWithName, err)
	}
	defer conn.Release()

	rows, err := conn.Query(ctx,
		`SELECT COUNT(*) AS "Count"
		FROM "UserEntry" WHERE "Username" = $1;`,
		value,
	)
	if err != nil {
		return false, utility.CreateCustomError(self.HasUserWithName, err)
	}
	defer rows.Close()

	count := 0
	if rows.Next() {
		rows.Scan(&count)
	} else {
		return false, ErrNoRows
	}

	if count > 1 {
		return false, ErrUnexpectedResult
	}

	return count > 0, nil
}

func (self *UserStorage) InsertUser(ctx context.Context, username,
	hashPassword, hashUpdToken string) (
	uint64, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return 0, utility.CreateCustomError(self.InsertUser, err)
	}
	defer conn.Release()

	var lastInsertedId uint64
	row := conn.QueryRow(ctx,
		`INSERT INTO "UserEntry" (
			"Username", 
			"HashPassword", "HashUpdToken",
			"SignUpTime", "SignInTime")
			VALUES(
				$1, $2, $3,
				SELECT NOW()::timestamp, NULL
				) RETURNING "Id";`,
	)

	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utility.CreateCustomError(self.InsertUser, err)
	}

	return lastInsertedId, nil
}

func (self *UserStorage) HasUserByCredentials(ctx context.Context, username, hashPassword string) (bool, error) {
	return false, nil
}

func (self *UserStorage) UpdateUserLocation(ctx context.Context, id uint64,
	longitude, latitude float64) error {
	return nil
}
