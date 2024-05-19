package dto

import (
	"encoding/json"
	"fmt"
	"geoqq_http/internal/domain"
	"testing"
)

func Test_MateChatsRes_toJson(t *testing.T) {
	res := MateChatsRes{}
	if res.Chats != nil {
		t.Error("chats is not nil")
	}

	jsonRes, err := json.Marshal(res)
	if err != nil {
		t.Error(err)
	}

	fmt.Println(string(jsonRes))
}

func Test_MateChatsRes_toJson_v2(t *testing.T) {
	res := MateChatsRes{
		Chats: make([]*MateChat, 0),
	}
	if res.Chats == nil {
		t.Error("chats is nil")
	}

	jsonRes, err := json.Marshal(res)
	if err != nil {
		t.Error(err)
	}

	fmt.Println(string(jsonRes))
}

// -----------------------------------------------------------------------

func Test_MakeMateChatsResFromOutput_toJson(t *testing.T) {
	res, err := NewMateChatsResFromOutput(
		make(domain.MateChatList, 0),
	)
	if err != nil {
		t.Error(err)
	}

	if res.Chats == nil {
		t.Error("chats is nil")
	}

	jsonRes, err := json.Marshal(res)
	if err != nil {
		t.Error(err)
	}

	fmt.Println(string(jsonRes))
}
