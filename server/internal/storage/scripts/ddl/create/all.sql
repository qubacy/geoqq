-- user
-- ---------------------------------------------------------------------------
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


-- mate
-- ---------------------------------------------------------------------------
CREATE TABLE "Mate"
(
    "Id" BIGSERIAL PRIMARY KEY,
    "FirstUserId" BIGINT,
    "SecondUserId" BIGINT,
    
    FOREIGN KEY ("FirstUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("SecondUserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "MateRequest"
(
    "Id" BIGSERIAL PRIMARY KEY,
    "FromUserId" BIGINT,
    "ToUserId" BIGINT,
    "RequestTime" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "ResponseTime" TIMESTAMP WITHOUT TIME ZONE,
    "Result" VARCHAR(512) NOT NULL,
    
    FOREIGN KEY ("FromUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("ToUserId") REFERENCES "UserEntry"("Id")
);

-- mateChat
-- ---------------------------------------------------------------------------
CREATE TABLE "MateChat"
(
    "Id" BIGSERIAL PRIMARY KEY,
    "FirstUserId" BIGINT,
    "SecondUserId" BIGINT,
    
    FOREIGN KEY ("FirstUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("SecondUserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "MateMessage"
(
    "Id" BIGSERIAL PRIMARY KEY,
    "MateChatId" BIGINT,
    "FromUserId" BIGINT,
    "Text" VARCHAR(4096) NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "Read" BOOLEAN,
    
    FOREIGN KEY ("MateChatId") REFERENCES "MateChat"("Id"),
    FOREIGN KEY ("FromUserId") REFERENCES "UserEntry"("Id")
);

CREATE TABLE "DeletedMateChat"
(
    "ChatId" BIGINT,
    "UserId" BIGINT,
    
    FOREIGN KEY ("ChatId") REFERENCES "MateChat"("Id"),
    FOREIGN KEY ("UserId") REFERENCES "UserEntry"("Id")
);

-- geoChat
-- ---------------------------------------------------------------------------
CREATE TABLE "GeoMessage"
(
    "Id" BIGSERIAL PRIMARY KEY,
    "FromUserId" BIGINT,
    "Text" VARCHAR(4096) NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NULL,
    "Longitude" double precision,
    "Latitude" double precision,
    
    FOREIGN KEY ("FromUserId") REFERENCES "UserEntry"("Id")
);


