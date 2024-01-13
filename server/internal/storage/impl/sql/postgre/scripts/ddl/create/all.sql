-- resource
-- ---------------------------------------------------------------------------
CREATE TABLE "Avatar" 
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "Filename" VARCHAR(1024) NOT NULL
);

-- user
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

CREATE TABLE "UserLocation"
(
    "UserId" BIGINT NOT NULL,
    "Longitude" double precision NOT NULL,
    "Latitude" double precision NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NULL,

    FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "UserDetails"
(
    "UserId" BIGINT NOT NULL,
    "Description" CHARACTER VARYING(4096) UNIQUE NOT NULL DEFAULT '',
    "AvatarId" BIGINT NULL,

	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("AvatarId") REFERENCES "Avatar"("Id")
);

CREATE TABLE "UserOptions"
(
    "UserId" BIGINT NOT NULL,
    "HitMeUp" INTEGER NOT NULL,
	FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);


-- mate
-- ---------------------------------------------------------------------------
CREATE TABLE "Mate"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "FirstUserId" BIGINT NOT NULL,
    "SecondUserId" BIGINT NOT NULL,
    
    FOREIGN KEY ("FirstUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("SecondUserId") REFERENCES "UserEntry"("Id")
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
    
    FOREIGN KEY ("FirstUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("SecondUserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "MateMessage"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "MateChatId" BIGINT NOT NULL,
    "FromUserId" BIGINT NOT NULL,
    "Text" VARCHAR(4096) NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "Read" BOOLEAN NOT NULL,
    
    FOREIGN KEY ("MateChatId") REFERENCES "MateChat"("Id"),
    FOREIGN KEY ("FromUserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "DeletedMateChat"
(
    "ChatId" BIGINT NOT NULL,
    "UserId" BIGINT NOT NULL,
    
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


