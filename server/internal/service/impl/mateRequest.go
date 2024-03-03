package impl

import (
	"context"
	"geoqq/internal/domain/table"
	"geoqq/internal/service/dto"
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

func (mrs *MateRequestService) GetAllIncomingMateRequestsForUser(ctx context.Context, userId uint64) (
	*dto.MateRequestsForUserOut, error) {
	mateRequests, err := mrs.domainStorage.GetAllWaitingMateRequestsForUser(
		ctx, userId)
	if err != nil {
		return nil, utl.NewFuncError(mrs.GetAllIncomingMateRequestsForUser,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	// ***

	return dto.NewMateRequestsForUserOutFromDomain(
		mateRequests, userId), nil
}

func (mrs *MateRequestService) GetIncomingMateRequestsForUser(ctx context.Context, userId, offset, count uint64) (
	*dto.MateRequestsForUserOut, error) {
	mateRequests, err := mrs.domainStorage.GetWaitingMateRequestsForUser(
		ctx, userId, offset, count)
	if err != nil {
		return nil, utl.NewFuncError(mrs.GetAllIncomingMateRequestsForUser,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	// ***

	return dto.NewMateRequestsForUserOutFromDomain(
		mateRequests, userId), nil
}

func (mrs *MateRequestService) GetIncomingMateRequestCountForUser(
	ctx context.Context, userId uint64) (int, error) {
	count, err := mrs.domainStorage.GetWaitingMateRequestCountForUser(ctx, userId)
	if err != nil {
		return 0, utl.NewFuncError(mrs.GetIncomingMateRequestCountForUser,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}

	return count, nil
}

func (mrs *MateRequestService) AddMateRequest(ctx context.Context,
	sourceUserId, targetUserId uint64) error {

	if sourceUserId == targetUserId {
		return utl.NewFuncError(mrs.AddMateRequest,
			ec.New(ErrMateRequestToSelf, ec.Client, ec.MateRequestToSelf))
	}

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

	err = mrs.partialValidateInputBeforeAddMateRequest(ctx,
		sourceUserId, targetUserId)
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

	// read from database

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

	mateRequest, err := mrs.domainStorage.GetMateRequestById(ctx, mateRequestId)
	if err != nil {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if mateRequest.Result != table.Waiting { // incoming mate request for user?
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(ErrMateRequestNotWaiting, ec.Client, ec.MateRequestNotWaiting))
	}

	// write to database

	if mateRequestResult.IsAccepted() {
		err = mrs.domainStorage.AcceptMateRequestById(ctx, mateRequestId,
			mateRequest.FromUserId, mateRequest.ToUserId)
	} else {
		err = mrs.domainStorage.RejectMateRequestById(ctx, mateRequestId,
			mateRequest.FromUserId, mateRequest.ToUserId)
	}

	if err != nil {
		return utl.NewFuncError(mrs.SetResultForMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	return nil
}

// private
// -----------------------------------------------------------------------

func (mrs *MateRequestService) partialValidateInputBeforeAddMateRequest(ctx context.Context,
	sourceUserId, targetUserId uint64) error {

	/*
		1. They might already be mates.
		2. The request may already be sent.
		3. There is already an incoming request.
	*/

	// are mates

	areMates, err := mrs.domainStorage.AreMates(ctx, sourceUserId, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.partialValidateInputBeforeAddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if areMates {
		return utl.NewFuncError(mrs.partialValidateInputBeforeAddMateRequest,
			ec.New(ErrAlreadyAreMates, ec.Client, ec.AlreadyAreMates))
	}

	// sent from you

	exists, err := mrs.domainStorage.HasWaitingMateRequest(
		ctx, sourceUserId, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.partialValidateInputBeforeAddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if exists {
		return utl.NewFuncError(mrs.partialValidateInputBeforeAddMateRequest,
			ec.New(ErrMateRequestAlreadySentFromYou,
				ec.Client, ec.MateRequestAlreadySentFromYou))
	}

	// sent to you

	exists, err = mrs.domainStorage.HasWaitingMateRequest(
		ctx, targetUserId, sourceUserId)
	if err != nil {
		return utl.NewFuncError(mrs.partialValidateInputBeforeAddMateRequest,
			ec.New(err, ec.Server, ec.DomainStorageError))
	}
	if exists {
		return utl.NewFuncError(mrs.partialValidateInputBeforeAddMateRequest,
			ec.New(ErrMateRequestAlreadySentToYou,
				ec.Client, ec.MateRequestAlreadySentToYou))
	}

	return nil
}
