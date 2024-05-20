package dto

import (
	"encoding/json"
	"fmt"
	"testing"
)

func Test_MyProfileRes_Marshal(t *testing.T) {
	resDto := MyProfileRes{
		Profile: Profile{
			Id:          123,
			Username:    "Test",
			Description: "Test",
			AvatarId:    123,
			Privacy: Privacy{
				HitMeUp: 1,
			},
		},
	}
	bytes, err := json.Marshal(resDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))
}

func Test_MyProfilePutReq_Unmarshal(t *testing.T) {
	reqDto := MyProfileWithAttachedAvatarPutReq{}
	bytes := []byte(`{
		"access-token":"Access",
		"avatar": "Test",
		"privacy":null,
		"security":null
		}`)

	err := json.Unmarshal(bytes, &reqDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(reqDto)
	fmt.Println(*reqDto.Avatar)

	// ***

	if reqDto.Avatar == nil {
		t.Error("Avatar is nil")
	}
	if reqDto.Privacy != nil {
		t.Error("Privacy is not nil")
	}
	if reqDto.Security != nil {
		t.Error("Security is not nil")
	}
	if reqDto.Description != nil {
		t.Error("Description is not nil")
	}
}

func Test_UserReq_Marshal(t *testing.T) {
	reqDto := SomeUsersReq{
		Ids: []float64{
			1, 2, 3, 5, 6, 7,
		},
	}
	bytes, err := json.Marshal(reqDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))

	if string(bytes) != `{"ids":[1,2,3,5,6,7]}` {
		t.Error()
	}
}

func Test_UserByIdRes_Marshal(t *testing.T) {
	resDto := UserByIdRes{
		User{
			Username:    "Test",
			Description: "Test",
			//...
		},
	}
	bytes, err := json.Marshal(resDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))
}
