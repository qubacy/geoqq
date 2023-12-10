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
