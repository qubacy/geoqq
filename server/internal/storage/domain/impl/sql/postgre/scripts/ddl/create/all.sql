-- user_and_avatar
-- ---------------------------------------------------------------------------
CREATE TABLE "UserEntry"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "Username" CHARACTER VARYING(128) UNIQUE NOT NULL,
    "HashPassword" VARCHAR(512) NOT NULL,
    "HashUpdToken" VARCHAR(512) NOT NULL DEFAULT '',
    "SignUpTime" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "SignInTime" TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE "Avatar" 
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "UserId" BIGINT NULL, -- who added the image
    
    "Label" VARCHAR(128) NULL, -- to search for special images
    "Time" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "Hash" VARCHAR(512) NOT NULL,

    FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "UserLocation"
(
    "UserId" BIGINT NOT NULL,
    "Longitude" double precision NOT NULL,
    "Latitude" double precision NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE, -- as usage flag!

    FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "UserDetails"
(
    "UserId" BIGINT NOT NULL,
    "Description" CHARACTER VARYING(4096) NOT NULL DEFAULT '',
    "AvatarId" BIGINT NOT NULL,
    
    "Gender" INT NULL,
    "Age" INT NULL,

	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("AvatarId") REFERENCES "Avatar"("Id")
);

CREATE TABLE "UserOptions"
(
    "UserId" BIGINT NOT NULL,
    "HitMeUp" INTEGER NOT NULL,
    
	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "DeletedUser"
(
    -- "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "UserId" BIGINT NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

-- mate
-- ---------------------------------------------------------------------------
CREATE TABLE "Mate"
(
    -- why is there an identifier here?
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,

    "FirstUserId" BIGINT NOT NULL,
    "SecondUserId" BIGINT NOT NULL,

    -- if use an index, then don't need it!
    -- UNIQUE ("FirstUserId", "SecondUserId"),

    -- does not work..?
    -- UNIQUE ("SecondUserId", "FirstUserId"), 
    
    FOREIGN KEY ("FirstUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("SecondUserId") REFERENCES "UserEntry"("Id")
);
create unique index unique_mate_ids_comb
    on "Mate"(
        greatest("FirstUserId", "SecondUserId"),
        least("FirstUserId", "SecondUserId")
        );

CREATE TABLE "MateRequest"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "FromUserId" BIGINT NOT NULL,
    "ToUserId" BIGINT NOT NULL,
    "RequestTime" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "ResponseTime" TIMESTAMP WITHOUT TIME ZONE,
    "Result" smallint NOT NULL,
    
    FOREIGN KEY ("FromUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("ToUserId") REFERENCES "UserEntry"("Id")
);


-- mateChat
-- ---------------------------------------------------------------------------
CREATE TABLE "MateChat"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "FirstUserId" BIGINT NOT NULL,
    "SecondUserId" BIGINT NOT NULL,

    -- if use an index, then don't need it!
    -- UNIQUE ("FirstUserId", "SecondUserId"),

    -- does not work..?
    -- UNIQUE ("SecondUserId", "FirstUserId"), 
    
    FOREIGN KEY ("FirstUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("SecondUserId") REFERENCES "UserEntry"("Id")
);
create unique index unique_mate_chat_ids_comb
    on "MateChat"(
        greatest("FirstUserId", "SecondUserId"),
        least("FirstUserId", "SecondUserId")
        );

CREATE TABLE "MateMessage"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "MateChatId" BIGINT NOT NULL,
    "FromUserId" BIGINT NOT NULL,
    "Text" VARCHAR(4096) NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "Read" BOOLEAN NOT NULL DEFAULT FALSE,
    
    FOREIGN KEY ("MateChatId") REFERENCES "MateChat"("Id"),
    FOREIGN KEY ("FromUserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "DeletedMateChat"
(
    "ChatId" BIGINT NOT NULL,
    "UserId" BIGINT NOT NULL,
    UNIQUE ("ChatId", "UserId"),
    
    FOREIGN KEY ("ChatId") REFERENCES "MateChat"("Id"),
    FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);


-- geoChat
-- ---------------------------------------------------------------------------
CREATE TABLE "GeoMessage"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "FromUserId" BIGINT NOT NULL,
    "Text" VARCHAR(4096) NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NULL,
    
    "Longitude" double precision NOT NULL,
    "Latitude" double precision NOT NULL,
    
    FOREIGN KEY ("FromUserId") REFERENCES "UserEntry"("Id")
);


-- functions/haversine
-- ---------------------------------------------------------------------------
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


