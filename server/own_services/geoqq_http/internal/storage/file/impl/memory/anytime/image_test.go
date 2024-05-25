package anytime

import (
	"common/pkg/file"
	hashImpl "common/pkg/hash/basic"
	"context"
	"flag"
	"fmt"
	"net/http"
	"os"
	"path/filepath"
	"regexp"
	"strconv"
	"strings"
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

func Test_fileNameFromId(t *testing.T) {
	storage, err := createStorage()
	if err != nil {
		t.Error(err)
	}

	fileName := storage.fileNameFromId(1) // calc hash
	fmt.Println("File name:", fileName)
}

func Test_wholeFileAndDirNames(t *testing.T) {
	storage, err := createStorage()
	if err != nil {
		t.Error(err)
	}

	fileName, dirName, err := storage.wholeFileAndDirNames(1)
	if err != nil {
		t.Error(err)
	}

	fmt.Println("File name:", fileName)
	fmt.Println("Dir name:", dirName)
}

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

// -----------------------------------------------------------------------

func Test_SaveImage_png(t *testing.T) {
	storage, err := createStorage()
	if err != nil {
		t.Error(err)
	}

	bytes, err := os.ReadFile("./testData/imageInBase64/png/2.txt")
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

func Test_SaveImage_jpg(t *testing.T) {
	storage, err := createStorage()
	if err != nil {
		t.Error(err)
	}

	bytes, err := os.ReadFile("./testData/imageInBase64/jpg/1.txt")
	if err != nil {
		t.Error(err)
	}

	image := file.Image{
		Id:        1,
		Extension: file.Jpg,
		Content:   string(bytes),
	}

	err = storage.SaveImage(context.Background(), &image)
	if err != nil {
		t.Error(err)
	}
}

// work with some images
// -----------------------------------------------------------------------

func Test_SaveImages(t *testing.T) {
	pathsToBase64Images := []string{
		"./testData/imageInBase64/jpg/1.txt",
		"./testData/imageInBase64/png/2.txt",
		//...
	}

	for _, onePath := range pathsToBase64Images {
		ext, id := extractImageExtAndId(t, onePath)
		fmt.Printf("Image with ext %v has id %v", ext, id)

		bytes, err := os.ReadFile(onePath)
		if err != nil {
			t.Error(err)
		}

		// ***

		image := file.Image{
			Id:        id,
			Extension: ext,
			Content:   string(bytes),
		}

		storage, err := createStorage()
		if err != nil {
			t.Error(err)
		}
		err = storage.SaveImage(context.Background(), &image)
		if err != nil {
			t.Error(err)
		}
	}
}

// -----------------------------------------------------------------------

func Test_LoadImage(t *testing.T) {
	Test_SaveImage_png(t)

	// ***

	storage, err := createStorage()
	if err != nil {
		t.Error(err)
	}

	image, err := storage.LoadImage(
		context.Background(), 1)
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

// TODO: make tests?
func extractImageExtAndId(t *testing.T, imagePath string) (file.ImageExt, uint64) {
	re, err := regexp.Compile(`\/(png|jpg)\/(\d+)\.txt$`)
	if err != nil {
		t.Error(err)
	}

	allStringSubs := re.FindAllStringSubmatch(imagePath, -1)[0]
	id, err := strconv.ParseUint(allStringSubs[2], 10, 64)
	if err != nil {
		t.Error(err)
	}

	return file.MakeImageExtFromString(allStringSubs[1]), id
}

// experiments
// -----------------------------------------------------------------------

func Test_filepath_Glob(t *testing.T) {
	files, err := filepath.Glob(`.\testData\imageInBase64\jpg\1.?*`)
	if err != nil {
		t.Error(err)
	}
	fmt.Printf("%v\n", files)
}

// -----------------------------------------------------------------------

func Test_regexp_FindAllString(t *testing.T) {
	re, err := regexp.Compile(`\/(png|jpg)\/(\d+)\.txt$`)
	if err != nil {
		t.Error(err)
	}

	allStrings := re.FindAllString(`./testData/imageInBase64/jpg/1.txt`, -1)
	fmt.Println("All strings:", allStrings)
}

func Test_regexp_FindAllStringSubmatch(t *testing.T) {
	re, err := regexp.Compile(`\/(png|jpg)\/(\d+)\.txt$`)
	if err != nil {
		t.Error(err)
	}

	allStringSubs := re.FindAllStringSubmatch(`./testData/imageInBase64/jpg/1.txt`, -1)
	fmt.Println("String sub count:", len(allStringSubs[0]))
	fmt.Println("All string subs:", allStringSubs[0]) // ?
}

// -----------------------------------------------------------------------

func Test_strings_find(t *testing.T) {
	value := `testData\imageInBase64\jpg\1.txt`
	index := strings.LastIndex(value, ".")
	fmt.Println(index)

	ext := value[index+1:]
	if ext != "txt" {
		t.Error()
	}
}

// -----------------------------------------------------------------------

func Test_http_DetectContentType_nil(t *testing.T) {
	contentType := http.DetectContentType(nil)
	fmt.Println("Content type:", contentType)
}

func Test_http_DetectContentType(t *testing.T) {
	fn := `testData\image\png\1.png`
	content, err := os.ReadFile(fn)
	if err != nil {
		t.Error()
	}

	contentType := http.DetectContentType(content)
	fmt.Println("Content type:", contentType)

	if contentType != "image/png" {
		t.Error("Unexpected content type")
	}
}
