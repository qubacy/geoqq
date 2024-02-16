CREATE TABLE "Mate"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "FirstUserId" BIGINT NOT NULL,
    "SecondUserId" BIGINT NOT NULL,
    UNIQUE ("FirstUserId", "SecondUserId"),
    
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