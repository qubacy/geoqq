package impl

import (
	"errors"
	"fmt"
	"testing"
)

func Test_New(t *testing.T) {
	err := New(errors.New("Text error"), Server)
	fmt.Println(err)

	nilErr := errors.Unwrap(err)
	fmt.Println(nilErr) // !

	// ***

	sideErr, converted := err.(*SideError)
	if !converted {
		t.Error("Unknown type")
	}

	fmt.Println("side:", sideErr.side)
}

func Test_Side(t *testing.T) {
	err := NewSideError(errors.New("Text error"), Client)
	fmt.Println(err)

	fmt.Println("side", err.Side())
}

func Test_Unwrap(t *testing.T) {
	err := errors.New("External module error")
	err = New(err, Client)

	fmt.Println(err)
}

func Test_Unwrap_v1(t *testing.T) {
	err := errors.New("External module error")
	err = fmt.Errorf("External up err with %w", err)

	err = New(err, Client)

	fmt.Println(err)
}

func Test_Wrap(t *testing.T) {
	err := New(errors.New("Test error"), Client)
	err = fmt.Errorf("Error wrapper %w", err)

	sideErr, converted := err.(*SideError)
	if converted {
		t.Error("Unexpected error type")
	}
	if sideErr != nil {
		t.Error("Unexpected conversion")
	}
}

// -----------------------------------------------------------------------

func Test_UnwrapErrorToLastSide(t *testing.T) {
	sideErr0 := New(errors.New("Side error #0"), Client) // last!

	err := fmt.Errorf("With error #0 %w", sideErr0)
	err = fmt.Errorf("With error %w", err)
	err = fmt.Errorf("With error %w", err)
	err = fmt.Errorf("With error %w", err)

	fmt.Println(err)
}

func Test_UnwrapErrorToLastSide_v1(t *testing.T) {
	err :=
		NewSideError(
			fmt.Errorf("Basic error #1 with %w",
				fmt.Errorf("Basic error #0 with %w",
					NewSideError(fmt.Errorf("Side error #0 with %w",
						errors.New("External module error")),
						Server))),
			Client)

	fmt.Println(err)

	err = UnwrapErrorToLastSide(err)
	fmt.Println(err)
}

func Test_UnwrapErrorToLastSide_v2(t *testing.T) {
	err :=
		fmt.Errorf("Basic error #1 with\n%w",
			fmt.Errorf("Basic error #2 with\n%w",
				fmt.Errorf("Basic error #3 with\n%w",
					errors.New("External module error"),
				),
			),
		)

	fmt.Println(err)

	// ***

	sideErr := UnwrapErrorToLastSide(err) // !
	fmt.Println("Side error:", err)

	if sideErr == nil {
		fmt.Println("Not found")
	}

	if sideErr != nil {
		t.Error("Unexpected result")
	}

	// ***

	err = sideErr
	if err == nil { // how so?
		t.Error("Unexpected result")
	}
}

// experiments
// -----------------------------------------------------------------------

func Test_equal_nils(t *testing.T) {
	var sideError *SideError = nil
	var err error = sideError

	fmt.Println("Side error:", sideError)
	fmt.Println("Basic error:", err)

	if sideError != err {
		t.Error("Unexpected result")
	}
}

func Test_equal_nils_v1(t *testing.T) {
	var sideError *SideError = nil
	var err error = nil

	fmt.Println("Side error:", sideError)
	fmt.Println("Basic error:", err)

	if sideError == err {
		t.Error("Unexpected result")
	}
}

func Test_equal_nils_v2(t *testing.T) {
	/*
		var err error = nil
		var sideError *SideError = err // cannot be assigned!
	*/
}
