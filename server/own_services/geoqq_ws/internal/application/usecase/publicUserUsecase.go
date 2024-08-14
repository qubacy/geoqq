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

type PublicUserUsecase struct {
	onlineUsersUc         input.OnlineUsersUsecase
	feedbackChsForPubUser []chan input.UserIdWithPublicUser

	database database.Database
}

func NewPublicUserUsecase(
	onlineUsersUc input.OnlineUsersUsecase,
	db database.Database,
	fbChanCount, fbChanSize int,
) *PublicUserUsecase {

	feedbackChs := []chan input.UserIdWithPublicUser{}
	for i := 0; i < fbChanCount; i++ {
		feedbackChs = append(feedbackChs,
			make(chan input.UserIdWithPublicUser, fbChanSize))
	}

	// ***

	return &PublicUserUsecase{
		onlineUsersUc:         onlineUsersUc,
		feedbackChsForPubUser: feedbackChs,
		database:              db,
	}
}

// -----------------------------------------------------------------------

/*
userId - user who updated their account!
*/
func (p *PublicUserUsecase) InformAboutPublicUserUpdated(ctx context.Context, userId uint64) error {
	sourceFunc := p.InformAboutPublicUserUpdated
	mateIds, err := p.database.GetMateIdsForUser(ctx, userId) // !
	if err != nil {
		return ec.New(utl.NewFuncError(sourceFunc, err),
			ec.Server, ec.DomainStorageError)
	}
	onlineMateIds := p.onlineUsersUc.ExcludeOfflineUsersFromList(mateIds...)

	// ***

	// TODO: following code requires optimization!

	publicUsers := make([]*domain.PublicUser, 0, len(onlineMateIds))
	for _, mateId := range onlineMateIds {
		publicUser, err := p.database.GetPublicUserById(ctx, userId, mateId)
		if err != nil {
			return ec.New(utl.NewFuncError(sourceFunc, err),
				ec.Server, ec.DomainStorageError)
		}

		publicUsers = append(publicUsers, publicUser)
	}

	for i := range onlineMateIds {
		ue := input.MakeUserIdWithEvent(onlineMateIds[i], input.EventUpdated)
		p.sendPublicUserToFbWithoutOnlineCheck(ue, publicUsers[i])
	}

	return nil
}

func (p *PublicUserUsecase) GetFbChansForPublicUser() []<-chan input.UserIdWithPublicUser {
	return utl.ChanToLeftDirected(p.feedbackChsForPubUser)
}

// private
// -----------------------------------------------------------------------

func (p *PublicUserUsecase) sendPublicUserToFbWithoutOnlineCheck(
	ue input.UserIdWithEvent, publicUser *domain.PublicUser) {
	index := rand.Intn(len(p.feedbackChsForPubUser))
	p.feedbackChsForPubUser[index] <- input.UserIdWithPublicUser{
		UserIdWithEvent: ue, PublicUser: publicUser}
}
