package dto

import (
	"encoding/json"
	"fmt"
	"testing"
)

func Test_ResWithError_Marshal(t *testing.T) {
	reqDto := ResWithError{
		Error: Error{
			Id:   100,
			Text: "Test",
		},
	}
	bytes, err := json.Marshal(reqDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))

	if string(bytes) != `{"error":{"id":100,"text":"Test"}}` {
		t.Error()
	}
}
