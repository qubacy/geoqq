package utility

import (
	"bytes"
	"errors"
	"fmt"
	"image"
	"image/png"
	"math/rand"
	"reflect"
	"runtime"
	"strconv"
	"strings"
)

// about error
// -----------------------------------------------------------------------

func IsFunction(i interface{}) bool {
	funType := reflect.TypeOf(i)
	funTypeName := funType.String()
	return strings.Contains(funTypeName, "func")
}

func GetFunctionName(i interface{}) string {
	if !IsFunction(i) {
		return ""
	}

	fullFunctionName := runtime.FuncForPC(
		reflect.ValueOf(
			i).Pointer()).Name()

	parts := strings.Split(fullFunctionName, "/")
	if len(parts) == 0 { // impossible?
		return ""
	}

	shortFunctionName := parts[len(parts)-1]
	return shortFunctionName
}

// creating a stack with errors!
func NewFuncError(i interface{}, err error) error {
	return fmt.Errorf(GetFunctionName(i)+"\n with an error/in: %w", err)
}

func UnwrapErrorsToLast(err error) error {
	for errors.Unwrap(err) != nil {
		err = errors.Unwrap(err)
	}
	return err
}

// generators
// -----------------------------------------------------------------------

func RandomString(n int) string {
	chars := []rune("abcdefghijklmnopqrstuvwxyz0123456789")
	result := make([]rune, n)
	for i := range result {
		randIndex := rand.Intn(len(chars))
		result[i] = chars[randIndex]
	}
	return string(result)
}

// [min, max)
func RandomInt(min, max int) int {
	return min + rand.Intn(max-min)
}

// converters
// -----------------------------------------------------------------------

func ImageToPngBytes(img image.Image) ([]byte, error) {
	buf := new(bytes.Buffer)
	err := png.Encode(buf, img)
	if err != nil {
		return nil, NewFuncError(ImageToPngBytes, err)
	}

	return buf.Bytes(), nil
}

func RemoveDuplicatesFromSlice[T comparable](sliceList []T) []T {
	allKeys := make(map[T]bool)
	list := []T{}
	for _, item := range sliceList {
		if _, value := allKeys[item]; !value {
			allKeys[item] = true
			list = append(list, item)
		}
	}
	return list
}

func NumbersToString[T uint64](numbers []T) string {
	strNumbers := []string{}
	for i := range numbers {
		strNumbers = append(strNumbers,
			strconv.FormatUint(uint64(numbers[i]), 10))
	}

	return strings.Join(strNumbers, ",")
}

func RemoveAdjacentSpacesFromString(text string) string {
	fields := strings.Fields(text)
	return strings.Join(fields, " ")
}

func RemoveAdjacentWs(text string) string {
	return RemoveAdjacentSpacesFromString(text)
}

// -----------------------------------------------------------------------

func ConvertSliceFloat64ToUint64(values []float64) []uint64 {
	result := []uint64{}
	for i := range values {
		result = append(result, uint64(values[i]))
	}
	return result
}
