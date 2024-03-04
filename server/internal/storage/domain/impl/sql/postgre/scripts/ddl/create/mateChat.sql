CREATE TABLE "MateChat"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "FirstUserId" BIGINT NOT NULL,
    "SecondUserId" BIGINT NOT NULL,

    -- if use an index, then don't need it!
    -- UNIQUE ("FirstUserId", "SecondUserId"),

    -- does not work..?
    -- UNIQUE ("SecondUserId", "FirstUserId"), 
    
    FOREIGN KEY ("FirstUserId") REFERENCES "UserEntry"("Id"),
    FOREIGN KEY ("SecondUserId") REFERENCES "UserEntry"("Id")
);
create unique index unique_mate_chat_ids_comb
    on "MateChat"(
        greatest("FirstUserId", "SecondUserId"),
        least("FirstUserId", "SecondUserId")
        );

CREATE TABLE "MateMessage"
(
    "Id" BIGSERIAL PRIMARY KEY NOT NULL,
    "MateChatId" BIGINT NOT NULL,
    "FromUserId" BIGINT NOT NULL,
    "Text" VARCHAR(4096) NOT NULL,
    "Time" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "Read" BOOLEAN NOT NULL DEFAULT FALSE,
    
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