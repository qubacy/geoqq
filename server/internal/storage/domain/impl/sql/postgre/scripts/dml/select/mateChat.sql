SELECT * FROM "MateChat";
SELECT * FROM "UserEntry";

UPDATE "MateChat" SET "FirstUserId" = 13
WHERE "Id" = 1;

-- -----------------------------------------------------------------------

SELECT case
           when COUNT(*) = 1 then true
           else false
       end As "Has"
FROM "MateChat"
WHERE "Id" = 1;

-- -----------------------------------------------------------------------

SELECT case
           when COUNT(*) = 1 then true
           else false
       end as "Available"
FROM "MateChat"
WHERE "Id" = 1
    AND ("FirstUserId" = 13
         OR "SecondUserId" = 13)
    AND NOT EXISTS
        (SELECT
         FROM "DeletedMateChat"
         WHERE "ChatId" = "Id"
             AND "UserId" = 13);

-- Not optimal!
-- -----------------------------------------------------------------------

SELECT "MateChat"."Id" AS "Id",
       case
           when "FirstUserId" = 1 then "SecondUserId"
           else "FirstUserId"
       end as "UserId",

    (SELECT COUNT(*)
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
         AND "Read" = false) AS "NewMessageCount",

    (SELECT "Id"
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
     ORDER BY "Time" DESC
     LIMIT 1) AS "LastMessageId",

    (SELECT "Text"
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
     ORDER BY "Time" DESC
     LIMIT 1) AS "LastMessageText",

    (SELECT "Time"
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
     ORDER BY "Time" DESC
     LIMIT 1) AS "LastMessageTime",

    (SELECT "FromUserId"
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
     ORDER BY "Time" DESC
     LIMIT 1) AS "LastMessageUserId"
FROM "MateChat";

-- GetMateChatsForUser
-- -----------------------------------------------------------------------

SELECT * FROM "UserEntry"
ORDER BY "Id" DESC;

SELECT * FROM "MateChat";
SELECT * FROM "DeletedMateChat";
SELECT * FROM "Mate";

SELECT * FROM "MateRequest";

DELETE FROM "DeletedMateChat";
INSERT INTO "DeletedMateChat" VALUES (1, 14);

SELECT * FROM "MateMessage";

-- -----------------------------------------------------------------------

SELECT * FROM "MateChat";

WITH "srcUserId" AS (VALUES (2))
SELECT "MateChat"."Id" AS "Id",
       case
           when "FirstUserId" = (table "srcUserId") 
                then "SecondUserId"
           else "FirstUserId"
       end as "UserId",

    (SELECT COUNT(*) FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id" 
        AND "MateMessage"."FromUserId" != (table "srcUserId")
        AND "Read" = false) AS "NewMessageCount",

       case
           when "LastMessage"."Id" is NULL then false
           else true
       end as "Exists",

       "LastMessage"."Id" AS "LastMessageId",
       "LastMessage"."Text" AS "LastMessageText",
       "LastMessage"."Time" AS "LastMessageTime",
       "LastMessage"."FromUserId" AS "LastMessageUserId"

FROM "MateChat"
LEFT JOIN LATERAL
    (SELECT *
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
     ORDER BY "Time" DESC
     LIMIT 1) "LastMessage" ON true
LEFT JOIN "DeletedMateChat" ON 
    ("DeletedMateChat"."ChatId" = "MateChat"."Id" AND
     "DeletedMateChat"."UserId" = (table "srcUserId"))
WHERE ("FirstUserId" = (table "srcUserId")
        OR "SecondUserId" = (table "srcUserId"))
            AND "DeletedMateChat"."ChatId" IS NULL
ORDER BY "LastMessageTime" DESC NULLS LAST,
         "LastMessageId" DESC NULLS LAST /* ? */
    LIMIT 10 OFFSET 0;

-- GetMateChatWithIdForUser
-- -----------------------------------------------------------------------

WITH "srcUserId" AS (VALUES (1)),
     "chatId" AS (VALUES (1))
