package postgre

import (
	"common/pkg/domain/geoqq"
	"common/pkg/postgreUtils/wrappedPgxpool"
	utl "common/pkg/utility"
)

func MateMessageWithChatIdFromQueryResult(scanner wrappedPgxpool.QueryResultScanner) (
	*geoqq.MateMessageWithChat, error) {
	sourceFunc := MateMessageWithChatIdFromQueryResult

	mm := geoqq.MateMessageWithChat{}
	err := scanner.Scan(
		&mm.Id, &mm.ChatId,
		&mm.UserId, &mm.Text,
		&mm.Time, &mm.Read)
	if err != nil {
		return nil, utl.NewFuncError(sourceFunc, err)
	}

	return &mm, nil // ok!
}
