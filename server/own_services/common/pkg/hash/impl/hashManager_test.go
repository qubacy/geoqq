package impl

import (
	"common/pkg/utility"
	"crypto"
	"crypto/md5"
	"crypto/sha1"
	"encoding/hex"
	"fmt"
	"strings"
	"testing"
)

// tests
// -----------------------------------------------------------------------

func Test_HashManager_ctor(t *testing.T) {
	_, err := NewHashManager(MD5)
	if err != nil {
		t.Error(err)
	}
}

func Test_HashManager_MD5_New(t *testing.T) {
	hashManager, err := NewHashManager(MD5)
	if err != nil {
		t.Error(err)
	}

	hashValue, err := hashManager.NewFromString("Test")
	if err != nil {
		t.Error(err)
	}

	fmt.Println(hashValue)
}

func Test_HashManager_SHA1_New(t *testing.T) {
	hashManager, err := NewHashManager(SHA1)
	if err != nil {
		t.Error(err)
	}

	hashValue, err := hashManager.NewFromString("Test")
	if err != nil {
		t.Error(err)
	}

	fmt.Println(hashValue)
}

// benchmarks
// -----------------------------------------------------------------------

func Benchmark_HashManager_SHA1_New(b *testing.B) {
	hashManager, err := NewHashManager(SHA1)
	if err != nil {
		b.Error(err)
	}

	for i := 0; i < b.N; i++ {
		_, err := hashManager.NewFromString(
			utility.RandomString(
				utility.RandomInt(5, 100)))

		if err != nil {
			b.Error()
		}
	}
}

// experiments
// -----------------------------------------------------------------------

func Test_crypto_md5(t *testing.T) {
	hashType := crypto.MD5
	fmt.Println(hashType)
	fmt.Println(hashType.Available())
	fmt.Println(hashType.Size())
}

func Test_crypto_sha1(t *testing.T) {
	hashType := crypto.SHA1
	fmt.Println(hashType)
	fmt.Println(hashType.Size())

	// if package is imported, then it's true!
	fmt.Println(hashType.Available())
}

func Test_md5_Sum(t *testing.T) {
	hash := md5.Sum([]byte("Test"))
	hexHash := hex.EncodeToString(hash[:])
	hexHash = strings.ToUpper(hexHash)
	fmt.Println(hexHash)
}

func Test_sha1_Sum(t *testing.T) {
	hash := sha1.Sum([]byte("Test"))
	hexHash := hex.EncodeToString(hash[:])
	hexHash = strings.ToUpper(hexHash)
	fmt.Println(hexHash)
}
