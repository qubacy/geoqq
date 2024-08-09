package postgre

import (
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"
	"fmt"
	"geoqq_http/internal/domain"
	storage "geoqq_http/internal/storage/domain"

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
	templateSelectPublicUsers = template.SelectPublicUsers
)

// public
// -----------------------------------------------------------------------

func (s *PublicUserStorage) GetPublicUserById(ctx context.Context,
	userId, targetUserId uint64) (*domain.PublicUser, error) {
	return s.GetTransformedPublicUserById(ctx, userId, targetUserId, nil)
}

func (s *PublicUserStorage) GetPublicUsersByIds(ctx context.Context,
	userId uint64, targetUserIds []uint64) (domain.PublicUserList, error) {
	return s.GetTransformedPublicUsersByIds(ctx, userId, targetUserIds, nil)
}

// -----------------------------------------------------------------------

func (s *PublicUserStorage) GetTransformedPublicUserById(ctx context.Context,
	userId uint64, targetUserId uint64, transform storage.PublicUserTransform) (
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

	if transform != nil {
		transform(publicUser)
	}
	return publicUser, nil
}

func (s *PublicUserStorage) GetTransformedPublicUsersByIds(ctx context.Context,
	userId uint64, targetUserIds []uint64, transform storage.PublicUserTransform) (
	domain.PublicUserList, error,
) {
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

		if transform != nil {
			transform(publicUser)
		}
		publicUsers = append(publicUsers, publicUser)
	}

	return publicUsers, nil
}

// convert
// -----------------------------------------------------------------------

func publicUserFromQueryResult(queryResult QueryResultScanner) (
	*domain.PublicUser, error,
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
		return nil, utl.NewFuncError(publicUserFromQueryResult, err)
	}

	return &publicUser, nil
}
