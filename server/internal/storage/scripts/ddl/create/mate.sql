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