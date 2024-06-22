package usecase

import (
	ec "common/pkg/errorForClient/geoqq"
	utl "common/pkg/utility"
	"context"
	dd "geoqq_ws/internal/application/domain"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/database"
	"math/rand"
)

type MateMessageUcParams struct {
	OnlineUsersUc input.OnlineUsersUsecase
	Database      database.Database

	FbChanCount int
	FbChanSize  int
}

// -----------------------------------------------------------------------

type MateMessageUsecase struct {
	onlineUsersUc          input.OnlineUsersUsecase
	feedbackChsForMateMsgs []chan input.UserIdWithMateMessage

	db database.Database
}

func NewMateMessageUsecase(params *MateMessageUcParams) *MateMessageUsecase {
	feedbackChsForMateMsgs := []chan input.UserIdWithMateMessage{}
	for i := 0; i < params.FbChanCount; i++ {
		feedbackChsForMateMsgs = append(feedbackChsForMateMsgs,
			make(chan input.UserIdWithMateMessage, params.FbChanSize))
	}

	// ***

	return &MateMessageUsecase{
		onlineUsersUc:          params.OnlineUsersUc,
		feedbackChsForMateMsgs: feedbackChsForMateMsgs,
		db:                     params.Database, // !
	}
}

// public
// -----------------------------------------------------------------------

func (m *MateMessageUsecase) AddMateMessage(ctx context.Context,
	userId, chatId uint64, text string) error {
	sourceFunc := m.AddMateMessage

	var (
		err            error
		mateMessageId  uint64
		interlocutorId uint64
		mateMessage    *dd.MateMessage
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
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	m.sendMateMessageToFb(interlocutorId, mateMessage) // no error!
	return nil
}

func (m *MateMessageUsecase) GetFbChansForMateMessages() []<-chan input.UserIdWithMateMessage {
	chs := []<-chan input.UserIdWithMateMessage{}
	for i := range m.feedbackChsForMateMsgs {
		chs = append(chs, m.feedbackChsForMateMsgs[i]) // convert chans!
	}

	return chs
}

// private
// -----------------------------------------------------------------------

func (m *MateMessageUsecase) sendMateMessageToFb(targetUserId uint64, mateMessage *dd.MateMessage) {
	if !m.onlineUsersUc.UserIsOnline(targetUserId) {
		return
	}

	count := len(m.feedbackChsForMateMsgs)
	index := rand.Intn(count)

	m.feedbackChsForMateMsgs[index] <- input.UserIdWithMateMessage{
		UserId:  targetUserId, // forward message to...
		MateMsg: mateMessage,
	}
}
