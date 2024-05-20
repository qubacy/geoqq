package app

import (
	"fmt"
	"os"
	"path"
	"path/filepath"
	"runtime"
	"strings"
	"testing"
)

func Test_os_Getwd(t *testing.T) {
	wd, err := os.Getwd()
	if err != nil {
		t.Error(err)
	}

	fmt.Printf("%v", wd)
}

func Test_path_Split(t *testing.T) {
	wd, _ := os.Getwd()
	dir, file := path.Split(wd)
	fmt.Println("File: ", file)
	fmt.Println("Dir: ", dir)
}

func Test_filepath_SplitList(t *testing.T) {
	wd, _ := os.Getwd()
	parts := filepath.SplitList(wd)
	for i := range parts {
		fmt.Println("Parts Item: ", parts[i])
	}
}

func Test_strings_Split(t *testing.T) {
	{
		wd, _ := os.Getwd()
		fmt.Println("Wd:", wd)

		parts := strings.Split(wd, string(os.PathSeparator))
		for i := range parts {
			fmt.Println("Parts Item: ", parts[i])
		}
	}

	{
		file := "d:/education/my/pl_go/common/server/app/app.go"
		fmt.Println("File:", file)

		parts := strings.Split(file, "/")
		for i := range parts {
			fmt.Println("Parts Item: ", parts[i])
		}
	}
}

func Test_ioutil_ReadDir(t *testing.T) {
	files, err := os.ReadDir(".")
	if err != nil {
		t.Error(err)
	}

	for _, file := range files {
		fmt.Println(file.Name(), file.IsDir())
	}
}

func Test_os_PathSeparator(t *testing.T) {
	fmt.Println("Path Separator: ",
		string(os.PathSeparator))
}

func Test_runtime_Caller(t *testing.T) {
	pc, file, line, ok := runtime.Caller(3)
	if !ok {
		t.Error("Runtime Caller Is Not OK!")
	}

	fmt.Println("Pc:", pc)
	fmt.Println("File:", file)
	fmt.Println("Line:", line)
}
