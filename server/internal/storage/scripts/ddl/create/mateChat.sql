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