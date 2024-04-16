package postgre

import (
	"context"
	"fmt"
	"geoqq/internal/domain"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type PublicUserStorage struct {
	pool *pgxpool.Pool
}

// private ctor
// -----------------------------------------------------------------------

func newPublicUserStorage(pool *pgxpool.Pool) *PublicUserStorage {
	return &PublicUserStorage{
		pool: pool,
	}
}

// templates
// -----------------------------------------------------------------------

var (
	/*
		Order:
			1. source userId
			2. target userId (or some ids)
	*/
	templateSelectPublicUsers = utl.RemoveAdjacentWs(`
		SELECT 
			"UserEntry"."Id" AS "Id",
			"Username",
			"Description",
			"AvatarId",
			"LastActionTime",
			case
				when "Mate"."Id" is null then false
				else true
			end as "IsMate",
			case 
				when "DeletedUser"."UserId" is null then false
				else true
			end as "IsDeleted",
			"UserOptions"."HitMeUp" AS "HitMeUp"
		FROM "UserEntry"
		INNER JOIN "UserDetails" ON "UserDetails"."UserId" = "UserEntry"."Id"
		INNER JOIN "UserOptions" ON "UserOptions"."UserId" = "UserEntry"."Id"
		LEFT JOIN "Mate" ON (
			("Mate"."FirstUserId" = $1 AND
				"Mate"."SecondUserId" = "UserEntry"."Id") OR
        	("Mate"."FirstUserId" = "UserEntry"."Id" AND
				"Mate"."SecondUserId" = $1)
		)
		LEFT JOIN "DeletedUser" ON "DeletedUser"."UserId" = "UserEntry"."Id"
			WHERE "UserEntry"."Id"`) // next placeholders start with 2.

)

// public
// -----------------------------------------------------------------------

func (s *PublicUserStorage) GetPublicUserById(ctx context.Context, userId, targetUserId uint64) (
	*domain.PublicUser, error,
) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetPublicUserById, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx,
		templateSelectPublicUsers+` = $2;`,
		userId, targetUserId,
	)

	publicUser, err := publicUserFromQueryResult(row)
	if err != nil {
		return nil, utl.NewFuncError(s.GetPublicUserById, err)
	}
	return &publicUser, nil
}

func (s *PublicUserStorage) GetPublicUsersByIds(ctx context.Context,
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
			utl.NumbersToString(targetUserIds),
		), userId,
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetPublicUsersByIds, err)
	}
	defer rows.Close()

	// ***

	publicUsers := domain.PublicUserList{}
	for rows.Next() {
		publicUser, err := publicUserFromQueryResult(rows)
		if err != nil {
			return nil, utl.NewFuncError(s.GetPublicUsersByIds, err)
		}

		publicUsers = append(publicUsers, &publicUser)
	}

	return publicUsers, nil
}

// convert
// -----------------------------------------------------------------------

func publicUserFromQueryResult(queryResult QueryResultScanner) (
	domain.PublicUser, error,
) {
	publicUser := domain.PublicUser{}
	err := queryResult.Scan(
		&publicUser.Id,
		&publicUser.Username,
		&publicUser.Description,
		&publicUser.AvatarId,
		&publicUser.LastActionTime,
		&publicUser.IsMate,
		&publicUser.IsDeleted,
		&publicUser.HitMeUp,
	)
	if err != nil {
		return domain.PublicUser{},
			utl.NewFuncError(publicUserFromQueryResult, err)
	}

	return publicUser, nil
}
