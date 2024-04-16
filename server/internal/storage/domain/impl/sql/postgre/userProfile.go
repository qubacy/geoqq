package postgre

import (
	"context"
	"errors"
	"geoqq/internal/domain"
	"geoqq/pkg/utility"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
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

func (s *UserProfileStorage) DeleteUserProfile(ctx context.Context, userId uint64) error {
	/*
		Action List:
			1. Add user to deleted list.
			2.
	*/

	sourceFunc := s.DeleteUserProfile
	conn, tx, err := begunTransaction(s.pool, ctx)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	defer conn.Release()

	// ***

	err = errors.Join(
		insertUserToDeletedInsideTx(ctx, tx, userId),
		deleteOutgoingMateRequestsInsideTx(ctx, tx, userId),
		deleteIncomingMateRequestsInsideTx(ctx, tx, userId),
		deleteMateChatsInsideTx(ctx, tx, userId),

		changeNameToDeletedForUserInsideTx(ctx, tx, userId),
		changeAvatarToDeletedForUserInsideTx(ctx, tx, userId),
		resetHashesForUserInsideTx(ctx, tx, userId),

		resetPrivacyForUserInsideTx(ctx, tx, userId),
		resetHashesForUserInsideTx(ctx, tx, userId),

		deleteUserAvatarsInsideTx(ctx, tx, userId),
	)
	if err != nil {
		err = errors.Join(tx.Rollback(ctx)) // ?
		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	err = tx.Commit(ctx)
	if err != nil {
		return utl.NewFuncError(sourceFunc, err)
	}
	return nil
}

// private
// -----------------------------------------------------------------------

func insertUserToDeletedInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

func deleteOutgoingMateRequestsInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

func deleteIncomingMateRequestsInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

func deleteMatesInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

// ?
func deleteMateChatsInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

// -----------------------------------------------------------------------

func changeNameToDeletedForUserInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

func changeAvatarToDeletedForUserInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

func resetHashesForUserInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

// -----------------------------------------------------------------------

func resetPrivacyForUserInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

func resetDescriptionForUserInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}

// -----------------------------------------------------------------------

func deleteUserAvatarsInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {

	return ErrNotImplemented
}
