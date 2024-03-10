package postgre

import (
	"context"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4/pgxpool"
)

type GeoChatMessageStorage struct {
	pool *pgxpool.Pool
}

func newGeoChatMessageStorage(pool *pgxpool.Pool) *GeoChatMessageStorage {
	return &GeoChatMessageStorage{
		pool: pool,
	}
}

// templates
// -----------------------------------------------------------------------

var (
	templateInsertGeoChatMessage = utl.RemoveAdjacentWs(`
		INSERT INTO "GeoMessage" (
			"FromUserId", "Text", "Time",
			"Latitude", "Longitude"
		)
		VALUES ($1, $2, NOW()::timestamp, $3, $4) 
			RETURNING "Id"`)
)

// public
// -----------------------------------------------------------------------

func (s *MateChatMessageStorage) InsertGeoChatMessage(ctx context.Context,
	fromUserId uint64, text string, latitude, longitude float64) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertGeoChatMessage, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateInsertGeoChatMessage,
		fromUserId, text, latitude, longitude)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertGeoChatMessage, err)
	}

	return lastInsertedId, nil
}
