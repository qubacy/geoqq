package postgre

import (
	"context"
	"errors"
	"fmt"
	"geoqq/internal/domain"
	"geoqq/internal/domain/table"
	"geoqq/internal/storage/domain/dto"
	utl "geoqq/pkg/utility"

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
	templateSelectPublicUsers = `
		SELECT 
			"UserEntry"."Id" AS "Id",
			"Username", "Description", "AvatarId",
			case when "Mate"."Id" is null then false else true end as "IsMate"
		FROM "UserEntry"
		INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
		LEFT JOIN "Mate" ON (
			("Mate"."FirstUserId" = $1 AND "Mate"."SecondUserId" = "UserEntry"."Id") OR
        	("Mate"."FirstUserId" = "UserEntry"."Id" AND "Mate"."SecondUserId" = $1)
		)
		WHERE "UserEntry"."Id"` // next placeholders start with 2.

	templateUpdateUserLocation = utl.RemoveAdjacentWs(`
		UPDATE "UserLocation" 
			SET "Longitude" = $1,
			    "Latitude" = $2
		WHERE "UserId" = $3`)

	templateUpdateOnlyHashRefreshTokenById = utl.RemoveAdjacentWs(`
		UPDATE "UserEntry" SET "HashUpdToken" = $1 WHERE "Id" = $2`)
)

// public
// -----------------------------------------------------------------------

func (s *UserStorage) GetPublicUserById(ctx context.Context, userId, targetUserId uint64) (
	*domain.PublicUser, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetPublicUserById, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx,
		templateSelectPublicUsers+` = $2;`, userId, targetUserId,
	)

	publicUser := domain.PublicUser{}
	err = row.Scan(
		&publicUser.Id,
		&publicUser.Username,
		&publicUser.Description,
		&publicUser.AvatarId,
		&publicUser.IsMate,
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetPublicUserById, err)
	}
	return &publicUser, nil
}

func (s *UserStorage) GetPublicUsersByIds(ctx context.Context,
	userId uint64, targetUserIds []uint64) (domain.PublicUserList, error) {
	if len(targetUserIds) == 0 {
		return domain.PublicUserList{}, nil
	}

	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetPublicUsersByIds, err)
	}
	defer conn.Release()

	// ***

	rows, err := conn.Query(ctx,
		templateSelectPublicUsers+fmt.Sprintf(` IN (%v);`,
			utl.NumbersToString(targetUserIds)),
		userId,
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetPublicUsersByIds, err)
	}
	defer rows.Close()

	// ***

	publicUsers := domain.PublicUserList{}
	for rows.Next() {
		publicUser := domain.PublicUser{}
		err = rows.Scan(
			&publicUser.Id,
			&publicUser.Username,
			&publicUser.Description,
			&publicUser.AvatarId,
			&publicUser.IsMate,
		)
		if err != nil {
			return nil, utl.NewFuncError(s.GetPublicUsersByIds, err)
		}

		publicUsers = append(publicUsers, &publicUser)
	}

	return publicUsers, nil
}

// -----------------------------------------------------------------------

func (us *UserStorage) GetUserIdByByName(ctx context.Context,
	value string) (uint64, error) {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(us.GetUserIdByByName, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT "Id" FROM "UserEntry"
			WHERE "Username" = $1;`,
		value)

	var userId uint64 = 0
	err = row.Scan(&userId)
	if err != nil {
		return 0, utl.NewFuncError(us.GetUserIdByByName, err)
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

// -----------------------------------------------------------------------

func (us *UserStorage) HasUserWithId(ctx context.Context, id uint64) (
	bool, error,
) {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserWithId, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) AS "Count" FROM "UserEntry"
			WHERE "Id" = $1;`,
		id)

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

func (us *UserStorage) HasUserWithName(ctx context.Context, value string) (
	bool, error,
) {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserWithName, err)
	}
	defer conn.Release()

	rows, err := conn.Query(ctx,
		`SELECT COUNT(*) AS "Count"
			FROM "UserEntry" WHERE "Username" = $1;`,
		value,
	)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserWithName, err)
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
	username, hashPassword string, avatarId uint64) (
	uint64, error,
) {
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

	lastInsertedId, err := insertUserEntry(ctx, tx, username, hashPassword)
	if err != nil {
		tx.Rollback(ctx) // <--- ignore error!
		return 0, utl.NewFuncError(us.InsertUser, err)
	}
	err = insertUserLocation(ctx, tx, lastInsertedId)
	if err != nil {
		tx.Rollback(ctx)
		return 0, utl.NewFuncError(us.InsertUser, err)
	}
	err = insertUserDetails(ctx, tx, lastInsertedId, avatarId)
	if err != nil {
		tx.Rollback(ctx)
		return 0, utl.NewFuncError(us.InsertUser, err)
	}
	err = insertUserOptions(ctx, tx, lastInsertedId)
	if err != nil {
		tx.Rollback(ctx)
		return 0, utl.NewFuncError(us.InsertUser, err)
	}

	err = tx.Commit(ctx)
	if err != nil {
		return 0, utl.NewFuncError(us.InsertUser, err)
	}

	return lastInsertedId, nil
}

