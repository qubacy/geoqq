package impl

import (
	"context"
	"geoqq/internal/domain/table"
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

	// TODO: to func
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

func (mrs *MateRequestService) SetResultForMateRequest(ctx context.Context,
	userId, mateRequestId uint64, value table.MateRequestResult) error {

	exists, err := mrs.domainStorage.HasMateRequestByIdAndToUser(ctx, mateRequestId, userId)
	if err != nil {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if !exists {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(ErrMateRequestNotFound, ec.Client, ec.MateRequestNotFound))
	}

	// ***

	result, err := mrs.domainStorage.GetMateRequestResultById(ctx, mateRequestId)
	if err != nil {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if result != table.Waiting {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(ErrMateRequestNotWaiting, ec.Client, ec.MateRequestNotWaiting))
	}

	// ***

	err = mrs.domainStorage.UpdateMateRequestResultById(ctx, mateRequestId, value)
	if err != nil {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return nil
}
