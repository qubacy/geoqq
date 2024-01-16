package impl

import (
	"errors"
	"fmt"
	"regexp"
	"testing"
)

// experiments
// -----------------------------------------------------------------------

func Test_regexp_String(t *testing.T) {
	re, err := regexp.Compile(`\d\d\d`)
	if err != nil {
		t.Error()
	}

	fmt.Println("Source regex:", re.String())
}

func Test_errors_Join(t *testing.T) {
	err := errors.Join(nil, nil, nil)
	fmt.Println(err)

	if err != nil {
		t.Error("Unexpected behavior")
	}
}

func Test_regexp_Compile_okk(t *testing.T) {
	re, err := regexp.Compile(`\d_\d{3}_\d{3}_\d\d\d\d`)
	if err != nil {
		t.Error()
	}

	matched := re.Match([]byte("8_888_888_8888"))
	if !matched {
		t.Error()
	}

	fmt.Println("Source regexp:", re)
}

func Test_regexp_Compile_err(t *testing.T) {
	re, err := regexp.Compile(`\d_\d{3}_\d{3}_\d\d\d\d(???)`)
	if err == nil {
		t.Error()
	}

	fmt.Println("Source regexp:", re)
}

// benchmarks
// -----------------------------------------------------------------------

func Benchmark_regexp_Compile(b *testing.B) {
	for i := 0; i < b.N; i++ {
		re, err := regexp.Compile(`\d_\d{3}_\d{3}_\d\d\d\d`)
		if err != nil {
			b.Error()
		}

		matched := re.Match([]byte("8_888_888_8888"))
		if !matched {
			b.Error()
		}
	}
}

func Benchmark_regexp_Compile_v1(b *testing.B) {
	re, err := regexp.Compile(`\d_\d{3}_\d{3}_\d\d\d\d`)
	if err != nil {
		b.Error()
	}

	for i := 0; i < b.N; i++ {
		matched := re.Match([]byte("8_888_888_8888"))
		if !matched {
			b.Error()
		}
	}
}
