package dto

import (
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"strconv"
	"testing"
)

func Test_SignInPostReq_Marshal(t *testing.T) {
	reqDto := SignInPostReq{
		Login:        "Test",
		PasswordHash: "Test",
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

// experiments

func Test_StringToSha256ToBase64(t *testing.T) {
	//sourceValue := "test_user124reg"
	sourceValue := "test_user124reg_"

	// to sha256

	h := sha256.New()
	h.Write([]byte(sourceValue))
	sha256Value := h.Sum(nil)

	fmt.Printf("%x\n", sha256Value)

	// to base64

	base64Value := make([]byte, 256)
	base64.StdEncoding.Encode(base64Value, sha256Value)

	// password hash in base64

	fmt.Println(string(base64Value))
}

func Test_StringToSha256ToHex(t *testing.T) {
	sourceValue := "test_user124reg"

	// to sha256

	h := sha256.New()
	h.Write([]byte(sourceValue))
	sha256Value := h.Sum(nil)

	fmt.Printf("%x\n", sha256Value)

	// to base64

	hexValue := make([]byte, 256)
	hex.Encode(hexValue, sha256Value)

	// password hash in base64

	fmt.Println(string(hexValue))
}

func Test_comparison_base64(t *testing.T) {
	for i := 0; ; i++ {
		h := sha256.New()
		h.Write([]byte(strconv.Itoa(i)))
		sourceValue := h.Sum(nil)

		// **

		stdBase64Value := make([]byte, 512)
		base64.StdEncoding.Encode(stdBase64Value, sourceValue)

		urlBase64Value := make([]byte, 512)
		base64.URLEncoding.Encode(urlBase64Value, sourceValue)

		// ***

		stdBase64StrValue := string(stdBase64Value)
		urlBase64StrValue := string(urlBase64Value)

		if urlBase64StrValue != stdBase64StrValue {
			fmt.Printf("Source Index: %v\n", i)
			fmt.Printf("Source Value: %v\n", hex.EncodeToString(sourceValue))

			fmt.Printf("\tStd Base64 Value: %v\n", stdBase64StrValue)
			fmt.Printf("\tUrl Base64 Value: %v\n", urlBase64StrValue)

			fmt.Println()
			return
		}
	}
}
