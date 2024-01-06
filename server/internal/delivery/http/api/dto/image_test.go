package dto

import (
	"encoding/json"
	"fmt"
	"testing"
)

func Test_ImageByIdRes_Marshal(t *testing.T) {
	resDto := ImageByIdRes{
		Image{
			Id:      123,
			Content: "Test",
		},
	}
	bytes, err := json.Marshal(resDto)
	if err != nil {
		t.Error()
	}
	fmt.Println(string(bytes))
}
