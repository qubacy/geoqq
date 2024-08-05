package rabbit

import (
	"common/pkg/messaging/geoqq/dto"
	"common/pkg/messaging/geoqq/dto/payload"
	"encoding/json"
	"fmt"
	"geoqq_http/internal/infra/msgs"
	"math/rand"
	"testing"
	"time"
)

func Test_timeToString(t *testing.T) {
	fmt.Println(time.Now().UTC().UnixMilli())

	dur := 5 * time.Minute
	fmt.Printf("%v", dur.Milliseconds())
}

func Test_timeToUtcToUtc(t *testing.T) {
	fmt.Println(time.Now())
	fmt.Println(time.Now().UTC())
	fmt.Println(time.Now().UTC().UTC())

	fmt.Println(time.Now().UnixMilli())
	fmt.Println(time.Now().UTC().UnixMilli())
	fmt.Println(time.Now().UTC().UTC().UnixMilli())
	//...
}

// -----------------------------------------------------------------------

func Test_Message_OnlyId(t *testing.T) {
	userId := rand.Int31n(1000)
	eventName := msgs.EventUpdatedPublicUser

	// ***

	oid := payload.OnlyId{Id: float64(userId)}
	msg := dto.Message{Event: eventName, Payload: &oid}
	printAnyToJson(msg, t)
}

func Test_Message_TargetWithId(t *testing.T) {
	targetUserId := rand.Int31n(1000)
	chatId := rand.Int31n(1000)
	eventName := msgs.EventAddedMateChat

	// ***

	twid := payload.TargetWithId{
		TargetUserId: float64(targetUserId),
		Id:           float64(chatId)}
	msg := dto.Message{Event: eventName, Payload: &twid}
	printAnyToJson(msg, t)
}

func Test_Message_MateRequest(t *testing.T) {
	targetUserId := rand.Int31n(1000)
	requestId := rand.Int31n(1000)
	requesterUserId := rand.Int31n(1000)
	eventName := msgs.EventAddedMateRequest

	// ***

	mr := payload.MateRequest{
		TargetUserId: float64(targetUserId),
		Id:           float64(requestId),
		UserId:       float64(requesterUserId), // from!
	}
	msg := dto.Message{Event: eventName, Payload: &mr}
	printAnyToJson(msg, t)
}

// -----------------------------------------------------------------------

func Test_Message_MateMessage(t *testing.T) {
	mateMessageId := rand.Int31n(1000)
	targetUserId := rand.Int31n(1000)
	chatId := rand.Int31n(1000)
	userId := rand.Int31n(1000)
	eventName := msgs.EventAddedMateMessage

	// ***

	mm := payload.MateMessage{
		TargetUserId: float64(targetUserId),
		Id:           float64(mateMessageId),
		ChatId:       float64(chatId),
		Text:         "some text",
		Time:         float64(time.Now().Unix()),
		UserId:       float64(userId),
		Read:         false,
	}
	msg := dto.Message{Event: eventName, Payload: &mm}
	printAnyToJson(msg, t)
}

func Test_Message_GeoMessage(t *testing.T) {
	geoMessageId := rand.Int31n(1000)
	userId := rand.Int31n(1000)
	eventName := msgs.EventAddedGeoMessage

	// ***

	gm := payload.GeoMessage{
		Id:        float64(geoMessageId),
		Text:      "some text",
		Time:      float64(time.Now().Unix()),
		UserId:    float64(userId),
		Latitude:  rand.Float64(),
		Longitude: rand.Float64(),
	}
	msg := dto.Message{Event: eventName, Payload: &gm}
	printAnyToJson(msg, t)
}

// -----------------------------------------------------------------------

func printAnyToJson(a any, t *testing.T) {
	jsonBytes, err := json.Marshal(a)
	if err != nil {
		t.Error(err)
		return
	}
	fmt.Printf("json msg: %v", string(jsonBytes))
}
