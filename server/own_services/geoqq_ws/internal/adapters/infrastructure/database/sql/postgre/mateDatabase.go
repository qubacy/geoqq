package postgre

import (
	"common/pkg/postgreUtils/wrappedPgxpool"
	"common/pkg/storage/geoqq/sql/postgre/template"
	utl "common/pkg/utility"
	"context"

	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

type MateDatabase struct {
	pool *pgxpool.Pool
}

func newMateDatabase(pool *pgxpool.Pool) *MateDatabase {
	return &MateDatabase{
		pool: pool,
	}
}

// public
// -----------------------------------------------------------------------

func (m *MateDatabase) InsertMateMessage(ctx context.Context, chatId uint64,
	fromUserId uint64, text string) (uint64, error) {
	sourceFunc := m.InsertMateMessage

	row, err := wrappedPgxpool.QueryRowWithConnectionAcquire(m.pool, ctx,
		func(conn *pgxpool.Conn, ctx context.Context) pgx.Row {
			return conn.QueryRow(ctx, template.InsertMateChatMessage+`;`,
				chatId, fromUserId, text)
		})

	var gmId uint64
	err = utl.RunFuncsRetErr(
		func() error { return err },
		func() error {
			gmId, err = wrappedPgxpool.ScanUint64(row, sourceFunc)
			return err // nil eq ok!
		})
	if err != nil {
		return 0, utl.NewFuncError(sourceFunc, err)
	}

	return gmId, nil
}