func (us *UserStorage) HasUserByCredentials(ctx context.Context,
	username, hashPassword string) (
	bool, error,
) {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserByCredentials, err)
	}
	defer conn.Release()

	row := conn.QueryRow(ctx,
		`SELECT COUNT(*) AS "Count" FROM "UserEntry"
			WHERE "Username" = $1 AND "HashPassword" = $2;`, username, hashPassword)
	count := 0
	err = row.Scan(&count)
	if err != nil {
		return false, utl.NewFuncError(us.HasUserByCredentials, err)
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

func (us *UserStorage) UpdateHashRefreshTokenAndEntryTime(ctx context.Context,
	id uint64, value string) error {
	conn, err := us.pool.Acquire(ctx)
	if err != nil {
		return utl.NewFuncError(us.UpdateHashRefreshTokenAndEntryTime, err)
	}
	defer conn.Release()

	cmdTag, err := conn.Exec(ctx,
		`UPDATE "UserEntry"
    		SET "HashUpdToken" = $1,
        		"SignInTime" = NOW()::timestamp
    		WHERE "Id" = $2;`, value, id)
	if err != nil {
		return utl.NewFuncError(us.UpdateHashRefreshTokenAndEntryTime, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func (us *UserStorage) UpdateUserParts(ctx context.Context,
	id uint64, input dto.UpdateUserPartsInp) error {
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

	if input.Description != nil {
		err = updateUserDescription(ctx, tx, id, *input.Description)
		if err != nil {
			tx.Rollback(ctx)
			return utl.NewFuncError(us.UpdateUserParts, err)
		}
	}
	if input.AvatarId != nil {
		err = updateUserAvatarId(ctx, tx, id, *input.AvatarId)
		if err != nil {
			tx.Rollback(ctx)
			return utl.NewFuncError(us.UpdateUserParts, err)
		}
	}
	if input.Privacy != nil {
		err = updateUserPrivacy(ctx, tx, id, *input.Privacy)
		if err != nil {
			tx.Rollback(ctx)
			return utl.NewFuncError(us.UpdateUserParts, err)
		}
	}
	if input.HashPassword != nil {
		err = errors.Join(
			updateUserHashPassword(ctx, tx, id, *input.HashPassword),
			updateHashRefreshToken(ctx, tx, id, ""), // reset!
		)

		if err != nil {
			tx.Rollback(ctx)
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

func (us *UserStorage) HasUserByIdAndHashPassword(ctx context.Context,
	id uint64, hashPassword string) (
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
		id, hashPassword)

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
		return 0, utl.NewFuncError(insertUserEntry, err)
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
		return utl.NewFuncError(insertUserLocation, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

func insertUserDetails(ctx context.Context, tx pgx.Tx,
	userId, avatarId uint64) error {

	// Avatar id is required parameter!
	cmdTag, err := tx.Exec(ctx,
		`INSERT INTO "UserDetails" (
			"UserId", "AvatarId"
		) VALUES (
			$1, $2
		);`,
		userId, avatarId)
	if err != nil {
		return utl.NewFuncError(insertUserDetails, err)
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
		return utl.NewFuncError(insertUserOptions, err)
	}
	if !cmdTag.Insert() {
		return ErrInsertFailed
	}

	return nil
}

// -----------------------------------------------------------------------

func updateUserDescription(ctx context.Context, tx pgx.Tx,
	id uint64, desc string) error {
	cmdTag, err := tx.Exec(ctx,
		`UPDATE "UserDetails" SET "Description" = $1
			WHERE "UserId" = $2;`, desc, id,
	)
	if err != nil {
		return utl.NewFuncError(updateUserDescription, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func updateUserAvatarId(ctx context.Context, tx pgx.Tx,
	id uint64, avatarId uint64) error {
	cmdTag, err := tx.Exec(ctx,
		`UPDATE "UserDetails" SET "AvatarId" = $1
			WHERE "UserId" = $2;`, avatarId, id,
	)
	if err != nil {
		return utl.NewFuncError(updateUserAvatarId, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func updateUserPrivacy(ctx context.Context, tx pgx.Tx,
	id uint64, privacy dto.Privacy) error {
	cmdTag, err := tx.Exec(ctx,
		`UPDATE "UserOptions" SET "HitMeUp" = $1
			WHERE "UserId" = $2;`, privacy.HitMeUp, id,
	)
	if err != nil {
		return utl.NewFuncError(updateUserPrivacy, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

func updateUserHashPassword(ctx context.Context, tx pgx.Tx,
	id uint64, hashValue string) error {
	cmdTag, err := tx.Exec(ctx,
		`UPDATE "UserEntry" SET "HashPassword" = $1
			WHERE "Id" = $2;`, hashValue, id, // hash-hash-password!
	)
	if err != nil {
		return utl.NewFuncError(updateUserHashPassword, err)
	}
	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}

// -----------------------------------------------------------------------

func updateHashRefreshToken(ctx context.Context, tx pgx.Tx,
	id uint64, hashValue string) error {

	cmdTag, err := tx.Exec(ctx,
		templateUpdateOnlyHashRefreshTokenById+`;`,
		hashValue, id,
	)
	if err != nil {
		return utl.NewFuncError(updateHashRefreshToken, err)
	}

	if !cmdTag.Update() {
		return ErrUpdateFailed
	}

	return nil
}
