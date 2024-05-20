package impl

import (
	"bytes"
	"common/pkg/token"
	"common/pkg/utility"
	"encoding/base64"
	"flag"
	"fmt"
	"os"
	"reflect"
	"strings"
	"testing"
	"time"

	"github.com/cristalhq/jwt/v5"
	"github.com/spf13/viper"
)

func TestMain(m *testing.M) {
	fmt.Println("...start test main...")

	// no need to use server options!
	if err := setUp(); err != nil {
		fmt.Println(err)
		os.Exit(-1)
		return
	}

	// ***

	flag.Parse() // ?
	code := m.Run()

	// ***

	fmt.Println("...finish test main...")
	os.Exit(code)
}

func setUp() error {
	err := setUpViper()
	if err != nil {
		return err
	}

	return nil
}

func setUpViper() error {
	// mock config...

	key := "token.secret"
	viper.Set(key, "signing_key")
	if len(viper.GetString(key)) == 0 {
		return fmt.Errorf("Value by key '%v' is empty", key)
	}

	key = "any_token.duration"
	viper.Set(key, 876000*time.Hour) // 100 years...
	if len(viper.GetString(key)) == 0 {
		return fmt.Errorf("Value by key '%v' is empty", key)
	}

	return nil
}

// tests
// -----------------------------------------------------------------------

func Test_Payload_GetJsonFieldNames(t *testing.T) {
	p := token.Payload{}

	wantNames := []string{"user-id", "purpose"}
	gotNames := p.GetJsonFieldNames()

	if !reflect.DeepEqual(wantNames, gotNames) {
		t.Error()
	}
}

func Test_New(t *testing.T) {
	manager := newTokenManagerWithChecks(t)

	// ***

	tokenValue, err := manager.New(
		token.MakePayload(123, token.ForRefresh),
		viper.GetDuration("any_token.duration"),
	)
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}

	// ***

	fmt.Println(tokenValue)
	tokenParts := strings.Split(tokenValue, ".")
	if len(tokenParts) != 3 {
		t.Errorf("Is there something wrong. Err: %v", err)
	}

	// *** view token parts ***

	encoding := base64.StdEncoding.WithPadding(base64.NoPadding) // ?
	headerBytes, err := encoding.DecodeString(tokenParts[0])     // header
	if err != nil {
		t.Errorf("Decode header failed. Err: %v", err)
	}
	fmt.Println(string(headerBytes))

	payloadBytes, err := encoding.DecodeString(tokenParts[1]) // payload
	if err != nil {
		t.Errorf("Decode payload failed. Err: %v", err)
	}
	fmt.Println(string(payloadBytes))
}

// -----------------------------------------------------------------------

func Test_Validate_okk(t *testing.T) {
	tokenExtractor := newTokenManagerWithChecks(t)

	// used duration from viper!
	tokenValue := "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjQ4NjQzNTYxMzEsInVzZXItaWQiOjEyMywicHVycG9zZSI6MX0.7PPBknnlyap1RviqNVA3DY72KW6H1WIQmKHvv05Y1bc"

	err := tokenExtractor.Validate(tokenValue)
	if err != nil {
		t.Errorf("Failed to validate valid token. Err: %v", err)
	}

	payload, err := tokenExtractor.Parse(tokenValue)
	if err != nil {
		t.Errorf("Failed to validate valid token. Err: %v", err)
	}

	fmt.Println(payload)
}

func Test_Validate_ErrInvalidFormat(t *testing.T) {
	tokenExtractor := newTokenManagerWithChecks(t)

	tokenValue := "123"
	err := tokenExtractor.Validate(tokenValue)
	if err == nil {
		t.Errorf("Invalid token successfully verified")
	}
	fmt.Println("Get error:", err)

	if utility.UnwrapErrorsToLast(err) != jwt.ErrInvalidFormat {
		t.Error()
	}
}

func Test_Validate_ErrTokenExpired(t *testing.T) {
	tokenExtractor := newTokenManagerWithChecks(t)

	tokenValue := "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDU0MTU5MDcsInVzZXItaWQiOjF9.XhcWnxVPumSQ8Y8yo6ibzmDvHnL1fa8SUlxc_KZSBpE"
	err := tokenExtractor.Validate(tokenValue)
	if err == nil {
		t.Errorf("Invalid token successfully verified")
	}

	if utility.UnwrapErrorsToLast(err) != ErrTokenExpired {
		t.Error()
	}
}

// -----------------------------------------------------------------------

func Test_Parse_okk(t *testing.T) {
	tokenManager := newTokenManagerWithChecks(t)
	tokenValue := "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjQ4NjQzNTYxMzEsInVzZXItaWQiOjEyMywicHVycG9zZSI6MX0.7PPBknnlyap1RviqNVA3DY72KW6H1WIQmKHvv05Y1bc"
	payload, err := tokenManager.Parse(tokenValue)

	if err != nil {
		t.Errorf("Failed to validate valid token. Err: %v", err)
	}

	fmt.Println("Payload:", payload)
}

func Test_Parse_ErrInvalidFormat(t *testing.T) {
	tokenManager := newTokenManagerWithChecks(t)
	_, err := tokenManager.Parse("...")

	if err == nil {
		t.Errorf("Invalid token successfully verified")
	}

	if utility.UnwrapErrorsToLast(err) != jwt.ErrInvalidFormat {
		t.Error()
	}
}

