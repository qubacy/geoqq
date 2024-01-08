package impl

import (
	"crypto"
	"crypto/md5"
	"crypto/sha1"
	"encoding/hex"
	"geoqq/pkg/hash"
	"geoqq/pkg/utility"
	"strings"
)

type HashType uint

const (
	MD5  HashType = HashType(crypto.MD5)
	SHA1 HashType = HashType(crypto.SHA1)
	//...
)

type HashManager struct {
	hashType HashType
}

func NewHashManager(hashType HashType) (*HashManager, error) {
	if hashType != MD5 && hashType != SHA1 {
		return nil, hash.ErrUnknownHashType
	}

	return &HashManager{
		hashType: hashType,
	}, nil
}

func (h *HashManager) New(value string) (string, error) {
	bytes, err := sum(h.hashType, value)
	if err != nil {
		return "", utility.CreateCustomError(h.New, err)
	}

	hexHashValue := hex.EncodeToString(bytes)
	return strings.ToUpper(hexHashValue), nil
}

// private
// -----------------------------------------------------------------------

func sum(hashType HashType, value string) ([]byte, error) {
	valueBytes := []byte(value)

	switch hashType {
	case MD5:
		hashedFixedBytes := md5.Sum(valueBytes)
		return hashedFixedBytes[:], nil
	case SHA1:
		hashedFixedBytes := sha1.Sum(valueBytes)
		return hashedFixedBytes[:], nil
	}

	return nil, hash.ErrUnknownHashType
}
