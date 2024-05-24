package geoDistance

import "errors"

var (
	ErrWrongLongitude = errors.New("wrong longitude")
	ErrWrongLatitude  = errors.New("wrong latitude")
)

func ValidateLat(latitude float64) error {
	if latitude < -90 || latitude > +90 {
		return ErrWrongLatitude
	}
	return nil
}

func ValidateLon(longitude float64) error {
	if longitude < -180 || longitude > +180 {
		return ErrWrongLongitude
	}
	return nil
}

func ValidateLatAndLon(longitude, latitude float64) error {
	/*
		Долгота (longitude) от −180° до +180°
		Широта (latitude) от −90° до +90°
	*/

	// Долгота
	if err := ValidateLon(longitude); err != nil {
		return err
	}

	// Широта
	if err := ValidateLat(latitude); err != nil {
		return err
	}

	return nil
}
