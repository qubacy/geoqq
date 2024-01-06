package dto

// GET /api/image/{id}
// -----------------------------------------------------------------------

type ImageByIdRes struct {
	Image
}

// GET /api/image
// -----------------------------------------------------------------------

type ImagesReq struct {
	Ids []float64 `json:"ids"`
}

type ImagesRes struct {
	Images []Image `json:"images"`
}

type Image struct {
	Id      float64 `json:"id"`
	Content string  `json:"content"` // <--- base64-string
}
