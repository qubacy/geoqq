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

	err = mrs.validateInputBeforeAddMateRequest(ctx, sourceUserId, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.AddMateRequest, err)
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
	userId, mateRequestId uint64, mateRequestResult table.MateRequestResult) error {

	if !mateRequestResult.IsAcceptedOrRejected() {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(ErrUnknownMateRequestResult, ec.Client, ec.UnknownMateRequestResult))
	}

	// ***

	exists, err := mrs.domainStorage.IsMateRequestForUser(ctx, mateRequestId, userId)
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
	if result != table.Waiting { // incoming mate request for user?
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(ErrMateRequestNotWaiting, ec.Client, ec.MateRequestNotWaiting))
	}

	// ***

	if mateRequestResult.IsAccepted() {

	}

	// ***

	err = mrs.domainStorage.UpdateMateRequestResultById(ctx, mateRequestId, mateRequestResult)
	if err != nil {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return nil
}

// private
// -----------------------------------------------------------------------

func (mrs *MateRequestService) validateInputBeforeAddMateRequest(ctx context.Context,
	sourceUserId, targetUserId uint64) error {

	/*
		1. They might already be mates.
		2. The request may already be sent.
		3. There is already an incoming request.
	*/

	// are mates

	areMates, err := mrs.domainStorage.AreMates(ctx, sourceUserId, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.validateInputBeforeAddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if areMates {
		return utl.NewFuncError(mrs.validateInputBeforeAddMateRequest,
			ec.New(ErrAlreadyAreMates, ec.Client, ec.AlreadyAreMates))
	}

	// sent from you

	exists, err := mrs.domainStorage.HasWaitingMateRequest(
		ctx, sourceUserId, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.validateInputBeforeAddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if exists {
		return utl.NewFuncError(mrs.validateInputBeforeAddMateRequest,
			ec.New(ErrMateRequestAlreadySentFromYou,
				ec.Client, ec.MateRequestAlreadySentFromYou))
	}

	// sent to you

	exists, err = mrs.domainStorage.HasWaitingMateRequest(
		ctx, targetUserId, sourceUserId)
	if err != nil {
		return utl.NewFuncError(mrs.validateInputBeforeAddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if exists {
		return utl.NewFuncError(mrs.validateInputBeforeAddMateRequest,
			ec.New(ErrMateRequestAlreadySentToYou,
				ec.Client, ec.MateRequestAlreadySentToYou))
	}

	return nil
}
