package postgre

import (
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"errors"
	"fmt"
	"geoqq_http/internal/domain/table"
	"geoqq_http/internal/storage/domain/dto"

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

// templates
// -----------------------------------------------------------------------

var (
	/*
		Order:
			1. login
			2. passwordDoubleHash
	*/
	templateInsertUserEntryWithoutHashUpdToken = `` +
		template.InsertUserEntryWithoutHashUpdToken

	/*
		Order:
			1. userId
			2. lon
			3. lat
	*/
	templateInsertUserLocation = `` +
		template.InsertUserLocationNoReturningId

	/*
		Order:
			1. userId
			2. username
			3. avatarId

		`AvatarId` is required parameter!
	*/
	templateInsertUserDetails = utl.RemoveAdjacentWs(`
		INSERT INTO "UserDetails" (
			"UserId", "Username", "AvatarId"
		) VALUES (
			$1, $2, $3
		)`)

	/*
		Order:
			1. userId
			2. hitMeUp
	*/
	templateInsertUserOptions = utl.RemoveAdjacentWs(`
		INSERT INTO "UserOptions" (
			"UserId", "HitMeUp") 
		VALUES ($1, $2)`)

	/*
		Order:
			1. userId
	*/
	templateUpdateLastActionTimeForUser = utl.RemoveAdjacentWs(`
		UPDATE "UserEntry" 
			SET "LastActionTime" = NOW()::timestamp
		WHERE "Id" = $1`)

	/*
		Order:
			1. userId
	*/
	templateWasUserDeleted = utl.RemoveAdjacentWs(`
		SELECT 
			case
				when COUNT(*) > 0 then TRUE
				else FALSE
			end as "IsDeleted"
		FROM "DeletedUser"
		WHERE "UserId" = $1`)

	templateWasUserWithLoginDeleted = utl.RemoveAdjacentWs(`
		SELECT 
			case
				when COUNT(*) > 0 then TRUE
				else FALSE
			end as "IsDeleted"
		FROM "DeletedUser"
		INNER JOIN "UserEntry" ON (
			"Id" = "UserId" AND "Login" = $1)`)

	/*
		Order:
			1. lon
			2. lat
			3. userId
	*/
	templateUpdateUserLocation = template.UpdateUserLocation

	templateUpdateOnlyHashRefreshTokenById = utl.RemoveAdjacentWs(`
		UPDATE "UserEntry" SET "HashUpdToken" = $1 WHERE "Id" = $2`)

	/*
		Order:
			1. userId
			2. hashUpdToken
	*/
	templateUpdateHashRefreshTokenAndSomeTimes = utl.RemoveAdjacentWs(`
		UPDATE "UserEntry"
			SET "HashUpdToken" = $2,
				"SignInTime" = NOW()::timestamp,
				"LastActionTime" = NOW()::timestamp
		WHERE "Id" = $1`)

	templateGetUserOptionsById = utl.RemoveAdjacentWs(`
		SELECT "UserId", "HitMeUp"
		FROM "UserOptions" WHERE "UserId" = $1`)
)

// public
// -----------------------------------------------------------------------

func (us *UserStorage) GetUserIdByByLogin(ctx context.Context,
	login string) (uint64, error) {
	sourceFunc := us.GetUserIdByByLogin
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT "Id" FROM "UserEntry"
			WHERE "Login" = $1;`,
		login,
	)

	var userId uint64 = 0
	err = row.Scan(&userId)
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}
	return userId, nil
}

func (us *UserStorage) GetHashRefreshToken(ctx context.Context, id uint64) (
	string, error,
) {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return "", utl.NewFuncError(us.GetHashRefreshToken, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT "HashUpdToken" FROM "UserEntry"
			WHERE "Id" = $1;`,
		id)

	var hashRefreshToken string
	err = row.Scan(&hashRefreshToken)
	if err != nil {
		return "", utl.NewFuncError(us.GetHashRefreshToken, err)
	}
	return hashRefreshToken, nil
}

