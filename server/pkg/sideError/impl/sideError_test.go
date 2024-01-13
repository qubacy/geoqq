package impl

import (
	"errors"
	"fmt"
	"reflect"
	"testing"
)

func Test_New(t *testing.T) {
	err := New(errors.New("Module error"), Server)
	fmt.Println(err)

	lastErr := errors.Unwrap(err)
	fmt.Println(lastErr) // !

	// ***

	sideErr, converted := err.(*SideError)
	if !converted {
		t.Error("Unknown type")
	}

	fmt.Println("Side:",
		sideErr.Side())
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
	sideErr := NewSideError(
		fmt.Errorf("Module error\n with an error %w",
			errors.New("Module error")),
		Client,
	) // last!

	err := fmt.Errorf("With side error %w", sideErr)
	err = fmt.Errorf("With error %w", err)
	err = fmt.Errorf("With error %w", err)
	err = fmt.Errorf("With error %w", err)

	sideErr = UnwrapErrorsToLastSide(err)
	if sideErr == nil {
		t.Error("Unexpected error")
	}

	fmt.Println(err)

	// ***

	fmt.Println("Side:", sideErr.Side())
	fmt.Println("Error:", sideErr.UnwrapToLast())
}

func Test_UnwrapErrorToLastSide_v1(t *testing.T) {
	err :=
		fmt.Errorf("Basic error #0 with %w",
			NewSideError(fmt.Errorf("Side error #0 with %w",
				fmt.Errorf("Basic error #1 with %w",
					NewSideError(fmt.Errorf("Side error #1 with %w",
						errors.New("Module error")),
						Server))), Client,
			),
		)
	fmt.Println(err)

	// ***

	sideErr := UnwrapErrorsToLastSide(err)
	fmt.Println(sideErr)

	// ***

	if sideErr == nil {
		t.Error("Unexpected result")
	}

	fmt.Println("Error:", sideErr.Error())
	fmt.Println("Side:", sideErr.Side())

	err = sideErr.Unwrap()
	fmt.Println("Internal error:", err)

	// ***

	for errors.Unwrap(err) != nil {
		err = errors.Unwrap(err)
	}
	fmt.Println("Last error:", err)
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

	sideErr := UnwrapErrorsToLastSide(err) // new variable!
	fmt.Println("Side error:", err)

	if sideErr == nil {
		fmt.Println("Side error not found!") // !
	}
	err = sideErr
	if err == nil {
		t.Error("Unexpected result") // how so? nil is not the same?
	}

	// ***

	if sideErr != nil {
		t.Error("Unexpected result")
	}
}

// experiments
// -----------------------------------------------------------------------

func Test_equal_nils(t *testing.T) {
	var sideError *SideError = nil
	var err error = sideError // <--- nil!

	fmt.Println("Side error:", sideError)
	fmt.Println("Basic error:", err)

	if sideError != err {
		t.Error("Unexpected result")
	}
	if !reflect.DeepEqual(sideError, err) {
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
	if reflect.DeepEqual(sideError, err) {
		t.Error("Unexpected result")
	}
}

func Test_equal_nils_v2(t *testing.T) {
	/*
		var err error = nil
		var sideError *SideError = err // cannot be assigned!
	*/
}

// -----------------------------------------------------------------------

func Test_errors_Unwrap(t *testing.T) {
	err := errors.Unwrap(nil)
	if err != nil {
		t.Error("Unexpected result")
	}
}
