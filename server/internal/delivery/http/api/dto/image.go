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
