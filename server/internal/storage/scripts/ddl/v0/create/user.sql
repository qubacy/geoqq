CREATE TABLE "UserEntry"
(
    "Id" BIGSERIAL PRIMARY KEY,
    "Username" CHARACTER VARYING(128) UNIQUE,
    "HashPassword" VARCHAR(512) NOT NULL,
    "HashUpdToken" VARCHAR(512) NOT NULL,
    "SignUpTime" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "SignInTime" TIMESTAMP WITHOUT TIME ZONE NULL
);

CREATE TABLE "UserLocation"
(
    "UserId" BIGINT,
    "Longitude" double precision,
    "Latitude" double precision,
    "Time" TIMESTAMP WITHOUT TIME ZONE NULL,
    FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "UserDetails"
(
    "UserId" BIGINT,
    "Description" CHARACTER VARYING(1024) UNIQUE,
    "Avatar" VARCHAR(1024) NOT NULL,
	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "UserOptions"
(
    "UserId" BIGINT,
    "Privacy" INTEGER,
	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);
