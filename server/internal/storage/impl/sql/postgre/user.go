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

func (self *UserStorage) HasUserWithName(ctx context.Context, value string) (
	bool, error,
) {
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

	return count == 1, nil
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
			NOW()::timestamp, NULL
		) RETURNING "Id";`,
		username, hashPassword, hashUpdToken,
	)

	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utility.CreateCustomError(self.InsertUser, err)
	}

	return lastInsertedId, nil
}

func (self *UserStorage) HasUserByCredentials(ctx context.Context,
	username, hashPassword string) (
	bool, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return false, utility.CreateCustomError(self.HasUserByCredentials, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx, `SELECT COUNT(*) AS "Count" FROM "UserEntry"
		WHERE "Username" = $1 AND "HashPassword" = $2;`)
	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utility.CreateCustomError(self.HasUserByCredentials, err)
	}

	if count > 1 {
		return false, ErrUnexpectedResult
	}
	return count == 1, nil
}

func (self *UserStorage) UpdateUserLocation(ctx context.Context, id uint64,
	longitude, latitude float64) error {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return utility.CreateCustomError(self.UpdateUserLocation, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx, `UPDATE "UserLocation" 
		SET "Longitude" = $1, "Latitude" = $2
		WHERE "UserId" = $3;`, longitude, latitude, id)
	if err != nil {
		return utility.CreateCustomError(self.UpdateUserLocation, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}
