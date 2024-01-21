package impl

import (
	"fmt"
	"os"
	"testing"

	"github.com/o1egl/govatar"
)

func Test_New(t *testing.T) {

}

// experiments
// -----------------------------------------------------------------------

func Test_govatar_GenerateFile(t *testing.T) {
	err := govatar.GenerateFile(randomGender(), "test.png") // !
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
