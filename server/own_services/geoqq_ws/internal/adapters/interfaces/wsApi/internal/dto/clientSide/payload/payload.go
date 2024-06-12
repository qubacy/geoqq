package payload

import (
	utl "common/pkg/utility"
	"encoding/json"
)

func NewFromAny[T any](value any) (*T, error) {
	jsonBytes, err := json.Marshal(value)
	if err != nil {
		return nil, utl.NewFuncError(NewFromAny[T], err)
	}

	var specificPayload T
	if err := json.Unmarshal(jsonBytes, &specificPayload); err != nil {
		return nil, utl.NewFuncError(NewFromAny[T], err)
	}

	return &specificPayload, nil
}
