package usecase

import (
	"common/pkg/logger"
	"geoqq_ws/internal/application/ports/input"
)

func checkOnlineUserAndDoAction(
	onlineUsersUc input.OnlineUsersUsecase,
	targetUserId uint64, action func()) {

	if !onlineUsersUc.UserIsOnline(targetUserId) {
		logger.Debug("user with id `%v` is offline", targetUserId)
		return
	}

	// ***

	action() // no return, no input!
}
