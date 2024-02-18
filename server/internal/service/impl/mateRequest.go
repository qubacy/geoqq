package impl

import (
	"context"
	domainStorage "geoqq/internal/storage/domain"
	ec "geoqq/pkg/errorForClient/impl"
	utl "geoqq/pkg/utility"
)

type MateRequestService struct {
	domainStorage domainStorage.Storage
}

func newMateRequestService(deps Dependencies) *MateRequestService {
	instance := &MateRequestService{
		domainStorage: deps.DomainStorage,
	}

	return instance
}

// public
// -----------------------------------------------------------------------

func (mrs *MateRequestService) AddMateRequest(ctx context.Context,
	sourceUserId, targetUserId uint64) error {
	exists, err := mrs.domainStorage.HasUserWithId(ctx, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.AddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !exists {
		return utl.NewFuncError(mrs.AddMateRequest,
			ec.New(ErrUserNotFound, ec.Client, ec.UserNotFound))
	}

	// ***

	exists, err = mrs.domainStorage.HasWaitingMateRequest(
		ctx, sourceUserId, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.AddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if exists {
		return utl.NewFuncError(mrs.AddMateRequest,
			ec.New(ErrMateRequestAlreadySent, ec.Client, ec.MateRequestAlreadySent))
	}

	// ***

	_, err = mrs.domainStorage.AddMateRequest(ctx,
		sourceUserId, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.AddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return nil
}
