package token

import (
	"errors"
	"reflect"
	"strings"
)

// zero when the field is missing.
const (
	Unknown = iota

	ForAccess
	ForRefresh
	//..?
)

type Payload struct {
	UserId  uint64 `json:"user-id"` // float64
	Purpose int    `json:"purpose"`
}

func MakePayload(userId uint64, purpose int) Payload {
	return Payload{
		UserId:  userId,
		Purpose: purpose,
	}
}

// -----------------------------------------------------------------------

func (p Payload) Validate() error {
	if p.Purpose != ForAccess &&
		p.Purpose != ForRefresh {
		return errors.New("unknown token purpose")
	}
	return nil
}

func (p Payload) GetJsonFieldNames() []string {
	names := []string{}
	t := reflect.TypeOf(p)
	for i := 0; i < t.NumField(); i++ {
		jsonWholeTag := t.Field(i).Tag.Get("json")
		jsonTags := strings.Split(jsonWholeTag, ",")
		names = append(names, jsonTags[0])
	}
	return names
}

/*

For some reason, this redefinition
	ignores the remaining parts of the composite structure.

func (p *Payload) UnmarshalJSON(rawJson []byte) error {
	var jsonObject map[string]any
	json.Unmarshal(rawJson, &jsonObject)

	keys := make([]string, 0, len(jsonObject))
	for k := range jsonObject {
		keys = append(keys, k)
	}

	values := []float64{}
	fieldNames := p.GetJsonFieldNames()
	for i := range fieldNames {
		if slices.Index(keys, fieldNames[i]) == -1 {
			return errors.New("json object is missing some keys")
		}
		number, converted := jsonObject[fieldNames[i]].(float64)
		if !converted {
			return errors.New("json object has values of the wrong type")
		}

		values = append(values, number)
	}

	p.UserId = uint64(values[0])
	p.Purpose = int(values[1])

	return nil
}
*/
