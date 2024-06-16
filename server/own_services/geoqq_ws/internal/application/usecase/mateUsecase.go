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

type MateUcParams struct {
	OnlineUsersUc OnlineUsersUsecase
	Database      database.Database
}

// -----------------------------------------------------------------------

type MateUsecase struct {
	onlineUsersUc    OnlineUsersUsecase
	db               database.Database
	fbChsForMateMsgs []chan input.UserIdWithMateMsg
}

func NewMateUsecase(deps MateUcParams) *MateUsecase {
	fbChsForMateMsgs := []chan input.UserIdWithMateMsg{
		make(chan input.UserIdWithMateMsg, 10),
	}

	return &MateUsecase{
		onlineUsersUc:    deps.OnlineUsersUc,
		db:               deps.Database,
		fbChsForMateMsgs: fbChsForMateMsgs,
	}
}

// public
// -----------------------------------------------------------------------

func (m *MateUsecase) AddMateMessage(ctx context.Context,
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

	mateMessage, err := m.db.GetMateMessageById(ctx, mateMessageId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	m.sendMateMessageToFb(interlocutorId, mateMessage)
	return nil
}

func (m *MateUsecase) GetFbChansForMateMessages() []<-chan input.UserIdWithMateMsg {
	chs := []<-chan input.UserIdWithMateMsg{}
	for i := range m.fbChsForMateMsgs {
		chs = append(chs, m.fbChsForMateMsgs[i])
	}

	return chs
}

// private
// -----------------------------------------------------------------------

func (m *MateUsecase) sendMateMessageToFb(userId uint64, mateMessage *domain.MateMessage) {
	count := len(m.fbChsForMateMsgs)
	index := rand.Intn(count)

	m.fbChsForMateMsgs[index] <- input.UserIdWithMateMsg{
		UserId:  userId,
		MateMsg: *mateMessage,
	}
}
