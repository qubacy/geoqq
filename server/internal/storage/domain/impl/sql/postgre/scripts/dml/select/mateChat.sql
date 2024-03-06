SELECT *
FROM "MateChat";

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
    AND ("FirstUserId" = 1
         OR "SecondUserId" = 1)
    AND NOT EXISTS
        (SELECT
         FROM "DeletedMateChat"
         WHERE "ChatId" = "Id"
             AND "UserId" = 1);

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

-- -----------------------------------------------------------------------

SELECT * FROM "MateChat";

WITH "srcUserId" AS (VALUES (1))
SELECT "MateChat"."Id" AS "Id",
       case
           when "FirstUserId" = (table "srcUserId") 
                then "SecondUserId"
           else "FirstUserId"
       end as "UserId",

    (SELECT COUNT(*)
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
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
WHERE ("FirstUserId" = (table "srcUserId")
       OR "SecondUserId" = (table "srcUserId"))
ORDER BY "Id" 
    LIMIT 1 OFFSET 0;
