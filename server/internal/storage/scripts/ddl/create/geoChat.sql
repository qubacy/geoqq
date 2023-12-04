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
