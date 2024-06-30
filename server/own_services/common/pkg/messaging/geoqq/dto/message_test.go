package dto

import (
	"common/pkg/messaging/geoqq/dto/payload"
	utl "common/pkg/utility"
	"encoding/json"
	"log"
	"testing"
)

func Test_MateRequestFromAny(t *testing.T) {
	var jsonStr string = utl.RemoveAdjacentWs(`
		{
			"event": "added_mate_request",
			"payload": {
				"target-user-id": 2,
				"id": 1,
				"user-id": 1
			}
		}`)

	var msg Message
	if err := json.Unmarshal([]byte(jsonStr), &msg); err != nil {
		t.Error(err)
		return
	}

	mr, err := PayloadFromAny[payload.MateRequest](msg.Payload)
	if err != nil {
		t.Error(err)
		return
	}

	if mr.TargetUserId != 2 || mr.UserId != 1 || mr.Id != 1 {
		t.Error(err)
		return
	}

	log.Println(mr)
}

func Test_MateMessageFromAny(t *testing.T) {
	var jsonStr string = utl.RemoveAdjacentWs(`
		{
			"event": "added_mate_message",
			"payload": {
				"target-user-id": 2,
				"id": 3,
				"chat-id": 2,
				"text": "Hi!",
				"time": 1719731337,
				"user-id": 1,
				"read": false
			}
		}`)

	var msg Message
	if err := json.Unmarshal([]byte(jsonStr), &msg); err != nil {
		t.Error(err)
		return
	}

	mm, err := PayloadFromAny[payload.MateMessage](msg.Payload)
	if err != nil {
		t.Error(err)
		return
	}

	log.Println(mm)
}
