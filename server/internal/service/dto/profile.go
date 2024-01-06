package dto

type UpdateProfileInp struct {
	Description *string
	Avatar      *string

	Privacy  *Privacy
	Security *Security
}

// every field is optional...
type Privacy struct {
	HitMeUp *int
}

type Security struct {
	Password    string
	NewPassword string
}
