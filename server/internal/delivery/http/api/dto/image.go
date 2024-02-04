package dto

// GET /api/image/{id}
// -----------------------------------------------------------------------

type ImageByIdRes struct {
	Image
}

// GET /api/image
// -----------------------------------------------------------------------

type ImagesReq struct {
	AccessToken string    `json:"access-token"` // ?
	Ids         []float64 `json:"ids"`
}

func (self *ImagesReq) GetIdsAsSliceOfUint64() []uint64 {
	ids := []uint64{}
	for i := range self.Ids {
		ids = append(ids, uint64(self.Ids[i]))
	}
	return ids
}

// -----------------------------------------------------------------------
// see pkg file... or new from?

type ImagesRes struct {
	ImageList []*Image `json:"images"`
}

type Image struct {
	Id        float64 `json:"id"`
	Extension float64 `json:"ext"`
	Content   string  `json:"content"` // <--- base64-string
}