func Test_Parse_ErrTokenExpired(t *testing.T) {
	tokenManager := newTokenManagerWithChecks(t)

	tokenValue := "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MDU0MTU5MDcsInVzZXItaWQiOjF9.XhcWnxVPumSQ8Y8yo6ibzmDvHnL1fa8SUlxc_KZSBpE"
	_, err := tokenManager.Parse(tokenValue)
	if err == nil {
		t.Errorf("Invalid token successfully verified")
	}

	if utility.UnwrapErrorsToLast(err) != ErrTokenExpired {
		t.Error()
	}
}

// experiments
// -----------------------------------------------------------------------

func Test_base64_StdEncoding_EncodeToString(t *testing.T) {
	result := base64.StdEncoding.EncodeToString([]byte("one 🐘 and three 🐋"))
	_, err := fmt.Println(result)
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
}

func Test_base64_StdEncoding_EncodeToString_v1(t *testing.T) {
	result := base64.StdEncoding.EncodeToString([]byte("deja vu"))
	_, err := fmt.Println(result)
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
}

// -----------------------------------------------------------------------

func Test_base64_StdEncoding_DecodeString(t *testing.T) {
	str := "YmFzZTY0LlN0ZEVuY29kaW5nLkRlY29kZVN0cmluZw=="
	data, err := base64.StdEncoding.DecodeString(str)
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
		return
	}

	_, err = fmt.Println(string(data))
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
}

func Test_base64_StdEncoding_DecodeString_v1(t *testing.T) {
	str := "ZGVqYSB2dQ=="
	data, err := base64.StdEncoding.DecodeString(str)
	if err != nil {
		fmt.Println("error:", err)
		return
	}

	_, err = fmt.Println(string(data))
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
}

// -----------------------------------------------------------------------

func Test_base64_NewEncoder(t *testing.T) {
	buffer := bytes.NewBufferString("")
	encoding := base64.StdEncoding.WithPadding(base64.StdPadding)
	encoder := base64.NewEncoder(encoding, buffer)

	// ***

	n, err := encoder.Write([]byte("deja vu"))
	encoder.Close()

	fmt.Printf("%v bytes written to buffer\n", n)
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
	if n == 0 {
		t.Errorf("Is there something wrong")
	}

	// *** only view content ***

	fmt.Println("Buffer string:", buffer.String())
	fmt.Println("Cap buffer:", buffer.Cap())
	fmt.Println("Len buffer:", buffer.Len())

	if string(buffer.Bytes()) != "ZGVqYSB2dQ==" {
		t.Errorf("Is there something wrong")
	}
}

func Test_base64_NewEncoder_v1(t *testing.T) {
	buffer := bytes.NewBufferString("")
	encoding := base64.StdEncoding.WithPadding(base64.StdPadding)
	encoder := base64.NewEncoder(encoding, buffer)

	// ***

	n, err := encoder.Write([]byte("deja vu"))
	encoder.Close()

	fmt.Printf("%v bytes written to buffer\n", n)
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
	if n == 0 {
		t.Errorf("Is there something wrong")
	}

	// *** take content ***

	rawBuffer := make([]byte, 512)
	n, err = buffer.Read(rawBuffer)
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
	fmt.Printf("%v bytes written to buffer\n", n)
	if n == 0 {
		t.Errorf("Is there something wrong")
	}
	fmt.Println("Raw buffer as string:", string(rawBuffer))

	fmt.Println("Cap buffer:", buffer.Cap())
	fmt.Println("Len buffer:", buffer.Len())
}

func Test_base64_NewEncoder_v2(t *testing.T) {
	buffer := bytes.NewBufferString("")
	input := []byte("foo\x00bar")
	encoder := base64.NewEncoder(base64.StdEncoding, buffer)
	encoder.Write(input)
	encoder.Close()

	// ***

	if "Zm9vAGJhcg==" != buffer.String() {
		t.Error("Is there something wrong")
	}
}

// -----------------------------------------------------------------------

func Test_bytes_NewBufferString(t *testing.T) {
	buffer := bytes.NewBufferString("")
	_, err := fmt.Println("Cap buffer:", buffer.Cap())
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}

	// ***

	n, err := buffer.Write([]byte("12345678901234567890123456789012345"))
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
	if n != 35 {
		t.Errorf("Is there something wrong")
	}

	// ***

	_, err = fmt.Println("Cap buffer:", buffer.Cap())
	if err != nil {
		t.Errorf("Is there something wrong. Err: %v", err)
	}
}

// -----------------------------------------------------------------------

func Test_time_Now(t *testing.T) {
	fmt.Println("Now time:", time.Now())

	var unixTime int64 = time.Now().Unix()
	fmt.Println("Now unix number:", time.Now().Unix())
	fmt.Println("Now unix time:", time.Unix(unixTime, 0))

	fmt.Println("Now unix utc:", time.Now().UTC())
	fmt.Println("Now unix utc time:", time.Now().UTC().Unix())

	//...
}

// private
// -----------------------------------------------------------------------

func newTokenManagerWithChecks(t *testing.T) token.TokenManager {
	manager, err := NewTokenManager(viper.GetString("token.secret"))
	if err != nil {
		t.Fatalf("Failed to new manager object. Err: %v", err)
		return nil // <--- no need?
	}
	return manager
}
