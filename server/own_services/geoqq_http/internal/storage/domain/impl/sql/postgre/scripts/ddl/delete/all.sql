-- indexes
-- ---------------------------------------------------------------------------
DROP INDEX IF EXISTS unique_mate_ids_comb;
DROP INDEX IF EXISTS unique_mate_chat_ids_comb;

-- functions
-- ---------------------------------------------------------------------------
DROP FUNCTION IF EXISTS geodistance;

-- tables
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS 
	"GeoMessage",
	"DeletedMateChat",
	"MateMessage",
	"MateChat",
	"MateRequest",
	"Mate",
	"DeletedUser",
	"UserOptions",
	"UserDetails",
	"UserLocation",
	"Avatar",
	"UserEntry"
	CASCADE;

