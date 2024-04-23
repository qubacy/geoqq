package postgre

import (
	"context"
	"errors"
	"geoqq/app/firstStart"
	"geoqq/internal/domain"
	"geoqq/internal/domain/table"
	"geoqq/internal/storage/domain/dto"
	"geoqq/pkg/logger"
	utl "geoqq/pkg/utility"

	"github.com/google/uuid"
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
	templateGetUserProfile = utl.RemoveAdjacentWs(`
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

	templateInsertUserToDeleted = utl.RemoveAdjacentWs(`
		INSERT INTO "DeletedUser" ("UserId", "Time")
		VALUES ($1, NOW()::timestamp)`)

	templateChangeNameForUser = utl.RemoveAdjacentWs(`
		UPDATE "UserEntry" SET "Username" = $1
		WHERE "Id" = $2`)

	/*
		Order:
			1. userId
			2. label
	*/
	templateSetRandomAvatarWithLabelForUser = utl.RemoveAdjacentWs(`
		UPDATE "UserDetails" SET "AvatarId" = (
			SELECT "Id" FROM "Avatar" WHERE "Label" = $2
			ORDER BY RANDOM() LIMIT 1
		) WHERE "UserId" = $1`)
)

// public
// -----------------------------------------------------------------------

func (s *UserProfileStorage) GetUserProfile(ctx context.Context, id uint64) (
	domain.UserProfile, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return domain.UserProfile{},
			utl.NewFuncError(s.GetUserProfile, err)
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
			utl.NewFuncError(s.GetUserProfile, err)
	}

	return userProfile, nil
}

func (s *UserProfileStorage) DeleteUserProfile(ctx context.Context, userId uint64) error {
	/*
		Action List:
			Below...
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
		deleteMateRequestsForUserInsideTx(ctx, tx, table.Waiting, userId),

		// ***

		changeNameToDeletedForUserInsideTx(ctx, tx, userId),
		changeAvatarToDeletedForUserInsideTx(ctx, tx, userId),

		updateUserDescriptionInsideTx(ctx, tx, userId, ""),
		updateHashRefreshTokenInsideTx(ctx, tx, userId, ""),
		resetPrivacyForUserInsideTx(ctx, tx, userId),

		// reset hash password?
	)
	if err != nil {
		err = errors.Join(err, tx.Rollback(ctx)) // ?
		logger.Error("%v", err)

		return utl.NewFuncError(sourceFunc, err)
	}

	// ***

	err = tx.Commit(ctx)
	if err != nil {
		logger.Error("%v", err)

		return utl.NewFuncError(sourceFunc, err)
	}

	// В очередь!
	// ---> deleteMateChatsInsideTx(ctx, tx, userId)
	return nil
}

// private
// -----------------------------------------------------------------------

func insertUserToDeletedInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {
	return insertInsideTx(ctx, insertDeletedMateChatInsideTx,
		tx, templateInsertUserToDeleted, userId,
	)
}

func deleteMateRequestsForUserInsideTx(ctx context.Context,
	tx pgx.Tx, result table.MateRequestResult, userId uint64) error {
	return deleteInsideTx(ctx, deleteMateRequestsForUserInsideTx,
		tx, templateDeleteMateRequestsForUser,
		userId, int16(result),
	)
}

// -----------------------------------------------------------------------

func changeNameToDeletedForUserInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {
	return updateInsideTx(ctx, changeNameToDeletedForUserInsideTx,
		tx, templateChangeNameForUser,
		uuid.NewString(), userId,
	)
}

func changeAvatarToDeletedForUserInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {
	return updateInsideTx(ctx, changeAvatarToDeletedForUserInsideTx,
		tx, templateSetRandomAvatarWithLabelForUser,
		userId, firstStart.LabelDeletedUser,
	)
}

func resetPrivacyForUserInsideTx(ctx context.Context,
	tx pgx.Tx, userId uint64) error {
	return updateUserPrivacyInsideTx(ctx, tx,
		userId, dto.MakePrivacyForDeletedUser()) // ?
}
