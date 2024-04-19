SELECT * FROM "UserEntry";
SELECT * FROM "MateChat";
SELECT * FROM "DeletedMateChat";

DELETE FROM "MateChat"
WHERE "Id" = 1;

SELECT * FROM "MateMessage";

WITH "MateChatForUsers" AS (
    SELECT "Id" FROM
)

SELECT "Id" FROM "MateChat"
WHERE ("FirstUserId" = 5 OR "SecondUserId" = 5) AND
      ("FirstUserId" = 14 OR "SecondUserId" = 14);

DELETE FROM "DeletedMateChat"
WHERE "ChatId" IN (
    SELECT "Id" FROM "MateChat"
    WHERE ("FirstUserId" = 5 OR "SecondUserId" = 5) AND
          ("FirstUserId" = 14 OR "SecondUserId" = 14)
);