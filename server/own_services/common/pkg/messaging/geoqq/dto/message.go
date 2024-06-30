package dto

import (
	utl "common/pkg/utility"
	"encoding/json"
)

type Message struct {
	Event   string `json:"event"`
	Payload any    `json:"payload"`
}

func PayloadFromAny[T any](rawPayload any) (T, error) {
	var specPayload T
	if jsonBytes, err := json.Marshal(rawPayload); err != nil {
		return specPayload, utl.NewFuncError(PayloadFromAny[T], err)
	} else {
		if err = json.Unmarshal(jsonBytes, &specPayload); err != nil {
			return specPayload, utl.NewFuncError(PayloadFromAny[T], err)
		}
	}

	return specPayload, nil // ok
}
