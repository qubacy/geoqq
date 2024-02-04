package dto

type UpdateProfileInp struct {
	Description *string
	Avatar      *Avatar

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

type Avatar struct {
	Ext     int
	Content string
}
