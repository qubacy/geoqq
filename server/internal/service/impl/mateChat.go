package impl

import (
	"context"
	"geoqq/internal/domain"
	domainStorage "geoqq/internal/storage/domain"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

type MateChatService struct {
	domainStorage domainStorage.Storage
}

func newMateChatService(deps Dependencies) *MateChatService {
	instance := &MateChatService{
		domainStorage: deps.DomainStorage,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (s *MateChatService) GetMateChatsForUser(ctx context.Context,
	userId, offset, count uint64) (domain.MateChatList, error) {
	mateChats, err := s.domainStorage.GetMateChatsForUser(ctx, userId, offset, count)
	if err != nil {
		return nil, utl.NewFuncError(s.GetMateChatsForUser,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return mateChats, nil // same types!
}
