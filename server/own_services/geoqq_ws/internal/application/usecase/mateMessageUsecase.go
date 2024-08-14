package usecase

import (
	domain "common/pkg/domain/geoqq"
	ec "common/pkg/errorForClient/geoqq"
	"common/pkg/logger"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/database"
	"geoqq_ws/internal/constErrors"
	"math/rand"
)

type MateMessageUcParams struct {
	OnlineUsersUc input.OnlineUsersUsecase
	Database      database.Database

	FbChanCount int
	FbChanSize  int

	MaxMessageLength uint64
}

// -----------------------------------------------------------------------

type MateMessageUsecase struct {
	onlineUsersUc         input.OnlineUsersUsecase
	feedbackChsForMateMsg []chan input.UserIdWithMateMessage

	db database.Database

	maxMessageLength uint64
}

func NewMateMessageUsecase(params *MateMessageUcParams) *MateMessageUsecase {
	feedbackChsForMateMsgs := []chan input.UserIdWithMateMessage{}
	for i := 0; i < params.FbChanCount; i++ {
		feedbackChsForMateMsgs = append(feedbackChsForMateMsgs,
			make(chan input.UserIdWithMateMessage, params.FbChanSize))
	}

	// ***

	return &MateMessageUsecase{
		onlineUsersUc:         params.OnlineUsersUc,
		feedbackChsForMateMsg: feedbackChsForMateMsgs,
		db:                    params.Database, // !
		maxMessageLength:      params.MaxMessageLength,
	}
}

// public
// -----------------------------------------------------------------------

func (m *MateMessageUsecase) ForwardMateMessage(ctx context.Context,
	targetUserId uint64, mm *domain.MateMessageWithChat) error {
	/*
		Mate message has already
			been added to the database.
	*/

	if mm == nil {
		return constErrors.ErrInputParamWithTypeNotSpecified(
			utl.GetTypeName(mm))
	}

	ue := input.MakeUserIdWithEvent(targetUserId, input.EventAdded)
	m.sendMateMessageToFb(ue, mm) // to target!

	// should i send a message to myself?!
	m.sendMateMessageToFb(ue.WithUserId(mm.UserId), mm)

	return nil
}

// -----------------------------------------------------------------------

func (m *MateMessageUsecase) AddMateMessage(ctx context.Context,
	userId, chatId uint64, text string) error {
	sourceFunc := m.AddMateMessage

	if len(text) > int(m.maxMessageLength) {
		return ec.New(ErrMessageTooLong(m.maxMessageLength),
			ec.Client, ec.MateMessageTooLong)
	}

	var (
		err            error
		mateMessageId  uint64
		interlocutorId uint64
		mateMessage    *domain.MateMessageWithChat
	)

	// TODO: chat available for userId

	err = utl.RunFuncsRetErr(
		func() error {
			mateMessageId, err = m.db.InsertMateMessage(ctx, chatId, userId, text)
			return err
		}, func() error {
			interlocutorId, err = m.db.GetMateIdByChatId(ctx, userId, chatId)
			return err
		}, func() error {
			mateMessage, err = m.db.GetMateMessageById(ctx, mateMessageId) // just added!
			return err
		})
	if err != nil {
		err = utl.NewFuncError(sourceFunc, err)
		logger.Error("%v")

		return ec.New(err, ec.Server, ec.DomainStorageError)
	}

	// ***

	ue := input.MakeUserIdWithEvent(userId, input.EventAdded)
	m.sendMateMessageToFb(ue, mateMessage)
	m.sendMateMessageToFb(ue.WithUserId(interlocutorId), mateMessage) // no error!

	return nil
}

// -----------------------------------------------------------------------

func (m *MateMessageUsecase) GetFbChansForMateMessages() []<-chan input.UserIdWithMateMessage {
	return utl.ChanToLeftDirected(m.feedbackChsForMateMsg)
}

// private
// -----------------------------------------------------------------------

func (m *MateMessageUsecase) sendMateMessageToFb(
	ue input.UserIdWithEvent, mateMessage *domain.MateMessageWithChat) {

	index := rand.Intn(len(m.feedbackChsForMateMsg))
	checkOnlineUserAndDoAction(m.onlineUsersUc, ue.GetUserId(),
		func() {
			m.feedbackChsForMateMsg[index] <- input.UserIdWithMateMessage{
				UserIdWithEvent: ue, // forward message to...
				MateMessage:     mateMessage,
			}
		})
}
