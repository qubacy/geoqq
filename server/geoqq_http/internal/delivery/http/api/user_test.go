package api

import (
	"encoding/json"
	"fmt"
	"geoqq_http/internal/delivery/http/api/dto"
	"testing"
)

func Test_json_Unmarshal_NullToInt(t *testing.T) {
	privacy := dto.Privacy{}
	privacyJson := []byte(
		`{"hit-me-up": null}`,
	)

	err := json.Unmarshal(privacyJson, &privacy)
	if err != nil {
		t.Error(err)
	}

	fmt.Println("Privacy.HitMeUp:",
		privacy.HitMeUp)
}
