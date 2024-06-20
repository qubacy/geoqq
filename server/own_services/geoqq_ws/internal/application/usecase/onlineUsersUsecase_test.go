package usecase

import (
	"reflect"
	"testing"
)

func Test_OnlineUsersUsecase(t *testing.T) {
	uc := NewOnlineUsersUsecase()

	uc.SetUsersToOnline(1, 2, 3)
	uc.RemoveUsersFromOnline(1)
	uc.RemoveUsersFromOnline(5) // no error!
	uc.RemoveUsersFromOnline(9)

	wantUserIds := []uint64{2, 3}
	gotUserIds := uc.GetOnlineUserIds()

	if !reflect.DeepEqual(gotUserIds, wantUserIds) {
		t.Errorf("got: %v, want: %v",
			gotUserIds, wantUserIds)
	}

	// ***

	if !uc.UserIsOnline(2) {
		t.Errorf("got: %v, want: %v",
			gotUserIds, wantUserIds)
	}
}
