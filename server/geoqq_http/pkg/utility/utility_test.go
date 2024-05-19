package utility

import (
	"errors"
	"fmt"
	"os"
	"reflect"
	"runtime"
	"strconv"
	"strings"
	"testing"
)

func Test_GetFunctionName(t *testing.T) {
	testCases := []struct {
		param interface{}
		want  string
	}{
		{111, ""},
		{111.111, ""},
		{"string", ""},
		{strings.Compare, "strings.Compare"},
		{strings.Contains, "strings.Contains"},
		{strings.Count, "strings.Count"},
		{GetFunctionName, "utility.GetFunctionName"},
		{IsFunction, "utility.IsFunction"},
		//...
	}

	for i := range testCases {
		actual := GetFunctionName(testCases[i].param)
		if actual != testCases[i].want {
			t.Fail()
			return
		}
	}
}

func Test_IsFunction(t *testing.T) {
	testCases := []struct {
		param interface{}
		want  bool
	}{
		{111, false},
		{111.111, false},
		{"string", false},
		{strings.Compare, true},
		{strings.Contains, true},
		{strings.Count, true},
		//...
	}

	// ***

	for i := range testCases {
		actual := IsFunction(testCases[i].param)
		if actual != testCases[i].want {
			t.Fail()
			return
		}
	}
}

func Test_CreateCustomError(t *testing.T) {
	err := NewFuncError(GetFunctionName, fmt.Errorf("<some library error>"))
	err = NewFuncError(GetFunctionName, err)
	err = NewFuncError(strconv.Atoi, err)
	err = NewFuncError(IsFunction, err)
	//...
	fmt.Println(err.Error())
}

func Test_CreateCustomError_v1(t *testing.T) {
	err := NewFuncError(GetFunctionName, fmt.Errorf("<some library error>"))
	fmt.Println(err.Error())

	err = errors.Unwrap(err)
	fmt.Println(err.Error())
}

func Test_UnwrapErrorsToLast(t *testing.T) {
	libraryErr := "<some library error>"
	err := NewFuncError(GetFunctionName, fmt.Errorf(libraryErr))
	err = NewFuncError(GetFunctionName, err)
	err = NewFuncError(strconv.Atoi, err)
	err = NewFuncError(IsFunction, err)
	//...

	lastErr := UnwrapErrorsToLast(err)
	fmt.Println(lastErr.Error())
	if lastErr.Error() != libraryErr {
		t.Errorf("Is there something wrong")
	}
}

func Test_RandomString(t *testing.T) {
	wantLength := 10
	if gotLength := len(RandomString(wantLength)); gotLength != wantLength {
		t.Errorf("Unexpected str len: %v", gotLength)
	}

	wantLength = 25
	if gotLength := len(RandomString(wantLength)); gotLength != wantLength {
		t.Errorf("Unexpected str len: %v", gotLength)
	}

	// ***

	fmt.Println(RandomString(10))
	fmt.Println(RandomString(10))
	fmt.Println(RandomString(10))
	//...
}

func Test_RandomInt(t *testing.T) {
	fmt.Println(RandomInt(10, 100))
	fmt.Println(RandomInt(100, 1000))
	//...

	for i := 0; i < 100; i++ {
		value := RandomInt(10, 100)
		if value < 10 || value >= 100 {
			t.Error("Random value out of range")
		}
	}
}

func Test_RemoveAdjacentSpacesFromString(t *testing.T) {
	type TestCase struct {
		input  string
		output string
	}
	testCases := []TestCase{
		{"123   123   123", "123 123 123"},
		{"123\n\n\n\n\n\n\n123\n\n\n\n\n123", "123 123 123"},
		{"123\t\t\t\t\n\n\n   123\t\t\n\n\n   123", "123 123 123"},
		//...
	}

	for i := range testCases {
		got := RemoveAdjacentSpacesFromString(testCases[i].input)
		if got != testCases[i].output {
			t.Error("Unexpected result")
		}
	}
}

// benchmarks
// -----------------------------------------------------------------------

func Benchmark_GetFunctionName(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if x := GetFunctionName(strings.Contains); x != "strings.Contains" {
			b.Fatalf("Unexpected result: %v", x)
		}
	}
}

func Benchmark_IsFunction(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if x := IsFunction(strings.Contains); x != true {
			b.Fatalf("Unexpected result: %v", x)
		}
	}
}

