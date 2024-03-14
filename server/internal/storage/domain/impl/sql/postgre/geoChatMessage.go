package postgre

import (
	"context"
	"errors"
	"geoqq/internal/domain"
	utl "geoqq/pkg/utility"

	"github.com/jackc/pgx/v4"
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

	templateGetGeoChatMessages = utl.RemoveAdjacentWs(`
		SELECT 
			"Id",
			"Text",
			"Time",
			"FromUserId" AS "UserId"
		FROM "GeoMessage"
		WHERE geodistance(
			"Latitude", "Longitude",
			$1, $2) < $3
		ORDER BY "Time" DESC`)

	templateGetGeoChatMessagesWithLimitAndOffset = templateGetGeoChatMessages +
		` LIMIT $4 OFFSET $5`
)

// public
// -----------------------------------------------------------------------

func (s *GeoChatMessageStorage) InsertGeoChatMessage(ctx context.Context,
	fromUserId uint64, text string, latitude, longitude float64) (uint64, error) {
	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertGeoChatMessage, err)
	}
	defer conn.Release()

	// ***

	row := conn.QueryRow(ctx, templateInsertGeoChatMessage+`;`,
		fromUserId, text, latitude, longitude)

	var lastInsertedId uint64
	err = row.Scan(&lastInsertedId)
	if err != nil {
		return 0, utl.NewFuncError(s.InsertGeoChatMessage, err)
	}

	return lastInsertedId, nil
}

func (s *GeoChatMessageStorage) InsertGeoChatMessageWithUpdateUserLocation(ctx context.Context,
	fromUserId uint64, text string,
	latitude, longitude float64) (uint64, error) {

	conn, tx, err := begunTransaction(s.pool, ctx)
	if err != nil {
		return 0, utl.NewFuncError(
			s.InsertGeoChatMessageWithUpdateUserLocation, err)
	}
	defer conn.Release()

	// ***

	lastInsertedId, err := insertGeoChatMessage(ctx, tx,
		fromUserId, text, latitude, longitude)

	err = errors.Join(
		err,
		updateUserLocation(ctx, tx, fromUserId, longitude, latitude),
	)
	if err != nil {
		tx.Rollback(ctx) // <--- ignore error!
		return 0, utl.NewFuncError(
			s.InsertGeoChatMessageWithUpdateUserLocation, err)
	}

	// ***

	err = tx.Commit(ctx)
	if err != nil {
		return 0, utl.NewFuncError(
			s.InsertGeoChatMessageWithUpdateUserLocation, err)
	}
	return lastInsertedId, nil
}

// -----------------------------------------------------------------------

func (s *GeoChatMessageStorage) GetGeoChatAllMessages(ctx context.Context,
	distance uint64,
	latitude, longitude float64) (domain.GeoMessageList, error) {

	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatAllMessages, err)
	}
	defer conn.Release()

	rows, err := conn.Query(ctx,
		templateGetGeoChatMessages+";",
		latitude, longitude, distance, // 1, 2, 3
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatAllMessages, err)
	}
	defer rows.Close()

	geoMessages, err := rowsToGeoMessages(rows)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatAllMessages, err)
	}

	return geoMessages, nil
}

func (s *GeoChatMessageStorage) GetGeoChatMessages(ctx context.Context,
	distance uint64,
	latitude, longitude float64,
	offset, count uint64) (domain.GeoMessageList, error) {

	conn, err := s.pool.Acquire(ctx)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatMessages, err)
	}
	defer conn.Release()

	rows, err := conn.Query(ctx,
		templateGetGeoChatMessagesWithLimitAndOffset+";",
		latitude, longitude, distance,
		count, offset, // 4, 5
	)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatMessages, err)
	}
	defer rows.Close()

	geoMessages, err := rowsToGeoMessages(rows)
	if err != nil {
		return nil, utl.NewFuncError(s.GetGeoChatMessages, err)
	}

	return geoMessages, nil
}

// private
// -----------------------------------------------------------------------

func rowsToGeoMessages(rows pgx.Rows) (domain.GeoMessageList, error) {
	geoMessages := domain.GeoMessageList{}
	for rows.Next() {
		geoMessage, err := rowsToGeoMessage(rows)
		if err != nil {
			return nil, utl.NewFuncError(rowsToGeoMessages, err)
		}

		geoMessages = append(geoMessages, geoMessage)
	}

	return geoMessages, nil
}

func rowsToGeoMessage(rows pgx.Rows) (*domain.GeoMessage, error) {
	geoMessage := &domain.GeoMessage{}
	err := rows.Scan(
		&geoMessage.Id,
		&geoMessage.Text,
		&geoMessage.Time,
		&geoMessage.UserId,
	)
	if err != nil {
		return nil, utl.NewFuncError(rowsToGeoMessage, err)
	}

	return geoMessage, nil
}
