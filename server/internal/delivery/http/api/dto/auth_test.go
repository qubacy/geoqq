package dto

import (
	"encoding/json"
	"fmt"
	"testing"
)

func Test_SignInPostReq_Marshal(t *testing.T) {
	reqDto := SignInPostReq{
		Login:                "Test",
		PasswordHashInBase64: "Test",
	}
	bytes, err := json.Marshal(reqDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))

	if string(bytes) != `{"login":"Test","password":"Test"}` {
		t.Error()
	}
}

func Test_SignInPostRes_Marshal(t *testing.T) {
	resDto := SignInPostRes{
		SignedTokens{
			AccessToken:  "Access",
			RefreshToken: "Refresh",
		},
	}
	bytes, err := json.Marshal(resDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))

	if string(bytes) != `{"access-token":"Access","refresh-token":"Refresh"}` {
		t.Error()
	}
}
