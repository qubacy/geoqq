CREATE TABLE "UserEntry"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "Username" CHARACTER VARYING(128) UNIQUE NOT NULL,
    "HashPassword" VARCHAR(512) NOT NULL,
    "HashUpdToken" VARCHAR(512) NOT NULL DEFAULT '',
    "SignUpTime" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "SignInTime" TIMESTAMP WITHOUT TIME ZONE
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

	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("AvatarId") REFERENCES "Avatar"("Id")
);

CREATE TABLE "UserOptions"
(
    "UserId" BIGINT NOT NULL,
    "HitMeUp" INTEGER NOT NULL,
    
	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);