func (us *UserStorage) GetUserOptionsById(ctx context.Context, id uint64) (
	*table.UserOptions, error,
) {
	sourceFunc := us.GetUserOptionsById
	row, err := queryRowWithConnectionAcquire(us.pool, ctx, func(conn *pgxpool.Conn, ctx context.Context) pgx.Row {
		return conn.QueryRow(ctx,
			templateGetUserOptionsById+`;`, id)
	})
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	userOptions := table.UserOptions{}
	err = row.Scan(
		&userOptions.UserId,
		&userOptions.HitMeUp,
	)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}
	return &userOptions, nil
}

// -----------------------------------------------------------------------

func (us *UserStorage) HasUserWithId(ctx context.Context, id uint64) (
	bool, error,
) {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserWithId, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx, template.HasUserWithId+`;`, id)

	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserWithId, err)
	}

	if count > 1 {
		return false, ErrUnexpectedResult
	}
	return count == 1, nil
}

func (us *UserStorage) HasUserWithIds(ctx context.Context, uniqueIds []uint64) (
	bool, error,
) {
	if len(uniqueIds) == 0 {
		return true, nil
	}

	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserWithIds, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		fmt.Sprintf(
			`SELECT COUNT(*) AS "Count"
				FROM "UserEntry" WHERE "Id" IN (%v);`,
			utl.NumbersToString(uniqueIds),
		),
	)

	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserWithId, err)
	}

	return count == len(uniqueIds), nil
}

func (us *UserStorage) HasUserWithLogin(ctx context.Context, login string) (
	bool, error,
) {
	sourceFunc := us.HasUserWithLogin
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	rows, err := conn.Query(ctx,
		`SELECT COUNT(*) AS "Count"
			FROM "UserEntry" WHERE "Login" = $1;`,
		login,
	)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
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

func (us *UserStorage) InsertUser(ctx context.Context,
	login, passwordDoubleHash string,
	avatarId uint64) (uint64, error) {

	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(us.InsertUser, err)
	}
	defer conn.Release()

	tx, err := conn.BeginTx(ctx, pgx.TxOptions{
		IsoLevel:       pgx.Serializable,
		AccessMode:     pgx.ReadWrite,
		DeferrableMode: pgx.NotDeferrable,
	})
	if err != nil {
		return 0, utl.NewFuncError(us.InsertUser, err)
	}

	// *** transaction ***

	lastInsertedId, err := insertUserEntryWithoutHashUpdTokenInsideTx(
		ctx, tx, login, passwordDoubleHash)
	if err != nil {
		err = errors.Join(err, tx.Rollback(ctx)) // <--- ignore error?
		return 0, utl.NewFuncError(us.InsertUser, err)
	}
	err = insertUserLocationInsideTx(ctx, tx, lastInsertedId)
	if err != nil {
		err = errors.Join(err, tx.Rollback(ctx))
		return 0, utl.NewFuncError(us.InsertUser, err)
	}
	err = insertUserDetailsInsideTx(ctx, tx, lastInsertedId, login, avatarId)
	if err != nil {
		err = errors.Join(err, tx.Rollback(ctx))
		return 0, utl.NewFuncError(us.InsertUser, err)
	}
	err = insertUserOptionsInsideTx(ctx, tx, lastInsertedId)
	if err != nil {
		err = errors.Join(err, tx.Rollback(ctx))
		return 0, utl.NewFuncError(us.InsertUser, err)
	}

	err = tx.Commit(ctx)
	if err != nil {
		return 0, utl.NewFuncError(us.InsertUser, err)
	}

	return lastInsertedId, nil
}

