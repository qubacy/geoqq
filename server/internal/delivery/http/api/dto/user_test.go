package dto

import (
	"encoding/json"
	"fmt"
	"testing"
)

func Test_MyProfileRes_Marshal(t *testing.T) {
	resDto := MyProfileRes{
		Username:    "Test",
		Description: "Test",
		AvatarId:    123,
		Privacy: Privacy{
			HitMeUp: 1,
		},
	}
	bytes, err := json.Marshal(resDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))
}

func Test_MyProfilePutReq_Marshal(t *testing.T) {
	reqDto := MyProfilePutReq{
		AccessToken: "Access",
	}
	bytes, err := json.Marshal(reqDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))
}

func Test_MyProfilePutReq_Unmarshal(t *testing.T) {
	reqDto := MyProfilePutReq{}
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
}
