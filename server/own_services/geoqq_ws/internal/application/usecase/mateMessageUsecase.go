package usecase

import (
	ec "common/pkg/errorForClient/geoqq"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/domain"
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
	onlineUsersUc    input.OnlineUsersUsecase
	fbChsForMateMsgs []chan input.UserIdWithMateMessage

	db database.Database
}

func NewMateMessageUsecase(deps MateMessageUcParams) *MateMessageUsecase {
	fbChsForMateMsgs := []chan input.UserIdWithMateMessage{}
	for i := 0; i < deps.FbChanCount; i++ {
		fbChsForMateMsgs = append(fbChsForMateMsgs,
			make(chan input.UserIdWithMateMessage, deps.FbChanSize))
	}

	// ***

	return &MateMessageUsecase{
		onlineUsersUc:    deps.OnlineUsersUc,
		fbChsForMateMsgs: fbChsForMateMsgs,
		db:               deps.Database,
	}
}

// public
// -----------------------------------------------------------------------

func (m *MateMessageUsecase) AddMateMessage(ctx context.Context,
	userId, chatId uint64, text string) error {
	sourceFunc := m.AddMateMessage

	var err error
	var mateMessageId uint64
	var interlocutorId uint64

	err = utl.RunFuncsRetErr(
		func() error {
			mateMessageId, err = m.db.InsertMateMessage(ctx, chatId, userId, text)
			return err
		}, func() error {
			interlocutorId, err = m.db.GetMateIdByChatId(ctx, userId, chatId)
			return err
		})
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	// ***

	mateMessage, err := m.db.GetMateMessageById(ctx, mateMessageId) // just added!
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	m.sendMateMessageToFb(interlocutorId, mateMessage)
	return nil
}

func (m *MateMessageUsecase) GetFbChansForMateMessages() []<-chan input.UserIdWithMateMessage {
	chs := []<-chan input.UserIdWithMateMessage{}
	for i := range m.fbChsForMateMsgs {
		chs = append(chs, m.fbChsForMateMsgs[i]) // convert chans!
	}

	return chs
}

// private
// -----------------------------------------------------------------------

func (m *MateMessageUsecase) sendMateMessageToFb(userId uint64, mateMessage *domain.MateMessage) {

	count := len(m.fbChsForMateMsgs)
	index := rand.Intn(count)

	m.fbChsForMateMsgs[index] <- input.UserIdWithMateMessage{
		UserId:  userId, // forward message to...
		MateMsg: *mateMessage,
	}
}
