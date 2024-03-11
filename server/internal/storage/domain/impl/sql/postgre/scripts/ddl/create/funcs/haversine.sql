CREATE OR REPLACE FUNCTION geodistance( 
    alat double precision, 
    alng double precision, 
    blat double precision, 
    blng double precision
    ) RETURNS double precision AS $BODY$
SELECT asin(
  sqrt(
    sin(radians($3-$1)/2)^2 +
    sin(radians($4-$2)/2)^2 *
    cos(radians($1)) *
    cos(radians($3))
  )
) * 2 * 6371 AS distance;
$BODY$ LANGUAGE sql IMMUTABLE COST 100;


DROP FUNCTION geodistance;