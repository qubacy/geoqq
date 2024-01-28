package impl

import (
	"fmt"
	"os"
	"testing"

	"github.com/google/uuid"
	"github.com/o1egl/govatar"
)

func Test_NewFile(t *testing.T) {
	ag, err := NewAvatarGenerator()
	if err != nil {
		t.Error(err)
	}

	ag.NewFile("./testResult", uuid.NewString())
}

// experiments
// -----------------------------------------------------------------------

func Test_govatar_GenerateFile(t *testing.T) {
	// works if the directory exists!
	err := govatar.GenerateFile(randomGender(), "./testResult/test.png") // !
	if err != nil {
		t.Error()
	}
}

func Test_os_Getwd(t *testing.T) {
	pwd, err := os.Getwd()
	if err != nil {
		t.Error()
	}
	fmt.Println(pwd)
}

func Test_os_Executable(t *testing.T) {
	path, err := os.Executable()
	if err != nil {
		t.Error()
	}
	fmt.Println(path)
}

func Test_os_MkdirAll(t *testing.T) {
	err := os.MkdirAll("./test/test.png", os.ModeDir) // test.png will be a directory!
	if err != nil {
		t.Error(err)
	}
}