SELECT "MateChat"."Id" AS "Id",
       case
           when "FirstUserId" = (table "srcUserId") 
                then "SecondUserId"
           else "FirstUserId"
       end as "UserId",

    (SELECT COUNT(*) FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id" 
        AND "MateMessage"."FromUserId" != (table "srcUserId")
        AND "Read" = false) AS "NewMessageCount",

       case
           when "LastMessage"."Id" is NULL then false
           else true
       end as "Exists",

       "LastMessage"."Id" AS "LastMessageId",
       "LastMessage"."Text" AS "LastMessageText",
       "LastMessage"."Time" AS "LastMessageTime",
       "LastMessage"."FromUserId" AS "LastMessageUserId"

FROM "MateChat"
LEFT JOIN LATERAL
    (SELECT *
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
     ORDER BY "Time" DESC /* or id? */
     LIMIT 1) "LastMessage" ON true
LEFT JOIN "DeletedMateChat" ON 
    ("DeletedMateChat"."ChatId" = "MateChat"."Id" AND
     "DeletedMateChat"."UserId" = (table "srcUserId"))
WHERE ("FirstUserId" = (table "srcUserId")
        OR "SecondUserId" = (table "srcUserId")) AND
           "DeletedMateChat"."ChatId" IS NULL AND
           "MateChat"."Id" = (table "chatId");

-- -----------------------------------------------------------------------

SELECT "Id", "FirstUserId", "SecondUserId" 
    FROM "MateChat" WHERE "Id" = 1 AND (
        "FirstUserId" = 1 OR "SecondUserId" = 1);

SELECT "Id", "FirstUserId", "SecondUserId",
    FROM "MateChat" WHERE ("FirstUserId" = 14 OR
                           "SecondUserId" = 14);

-- Get Mate Chat Ids What Deleted For User!
-- -----------------------------------------------------------------------

WITH "userId" AS (VALUES (14))
SELECT "Id" FROM "MateChat" 
INNER JOIN "DeletedMateChat" ON (
    "ChatId" = "Id" AND "UserId" = (table "userId"));

-- Get Mate Chat Ids What Not Deleted For User!
-- -----------------------------------------------------------------------

WITH "userId" AS (VALUES (14))
SELECT "Id" FROM "MateChat" 
LEFT JOIN "DeletedMateChat" ON (
    "ChatId" = "Id" AND "UserId" = (table "userId"))
WHERE "DeletedMateChat"."ChatId" IS NULL;

-- getAvailableTableMateChatsForUser
-- -----------------------------------------------------------------------

SELECT * FROM "DeletedMateChat";
DELETE FROM "DeletedMateChat" 
    WHERE "ChatId" = 4 AND "UserId" = 1;

SELECT * FROM "MateChat";
INSERT INTO "DeletedMateChat" VALUES (2, 2);

/*
    Desc: 
        Return all chats available to the user.
        With information about availability for second.
*/
WITH "userId" AS (VALUES (14)), /* 1stUser */
     "MateChatNotDeletedFor1stUser" AS (
    SELECT 
        "Id" AS "MateChatId",
        case 
            when "SecondUserId" = (table "userId") 
            then "FirstUserId" else "SecondUserId"
        end as "2ndUserId"

    FROM "MateChat"
    LEFT JOIN "DeletedMateChat" ON ("ChatId" = "Id" AND
                                    "UserId" = (table "userId"))
    WHERE ("FirstUserId" = (table "userId") OR
           "SecondUserId" = (table "userId")) AND 
                "DeletedMateChat"."ChatId" IS NULL /* filter! */
)
SELECT 
    "MateChatNotDeletedFor1stUser".*,
    case
        when "DeletedMateChat"."ChatId" IS NULL 
        then FALSE else TRUE
    end as "DeletedFor2nd"
FROM "MateChatNotDeletedFor1stUser"
LEFT JOIN "DeletedMateChat" ON 
    "MateChatId" = "ChatId";