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

-- Не оптимально!
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

SELECT "MateChat"."Id" AS "Id",
       case
           when "FirstUserId" = 1 then "SecondUserId"
           else "FirstUserId"
       end as "UserId",

    (SELECT COUNT(*)
     FROM "MateMessage"
     WHERE "MateChatId" = "MateChat"."Id"
         AND "Read" = false) AS "NewMessageCount",
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
     LIMIT 1) "LastMessage" ON true;