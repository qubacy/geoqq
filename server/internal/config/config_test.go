package config

import (
	"fmt"
	"os"
	"runtime/debug"
	"testing"
)

// experiments
// -----------------------------------------------------------------------

func Test_os_Executable(t *testing.T) {
	executable, err := os.Executable()
	if err != nil {
		t.Error(err)
	}

	fmt.Println(executable)
}

func Test_debug_BuildInfo(t *testing.T) {
	buildInfo, ok := debug.ReadBuildInfo()
	if !ok {
		t.Error()
	}

	fmt.Println(buildInfo.GoVersion)
}
