CREATE TABLE "UserEntry"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "Username" CHARACTER VARYING(128) UNIQUE NOT NULL,
    "HashPassword" VARCHAR(512) NOT NULL,
    "HashUpdToken" VARCHAR(512) NOT NULL,
    "SignUpTime" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "SignInTime" TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE "UserLocation"
(
    "UserId" BIGINT NOT NULL,
    "Longitude" double precision NULL,
    "Latitude" double precision NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NULL,

    FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "UserDetails"
(
    "UserId" BIGINT NOT NULL,
    "Description" CHARACTER VARYING(4096) UNIQUE NOT NULL,
    "AvatarId" BIGINT NULL,

	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("AvatarId") REFERENCES "Avatar"("Id")
);

CREATE TABLE "UserOptions"
(
    "UserId" BIGINT NOT NULL,
    "Privacy" INTEGER NOT NULL,
	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);
