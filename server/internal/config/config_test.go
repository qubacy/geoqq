package config

import (
	"fmt"
	"os"
	"testing"
)

func Test_os_Executable(t *testing.T) {
	executable, err := os.Executable()
	if err != nil {
		t.Error(err)
	}

	fmt.Println(executable)
}