func (us *UserStorage) HasUserByCredentials(ctx context.Context,
	login, passwordDoubleHash string) (
	bool, error,
) {
	sourceFunc := us.HasUserByCredentials
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) AS "Count" FROM "UserEntry"
			WHERE "Login" = $1 AND "HashPassword" = $2;`,
		login, passwordDoubleHash,
	)
	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}

	if count > 1 {
		return false, ErrUnexpectedResult
	}
	return count == 1, nil
}

func (us *UserStorage) UpdateUserLocation(ctx context.Context, id uint64,
	longitude, latitude float64) error {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(us.UpdateUserLocation, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx, templateUpdateUserLocation+`;`,
		longitude, latitude, id)
	if err != nil {
		return utl.NewFuncError(us.UpdateUserLocation, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func (us *UserStorage) ResetHashRefreshToken(ctx context.Context, id uint64) error {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(us.ResetHashRefreshToken, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx,
		templateUpdateOnlyHashRefreshTokenById+`;`,
		"", id,
	)
	if err != nil {
		return utl.NewFuncError(us.ResetHashRefreshToken, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func (us *UserStorage) UpdateHashRefreshTokenAndSomeTimes(ctx context.Context,
	id uint64, refreshTokenHash string) error {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(us.UpdateHashRefreshTokenAndSomeTimes, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx,
		templateUpdateHashRefreshTokenAndSomeTimes+`;`,
		id, refreshTokenHash,
	)
	if err != nil {
		return utl.NewFuncError(us.UpdateHashRefreshTokenAndSomeTimes, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func (us *UserStorage) UpdateUserParts(ctx context.Context,
	id uint64, input *dto.UpdateUserPartsInp) error {
	if input == nil {
		return ErrNilInputParameterWithName("UpdateUserPartsInp")
	}

	// ***

	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(us.UpdateUserParts, err)
	}
	defer conn.Release()

	tx, err := conn.BeginTx(ctx, pgx.TxOptions{
		IsoLevel:       pgx.Serializable,
		AccessMode:     pgx.ReadWrite,
		DeferrableMode: pgx.NotDeferrable,
	})
	if err != nil {
		return utl.NewFuncError(us.UpdateUserParts, err)
	}

	// ***

	if input.PasswordDoubleHash != nil {
		err = errors.Join(
			updateUserHashPasswordInsideTx(ctx, tx, id, *input.PasswordDoubleHash),
			updateHashRefreshTokenInsideTx(ctx, tx, id, ""), // reset!
		)

		if err != nil {
			err = errors.Join(err, tx.Rollback(ctx))
			return utl.NewFuncError(us.UpdateUserParts, err)
		}
	}

	if input.Username != nil {
		err = changeUsernameForUserInsideTx(ctx, tx, id, *input.Username)
		if err != nil {
			err = errors.Join(err, tx.Rollback(ctx))
			return utl.NewFuncError(us.UpdateUserParts, err)
		}
	}
	if input.Description != nil {
		err = updateUserDescriptionInsideTx(ctx, tx, id, *input.Description)
		if err != nil {
			err = errors.Join(err, tx.Rollback(ctx))
			return utl.NewFuncError(us.UpdateUserParts, err)
		}
	}

	if input.AvatarId != nil {
		err = updateUserAvatarIdInsideTx(ctx, tx, id, *input.AvatarId)
		if err != nil {
			err = errors.Join(err, tx.Rollback(ctx))
			return utl.NewFuncError(us.UpdateUserParts, err)
		}
	}

	if input.Privacy != nil {
		err = updateUserPrivacyInsideTx(ctx, tx, id, *input.Privacy)
		if err != nil {
			err = errors.Join(err, tx.Rollback(ctx))
			return utl.NewFuncError(us.UpdateUserParts, err)
		}
	}

	// ***

	err = tx.Commit(ctx)
	if err != nil {
		return utl.NewFuncError(us.UpdateUserParts, err)
	}

	return nil
}

func (us *UserStorage) UpdateLastActivityTimeForUser(
	ctx context.Context, id uint64) error {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(us.UpdateLastActivityTimeForUser, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx,
		templateUpdateLastActionTimeForUser+`;`, id)
	if err != nil {
		return utl.NewFuncError(us.UpdateLastActivityTimeForUser, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func (us *UserStorage) HasUserByIdAndHashPassword(ctx context.Context,
	id uint64, passwordDoubleHash string) (
	bool, error,
) {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserByIdAndHashPassword, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) FROM "UserEntry"
			WHERE "Id" = $1 AND "HashPassword" = $2;`,
		id, passwordDoubleHash)

	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserByIdAndHashPassword, err)
	}

	if count > 1 {
		return false, ErrUnexpectedResult
	}
	return count == 1, nil
}

func (us *UserStorage) WasUserDeleted(ctx context.Context, id uint64) (bool, error) {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(us.WasUserDeleted, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		templateWasUserDeleted+`;`, id)

	isDeleted := false
	err = row.Scan(&isDeleted)
	if err != nil {
		return false, utl.NewFuncError(us.WasUserDeleted, err)
	}

	return isDeleted, nil
}

