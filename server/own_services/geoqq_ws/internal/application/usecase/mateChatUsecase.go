package usecase

import (
	domain "common/pkg/domain/geoqq"
	ec "common/pkg/errorForClient/geoqq"
	utl "common/pkg/utility"
	"context"
	"geoqq_ws/internal/application/ports/input"
	"geoqq_ws/internal/application/ports/output/database"
	"math/rand"
)

type MateChatUsecase struct {
	onlineUsersUc          input.OnlineUsersUsecase
	feedbackChsForMateChat []chan input.UserIdWithMateChat

	db database.Database
}

func NewMateChatUsecase(
	onlineUsersUc input.OnlineUsersUsecase,
	db database.Database) *MateChatUsecase {

	return &MateChatUsecase{
		onlineUsersUc: onlineUsersUc,
		db:            db,
	}
}

// public
// -----------------------------------------------------------------------

func (m *MateChatUsecase) InformAboutMateChatAdded(ctx context.Context,
	targetUserId, mateChatId uint64) error {

	if err := m.informAboutEventWithMateChat(ctx,
		input.EventAdded, targetUserId, mateChatId); err != nil {
		return utl.NewFuncError(m.InformAboutMateChatAdded, err)
	}

	return nil
}

func (m *MateChatUsecase) InformAboutMateChatUpdated(ctx context.Context,
	targetUserId, mateChatId uint64) error {

	if err := m.informAboutEventWithMateChat(ctx,
		input.EventUpdated, targetUserId, mateChatId); err != nil {
		return utl.NewFuncError(m.InformAboutMateChatUpdated, err)
	}

	return nil
}

func (m *MateChatUsecase) GetFbChansForMateChat() []<-chan input.UserIdWithMateChat {
	return utl.ChanToLeftDirected(m.feedbackChsForMateChat)
}

// private
// -----------------------------------------------------------------------

func (m *MateChatUsecase) informAboutEventWithMateChat(ctx context.Context,
	event input.Event, targetUserId, mateChatId uint64) error {
	sourceFunc := m.informAboutEventWithMateChat

	mateChat, err := m.db.GetMateChatWithIdForUser(ctx, targetUserId, mateChatId)
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}

	ue := input.MakeUserIdWithEvent(targetUserId, event)
	m.sendMateChatToFb(ue, mateChat)

	return nil
}

func (m *MateChatUsecase) sendMateChatToFb(
	ue input.UserIdWithEvent, mateChat *domain.MateChat) {

	index := rand.Intn(len(m.feedbackChsForMateChat))
	checkOnlineUserAndDoAction(m.onlineUsersUc, ue.GetUserId(),
		func() {
			m.feedbackChsForMateChat[index] <- input.UserIdWithMateChat{
				UserIdWithEvent: ue, MateChat: mateChat}
		})
}
