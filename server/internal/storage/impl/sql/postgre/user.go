package postgre

import (
	"context"
	"geoqq/internal/domain/table"
	"geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
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

func (self *UserStorage) GetUserIdByByCredentials(ctx context.Context,
	username, hashPassword string) (uint64, error) {

	// TODO:

	return 0, nil
}

func (self *UserStorage) HasUserWithName(ctx context.Context, value string) (
	bool, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return false, utility.NewFuncError(self.HasUserWithName, err)
	}
	defer conn.Release()

	rows, err := conn.Query(ctx,
		`SELECT COUNT(*) AS "Count"
		FROM "UserEntry" WHERE "Username" = $1;`,
		value,
	)
	if err != nil {
		return false, utility.NewFuncError(self.HasUserWithName, err)
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

func (self *UserStorage) InsertUser(ctx context.Context,
	username, hashPassword string) (
	uint64, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return 0, utility.NewFuncError(self.InsertUser, err)
	}
	defer conn.Release()

	tx, err := conn.BeginTx(ctx, pgx.TxOptions{
		IsoLevel:       pgx.Serializable,
		AccessMode:     pgx.ReadWrite,
		DeferrableMode: pgx.NotDeferrable,
	})
	if err != nil {
		return 0, utility.NewFuncError(self.InsertUser, err)
	}

	// *** transaction ***

	lastInsertedId, err := insertUserEntry(ctx, tx, username, hashPassword)
	if err != nil {
		tx.Rollback(ctx) // <--- ignore error!
		return 0, utility.NewFuncError(self.InsertUser, err)
	}
	err = insertUserLocation(ctx, tx, lastInsertedId)
	if err != nil {
		tx.Rollback(ctx)
		return 0, utility.NewFuncError(self.InsertUser, err)
	}
	err = insertUserDetails(ctx, tx, lastInsertedId)
	if err != nil {
		tx.Rollback(ctx)
		return 0, utility.NewFuncError(self.InsertUser, err)
	}
	err = insertUserOptions(ctx, tx, lastInsertedId)
	if err != nil {
		tx.Rollback(ctx)
		return 0, utility.NewFuncError(self.InsertUser, err)
	}

	err = tx.Commit(ctx)
	if err != nil {
		return 0, utility.NewFuncError(self.InsertUser, err)
	}

	return lastInsertedId, nil
}

func (self *UserStorage) HasUserByCredentials(ctx context.Context,
	username, hashPassword string) (
	bool, error,
) {
	conn, err := self.pool.Acquire(ctx)
	if err != nil {
		return false, utility.NewFuncError(self.HasUserByCredentials, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx, `SELECT COUNT(*) AS "Count" FROM "UserEntry"
		WHERE "Username" = $1 AND "HashPassword" = $2;`)
	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utility.NewFuncError(self.HasUserByCredentials, err)
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
		return utility.NewFuncError(self.UpdateUserLocation, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx, `UPDATE "UserLocation" 
		SET "Longitude" = $1, "Latitude" = $2
		WHERE "UserId" = $3;`, longitude, latitude, id)
	if err != nil {
		return utility.NewFuncError(self.UpdateUserLocation, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func (self *UserStorage) UpdateHashRefreshToken(value string) error {
	return nil
}

// private
// -----------------------------------------------------------------------

func insertUserEntry(ctx context.Context, tx pgx.Tx,
	username, hashPassword string) (
	uint64, error,
) {
	var lastInsertedId uint64
	row := tx.QueryRow(ctx,
		`INSERT INTO "UserEntry" (
			"Username", "HashPassword", 
			"SignUpTime", "SignInTime")
		VALUES(
			$1, $2,
			NOW()::timestamp, NULL
		) RETURNING "Id";`,
		username, hashPassword,
	)

	err := row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utility.NewFuncError(insertUserEntry, err)
	}

	return lastInsertedId, nil
}

func insertUserLocation(ctx context.Context, tx pgx.Tx,
	userId uint64) error {

	cmdTag, err := tx.Exec(ctx,
		`INSERT INTO "UserLocation" (
			"UserId", "Longitude", "Latitude",
			"Time"
		) 
		VALUES (
			$1, $2, $3,
			NOW()::timestamp
		);`,
		userId, 0.0, 0.0)
	if err != nil {
		return utility.NewFuncError(insertUserLocation, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

func insertUserDetails(ctx context.Context, tx pgx.Tx,
	userId uint64) error {

	cmdTag, err := tx.Exec(ctx,
		`INSERT INTO "UserDetails" ("UserId") VALUES ($1);`,
		userId)
	if err != nil {
		return utility.NewFuncError(insertUserDetails, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

func insertUserOptions(ctx context.Context, tx pgx.Tx,
	userId uint64) error {

	cmdTag, err := tx.Exec(ctx,
		`INSERT INTO "UserOptions" (
			"UserId", "HitMeUp") 
		VALUES ($1, $2);`,
		userId, table.HitMeUpYes)
	if err != nil {
		return utility.NewFuncError(insertUserOptions, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}
