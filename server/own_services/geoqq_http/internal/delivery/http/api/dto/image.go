package dto

import serviceDto "geoqq_http/internal/service/dto"

// GET /api/image/{id}
// -----------------------------------------------------------------------

type ImageByIdRes struct {
	Image
}

// GET /api/image
// -----------------------------------------------------------------------

type SomeImagesReq struct {
	Ids []float64 `json:"ids" binding:"required"`
}

// -----------------------------------------------------------------------

// see pkg file... or new from?

type SomeImagesRes struct {
	ImageList []*Image `json:"images"`
}

type Image struct {
	Id        float64 `json:"id"`
	Extension float64 `json:"ext"`
	Content   string  `json:"content"` // <--- base64-string

	// or composition with struct `ImageWithoutId`?
}

// POST /api/image
// -----------------------------------------------------------------------

type ImagePostReq struct {
	Image ImageWithoutId `json:"image"`
}

func (s *ImagePostReq) ToDynamicInp() *serviceDto.ImageForAddToUserInp {
	return &serviceDto.ImageForAddToUserInp{
		Ext:     int(s.Image.Ext),
		Content: s.Image.Content,
	}
}

type ImageWithoutId struct {
	Ext     float64 `json:"ext" binding:"required"`
	Content string  `json:"content" binding:"required"`
}

type ImagePostRes struct {
	Id float64 `json:"id"`
}

func MakeImagePostRes(id uint64) ImagePostRes {
	return ImagePostRes{
		Id: float64(id),
	}
}
