package anytime

import (
	"context"
	"flag"
	"fmt"
	"geoqq/pkg/file"
	hashImpl "geoqq/pkg/hash/impl"
	"os"
	"testing"

	"github.com/spf13/viper"
)

func TestMain(m *testing.M) {
	flag.Parse()

	setUp()
	exitCode := m.Run()
	tearDown()

	os.Exit(exitCode)
}

func setUp() {
	const (
		rootDirName   = "./testResult/"
		avatarDirName = "./testResult/avatar"
	)

	viper.Set("storage.file.root", rootDirName)
	viper.Set("storage.file.avatar", avatarDirName)

	// ***

	fmt.Println("Try remove all:", rootDirName)
	err := os.RemoveAll(viper.GetString("storage.file.avatar"))
	if err != nil {
		fmt.Println(err)
	}
}

func tearDown() {}

// tests
// -----------------------------------------------------------------------

func Test_createDirsIfNeeded(t *testing.T) {
	err := createDirsIfNeeded(viper.GetString("storage.file.avatar") + "/b1")
	if err != nil {
		t.Error(err)
	}
}

func Test_existsFileOrDirectory(t *testing.T) {
	exists, err := existsFileOrDir("./storage.go")
	if err != nil {
		t.Error(err)
	}
	if !exists {
		t.Error()
	}

	// ***

	exists, err = existsFileOrDir("./unknown.go")
	if err != nil {
		t.Error(err)
	}
	if exists {
		t.Error()
	}
}

func Test_SaveImage(t *testing.T) {
	storage, err := createStorage()
	if err != nil {
		t.Error(err)
	}

	bytes, err := os.ReadFile("./testData/imageInBase64/png/1.txt")
	if err != nil {
		t.Error(err)
	}

	image := file.Image{
		Id:        1,
		Extension: file.Png,
		Content:   string(bytes),
	}

	err = storage.SaveImage(context.Background(), &image)
	if err != nil {
		t.Error(err)
	}
}

func Test_LoadImage(t *testing.T) {
	Test_SaveImage(t)

	// ***

	storage, err := createStorage()
	if err != nil {
		t.Error(err)
	}

	image, err := storage.LoadImage(
		context.Background(), 1, file.Png)
	if err != nil {
		t.Error(err)
	}

	fmt.Println(len(image.Content))
}

// private
// -----------------------------------------------------------------------

func createStorage() (*Storage, error) {
	hashManager, err := hashImpl.NewHashManager(hashImpl.MD5)
	if err != nil {
		return nil, err
	}

	storage, err := NewStorage(Dependencies{
		AvatarDirName: viper.GetString("storage.file.avatar"),
		HashManager:   hashManager,
	})
	if err != nil {
		return nil, err
	}

	return storage, nil
}
