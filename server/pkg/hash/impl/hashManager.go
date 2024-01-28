package impl

import (
	"crypto"
	"crypto/md5"
	"crypto/sha1"
	"encoding/hex"
	"geoqq/pkg/utility"
	"strings"
)

type HashType uint

const (
	MD5  HashType = HashType(crypto.MD5)
	SHA1 HashType = HashType(crypto.SHA1)
	//...
)

func StrToHashType(value string) (HashType, error) {
	value = strings.ToUpper(value)
	if value == "MD5" {
		return MD5, nil
	} else if value == "SHA1" {
		return SHA1, nil
	}

	return 0, ErrUnknownHashType
}

// -----------------------------------------------------------------------

type HashManager struct {
	hashType HashType
}

func NewHashManager(hashType HashType) (*HashManager, error) {
	if hashType != MD5 && hashType != SHA1 {
		return nil, ErrUnknownHashType
	}

	return &HashManager{
		hashType: hashType,
	}, nil
}

// -----------------------------------------------------------------------

func (h *HashManager) NewFromString(value string) (string, error) {
	return h.NewFromBytes([]byte(value))
}

func (h *HashManager) NewFromBytes(bytes []byte) (string, error) {
	bytes, err := sum(h.hashType, bytes)
	if err != nil {
		return "", utility.NewFuncError(h.NewFromString, err)
	}

	hexHashValue := hex.EncodeToString(bytes)
	return strings.ToLower(hexHashValue), nil // !
}

// private
// -----------------------------------------------------------------------

func sum(hashType HashType, valueBytes []byte) ([]byte, error) {
	switch hashType {
	case MD5:
		hashedFixedBytes := md5.Sum(valueBytes)
		return hashedFixedBytes[:], nil
	case SHA1:
		hashedFixedBytes := sha1.Sum(valueBytes)
		return hashedFixedBytes[:], nil
	}

	return nil, ErrUnknownHashType
}