func Benchmark_RandomString(b *testing.B) {
	length := 15
	for i := 0; i < b.N; i++ {
		str := RandomString(length)
		if len(str) != length {
			b.Fatalf("Unexpected str len: %v", len(str))
		}
	}
}

// experiments
// -----------------------------------------------------------------------

func Test_Split(t *testing.T) {
	testCases := []struct {
		param string
		sep   string
		want  []string
	}{
		{"", ",", []string{""}},
		{"ilserver/utility.GetFunctionName", "/", []string{"ilserver", "utility.GetFunctionName"}},
		{"123 123 123", " ", []string{"123", "123", "123"}},
		{"123   123   123", " ", []string{"123", "", "", "123", "", "", "123"}},
	}

	// ***

	for i := range testCases {
		actual := strings.Split(
			testCases[i].param,
			testCases[i].sep,
		)

		if len(actual) != len(testCases[i].want) {
			t.Fail()
			return
		}
		if reflect.TypeOf(actual) != reflect.TypeOf(testCases[i].want) {
			t.Fail()
			return
		}
		if !reflect.DeepEqual(actual, testCases[i].want) {
			t.Fail()
			return
		}
	}
}

func Test_FunctionType(t *testing.T) {
	var fun interface{} = GetFunctionName

	funType := reflect.TypeOf(fun)
	fmt.Println("fun type:", funType)

	funTypeName := funType.String()
	fmt.Println("fun type name:", funTypeName)

	if !strings.Contains(funType.String(), "func") {
		t.Fail()
		return
	}
}

func Test_FuncName(t *testing.T) {
	pc, file, line, ok := runtime.Caller(0)
	if !ok {
		t.Fail()
		return
	}

	fmt.Printf("pc: %v\n", pc)
	fmt.Printf("file: %v\n", file)
	fmt.Printf("line: %v\n", line)

	fn := runtime.FuncForPC(pc)
	if fn == nil {
		t.Fail()
		return
	}

	fmt.Println()
	fmt.Printf("full function name: %v\n", fn.Name())

	shortFuncName := strings.Split(fn.Name(), ".")[1]
	fmt.Printf("short function name: %v\n", shortFuncName)

	if shortFuncName != "Test_FuncName" {
		t.Fail()
		return
	}
}

func Test_FuncName_v1(t *testing.T) {
	functionName := runtime.FuncForPC(
		reflect.ValueOf(
			Test_FuncName_v1).Pointer()).Name()

	fmt.Println("function name:", functionName)

	shortFuncName := strings.Split(functionName, ".")[1]
	fmt.Printf("short function name: %v\n", shortFuncName)

	if shortFuncName != "Test_FuncName_v1" {
		t.Fail()
		return
	}
}

func Test_WrapError(t *testing.T) {
	err := errors.New("0")
	err = fmt.Errorf("1 %w", err)
	err = fmt.Errorf("2 %w", err)

	// ***

	if err.Error() != "2 1 0" {
		t.Fail()
		return
	}
	err = errors.Unwrap(err)
	if err.Error() != "1 0" {
		t.Fail()
		return
	}
	err = errors.Unwrap(err)
	if err.Error() != "0" {
		t.Fail()
		return
	}
	err = errors.Unwrap(err)
	if err != nil {
		t.Fail()
		return
	}
}

func Test_Executable(t *testing.T) {
	path, err := os.Executable()
	if err != nil {
		fmt.Println("err:", err.Error())
		t.Fail()
		return
	}

	fmt.Printf("path: %v\n", path)
}

func Test_Getwd(t *testing.T) {
	path, err := os.Getwd()
	if err != nil {
		fmt.Println("err:", err.Error())
		t.Fail()
		return
	}

	fmt.Printf("path: %v\n", path)
}

func Test_RemoveDuplicate(t *testing.T) {
	result := RemoveDuplicatesFromSlice[uint64](
		[]uint64{1, 2, 1, 2, 4, 5, 6},
	)

	fmt.Println(result)
}

func Test_NumbersToString(t *testing.T) {
	type TestCase struct {
		input []uint64
		want  string
	}
	testCases := []TestCase{
		{[]uint64{}, ""},
		{[]uint64{1, 2, 3}, "1,2,3"},
		{[]uint64{1, 2, 100, 1000}, "1,2,100,1000"},
		//...
	}

	for i := range testCases {
		got := NumbersToString(testCases[i].input)
		if testCases[i].want != got {
			t.Errorf("Error 'want' \"%s\" not eq 'got' \"%s\"",
				testCases[i].want, got)
		}
	}
}
