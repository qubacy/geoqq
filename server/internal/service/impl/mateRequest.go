package impl

import (
	"context"
	"geoqq/internal/domain/table"
	ec "geoqq/internal/pkg/errorForClient/impl"
	"geoqq/internal/service/dto"
	domainStorage "geoqq/internal/storage/domain"
	"geoqq/pkg/logger"
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

	mrs.domainStorage.UpdateBgrLastActivityTimeForUser(userId)
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

	mrs.domainStorage.UpdateBgrLastActivityTimeForUser(userId)
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
		return ec.New(ErrMateRequestToSelf,
			ec.Client, ec.MateRequestToSelf)
	}

	// asserts

	err := assertUserWithIdExists(ctx,
		mrs.domainStorage, targetUserId,
		ec.TargetUserNotFound,
	)
	if err != nil {
		return utl.NewFuncError(mrs.AddMateRequest, err)
	}
	err = assertUserWithIdNotDeleted(ctx, mrs.domainStorage, targetUserId)
	if err != nil {
		return utl.NewFuncError(mrs.AddMateRequest, err)
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
		return ec.New(utl.NewFuncError(mrs.AddMateRequest, err),
			ec.Server, ec.DomainStorageError)
	}

	return nil
}

func (mrs *MateRequestService) SetResultForMateRequest(ctx context.Context,
	userId, mateRequestId uint64, mateRequestResult table.MateRequestResult) error {

	if !mateRequestResult.IsAcceptedOrRejected() {
		return ec.New(ErrUnknownMateRequestResult,
			ec.Client, ec.UnknownMateRequestResult)
	}

	// read from database

	exists, err := mrs.domainStorage.IsMateRequestForUser(ctx, mateRequestId, userId)
	if err != nil {
		return ec.New(utl.NewFuncError(mrs.SetResultForMateRequest, err),
			ec.Server, ec.DomainStorageError)
	}
	if !exists {
		return ec.New(ErrMateRequestNotFound,
			ec.Client, ec.MateRequestNotFound)
	}

	// ***

	mateRequest, err := mrs.domainStorage.GetMateRequestById(ctx, mateRequestId)
	if err != nil {
		return ec.New(utl.NewFuncError(mrs.SetResultForMateRequest, err),
			ec.Server, ec.DomainStorageError)
	}
	if mateRequest.Result != table.Waiting { // incoming mate request for user?
		return ec.New(ErrMateRequestNotWaiting,
			ec.Client, ec.MateRequestNotWaiting)
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
		logger.Error("%v", err) // with source information!

		return ec.New(utl.NewFuncError(mrs.SetResultForMateRequest, err),
			ec.Server, ec.DomainStorageError)
	}
	return nil
}

// private
// -----------------------------------------------------------------------

// TODO: changed error return style!
func (mrs *MateRequestService) partialValidateInputBeforeAddMateRequest(ctx context.Context,
	sourceUserId, targetUserId uint64) error {
	/*
		Check List:
			1. Target user may be deleted.
			2. They might already be mates.
			3. The request may already be sent.
			4. There is already an incoming request.
	*/

	// target user deleted

	sourceFunc := mrs.partialValidateInputBeforeAddMateRequest
	wasDeleted, err := mrs.domainStorage.WasUserDeleted(ctx, targetUserId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	if wasDeleted {
		return ec.New(ErrTargetUserDeleted,
			ec.Client, ec.TargetUserDeleted) // TODO: user wrapper?
	}

	// are mates

	areMates, err := mrs.domainStorage.AreMates(ctx, sourceUserId, targetUserId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	if areMates {
		return ec.New(ErrAlreadyAreMates,
			ec.Client, ec.AlreadyAreMates)
	}

	// sent from you

	exists, err := mrs.domainStorage.HasWaitingMateRequest(
		ctx, sourceUserId, targetUserId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	if exists {
		return ec.New(ErrMateRequestAlreadySentFromYou,
			ec.Client, ec.MateRequestAlreadySentFromYou)
	}

	// sent to you

	exists, err = mrs.domainStorage.HasWaitingMateRequest(
		ctx, targetUserId, sourceUserId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	if exists {
		return ec.New(ErrMateRequestAlreadySentToYou,
			ec.Client, ec.MateRequestAlreadySentToYou)
	}

	return nil
}

func assertUserWithIdNotDeleted(ctx context.Context,
	storage domainStorage.Storage, id uint64) error {

	wasDeleted, err := storage.WasUserDeleted(ctx, id)
	if err != nil {
		return ec.New(utl.NewFuncError(assertUserWithIdNotDeleted, err),
			ec.Server, ec.DomainStorageError)
	}

	if wasDeleted {
		return ec.New(ErrUserWithNameHasBeenDeleted,
			ec.Client, ec.UserWasPreviouslyDeleted)
	}

	return nil
}
