package usecase

import (
	utl "common/pkg/utility"
	"log"
	"reflect"
	"testing"
)

func Test_OnlineUsersUsecase(t *testing.T) {
	uc := NewOnlineUsersUsecase(&OnlineUsersParams{
		TempDatabase: nil,
	})

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

func Test_ExcludeOfflineUsersFromLis(t *testing.T) {
	uc := NewOnlineUsersUsecase(&OnlineUsersParams{
		TempDatabase: nil,
	})

	uc.SetUsersToOnline(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
	userIds := []uint64{101, 102, 1, 103, 5, 6}

	gotOnlineUserIds := uc.ExcludeOfflineUsersFromList(userIds...)
	log.Printf("online user ids: %v", gotOnlineUserIds)

	wantOnlineUserIds := []uint64{1, 5, 6}
	if utl.EqualUnsortedSlices(gotOnlineUserIds, wantOnlineUserIds) {
		t.Errorf("got: %v, want: %v",
			gotOnlineUserIds, wantOnlineUserIds)
	}
}