func (us *UserStorage) WasUserWithLoginDeleted(ctx context.Context, login string) (bool, error) {
	sourceFunc := us.WasUserWithLoginDeleted
	row, err := queryRowWithConnectionAcquire(us.pool, ctx,
		func(conn *pgxpool.Conn, ctx context.Context) pgx.Row {
			return conn.QueryRow(ctx, templateWasUserWithLoginDeleted+`;`,
				login)
		},
	)

	if err != nil {
		return false, utl.NewFuncError(sourceFunc, err)
	}

	return scanBool(row, sourceFunc)
}

// private
// -----------------------------------------------------------------------

func insertUserEntryWithoutHashUpdTokenInsideTx(ctx context.Context, tx pgx.Tx,
	login, passwordDoubleHash string) (
	uint64, error,
) {
	var lastInsertedId uint64
	row := tx.QueryRow(ctx,
		templateInsertUserEntryWithoutHashUpdToken+`;`,
		login, passwordDoubleHash,
	)

	err := row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(
			insertUserEntryWithoutHashUpdTokenInsideTx, err)
	}

	return lastInsertedId, nil
}

func insertUserLocationInsideTx(ctx context.Context, tx pgx.Tx,
	userId uint64) error {

	lat := 0.0
	lon := 0.0

	cmdTag, err := tx.Exec(ctx,
		templateInsertUserLocation+`;`,
		userId, lon, lat,
	)

	if err != nil {
		return utl.NewFuncError(insertUserLocationInsideTx, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

func insertUserDetailsInsideTx(ctx context.Context, tx pgx.Tx,
	userId uint64, username string, avatarId uint64) error {

	cmdTag, err := tx.Exec(ctx,
		templateInsertUserDetails+`;`,
		userId, username, avatarId,
	)

	if err != nil {
		return utl.NewFuncError(insertUserDetailsInsideTx, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

func insertUserOptionsInsideTx(ctx context.Context, tx pgx.Tx,
	userId uint64) error {

	cmdTag, err := tx.Exec(ctx,
		templateInsertUserOptions+`;`,
		userId, table.HitMeUpYes,
	)

	if err != nil {
		return utl.NewFuncError(insertUserOptionsInsideTx, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

// -----------------------------------------------------------------------

func updateUserDescriptionInsideTx(ctx context.Context, tx pgx.Tx,
	id uint64, desc string) error {
	cmdTag, err := tx.Exec(ctx,
		`UPDATE "UserDetails" SET "Description" = $1
			WHERE "UserId" = $2;`, desc, id,
	)

	if err != nil {
		return utl.NewFuncError(updateUserDescriptionInsideTx, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func updateUserAvatarIdInsideTx(ctx context.Context, tx pgx.Tx,
	id uint64, avatarId uint64) error {
	cmdTag, err := tx.Exec(ctx,
		`UPDATE "UserDetails" SET "AvatarId" = $1
			WHERE "UserId" = $2;`, avatarId, id,
	)

	if err != nil {
		return utl.NewFuncError(updateUserAvatarIdInsideTx, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func updateUserPrivacyInsideTx(ctx context.Context, tx pgx.Tx,
	id uint64, privacy dto.Privacy) error {
	cmdTag, err := tx.Exec(ctx,
		`UPDATE "UserOptions" SET "HitMeUp" = $1
			WHERE "UserId" = $2;`, privacy.HitMeUp, id,
	)

	if err != nil {
		return utl.NewFuncError(updateUserPrivacyInsideTx, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func updateUserHashPasswordInsideTx(ctx context.Context, tx pgx.Tx,
	id uint64, passwordDoubleHash string) error {
	cmdTag, err := tx.Exec(ctx,
		`UPDATE "UserEntry" SET "HashPassword" = $1
			WHERE "Id" = $2;`, passwordDoubleHash, id,
	)

	if err != nil {
		return utl.NewFuncError(updateUserHashPasswordInsideTx, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

// -----------------------------------------------------------------------

func updateHashRefreshTokenInsideTx(ctx context.Context, tx pgx.Tx,
	id uint64, refreshTokenHash string) error {

	cmdTag, err := tx.Exec(ctx,
		templateUpdateOnlyHashRefreshTokenById+`;`,
		refreshTokenHash, id,
	)

	if err != nil {
		return utl.NewFuncError(updateHashRefreshTokenInsideTx, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}
