package config

import (
	"errors"
	"fmt"
	"os"
	"runtime/debug"
	"strings"
	"testing"
)

func Test_existsConfigFile(t *testing.T) {
	exists := existsConfigFile(".")
	fmt.Println("Exists:", exists)
}

func Test_wholeConfigFileName(t *testing.T) {
	whole := wholeConfigFileName(".")
	fmt.Println(whole)
}

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

	fmt.Println("GoVersion:", buildInfo.GoVersion)
	fmt.Println("Path:", buildInfo.Path)
	fmt.Println("Main:", buildInfo.Main)
	fmt.Println("Settings:", buildInfo.Settings)
}

func Test_os_Stat(t *testing.T) {
	if _, err := os.Stat("./config.yml"); errors.Is(err, os.ErrNotExist) {
		t.Error()
	}
}

func Test_strings_Join(t *testing.T) {
	wholeConfigFileName := strings.Join([]string{
		ConfigFileName,
		ConfigFileExt,
	}, ".")

	fmt.Println(wholeConfigFileName)
}
