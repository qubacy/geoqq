package dto

// GET /api/image/{id}
// -----------------------------------------------------------------------

type ImageByIdRes struct {
	Image
}

// GET /api/image
// -----------------------------------------------------------------------

type SomeImagesReq struct {
	AccessToken string    `json:"access-token" binding:"required"` // ?
	Ids         []float64 `json:"ids" binding:"required"`
}

func (s *SomeImagesReq) GetIdsAsSliceOfUint64() []uint64 {
	ids := []uint64{}
	for i := range s.Ids {
		ids = append(ids, uint64(s.Ids[i]))
	}
	return ids
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
}
