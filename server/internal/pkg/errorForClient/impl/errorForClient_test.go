package impl

import (
	"errors"
	"fmt"
	"testing"
)

func Test_New(t *testing.T) {
	err := New(errors.New("Module error"),
		Server, ServerError)
	fmt.Println(err)

	lastErr := errors.Unwrap(err)
	fmt.Println("Last err:", lastErr) // !

	// ***

	errForClient, converted := err.(*ErrorForClient)
	if !converted {
		t.Error("Unknown type")
	}

	// ***

	fmt.Println("Guilty side:", errForClient.GuiltySide())
	fmt.Println("Client code:", errForClient.ClientCode())
}

func Test_ReadSideAndCode(t *testing.T) {
	err := NewErrorForClient(errors.New("Text error"),
		Client, AuthError) // <--- example!
	fmt.Println(err)

	fmt.Println("Guilty side:", err.GuiltySide())
	fmt.Println("Client code:", err.ClientCode())

	if err.GuiltySide() != Client {
		t.Error()
	}
	if err.ClientCode() != AuthError {
		t.Error()
	}
}

func Test_Unwrap(t *testing.T) {
	err := errors.New("Module error")
	err = New(err, Server, ServerError)

	fmt.Println(err)
}

func Test_Unwrap_v1(t *testing.T) {
	err := errors.New("Module error")
	err = fmt.Errorf("Top module err with %w", err)
	err = New(err, Server, ServerError)

	fmt.Println(err)
}

func Test_Wrap(t *testing.T) {
	err := New(errors.New("Test error"), Client, AuthError)
	err = fmt.Errorf("Error wrapper %w", err)

	// ***

	errForClient, converted := err.(*ErrorForClient)
	if converted {
		t.Error("Unexpected error type")
	}
	if errForClient != nil {
		t.Error("Unexpected conversion")
	}
}

// -----------------------------------------------------------------------

func Test_UnwrapErrorToLastSide(t *testing.T) {
	errForClient := NewErrorForClient(
		fmt.Errorf("Module error\n with an error %w",
			errors.New("Module error")),
		Client, AuthError,
	) // last!

	err := fmt.Errorf("With side error %w", errForClient)
	err = fmt.Errorf("With error %w", err)
	err = fmt.Errorf("With error %w", err)
	err = fmt.Errorf("With error %w", err)

	errForClient = UnwrapErrorsToLastForClient(err)
	if errForClient == nil {
		t.Error("Unexpected error")
	}
	fmt.Println(err)

	// ***

	fmt.Println("Guilty side:", errForClient.GuiltySide())
	fmt.Println("Client code:", errForClient.ClientCode())
	fmt.Println("Error:", errForClient.UnwrapToLast())
}

func Test_UnwrapErrorToLastSide_v1(t *testing.T) {
	err :=
		fmt.Errorf("Basic error #0 with %w",
			NewErrorForClient(fmt.Errorf("Side error #0 with %w",
				fmt.Errorf("Basic error #1 with %w",
					NewErrorForClient(fmt.Errorf("Side error #1 with %w",
						errors.New("Module error")),
						Server, ServerError),
				)), Client, AuthError,
			),
		)
	fmt.Println(err)

	// ***

	errForClient := UnwrapErrorsToLastForClient(err)
	fmt.Println(errForClient)

	// ***

	if errForClient == nil {
		t.Error("Unexpected result")
	}

	fmt.Println("Error:", errForClient.Error())
	fmt.Println("Guilty side:", errForClient.GuiltySide())
	fmt.Println("Client code:", errForClient.ClientCode())

	err = errForClient.Unwrap()
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

	errForClient := UnwrapErrorsToLastForClient(err) // new variable!
	fmt.Println("Side error:", err)

	if errForClient == nil {
		fmt.Println("Side error not found!") // !
	}
	err = errForClient // warning!
	if err == nil {
		t.Error("Unexpected result") // how so? nil is not the same?
	}

	// ***

	if errForClient != nil {
		t.Error("Unexpected result")
	}
}

// experiments
// -----------------------------------------------------------------------

func Test_equal_nils(t *testing.T) {
	var errForClient *ErrorForClient = nil
	var err error = errForClient // <--- nil!

	fmt.Println("Side error:", errForClient)
	fmt.Println("Basic error:", err)

	if errForClient != err {
		t.Error("Unexpected result")
	}
	/*
		if !reflect.DeepEqual(errForClient, err) {
			t.Error("Unexpected result")
		}
	*/
}

func Test_equal_nils_v1(t *testing.T) {
	var errForClient *ErrorForClient = nil
	var err error = nil

	fmt.Println("Side error:", errForClient)
	fmt.Println("Basic error:", err)

	if errForClient == err { // warning!
		t.Error("Unexpected result")
	}
	/*
		if reflect.DeepEqual(errForClient, err) {
			t.Error("Unexpected result")
		}
	*/
}

func Test_equal_nils_v2(t *testing.T) {
	/*
		var err error = nil
		var errForClient *ErrorForClient = err // cannot be assigned!
	*/
}

// -----------------------------------------------------------------------

func Test_errors_Unwrap(t *testing.T) {
	err := errors.Unwrap(nil)
	if err != nil {
		t.Error("Unexpected result")
	}
}
