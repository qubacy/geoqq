SELECT * FROM "MateMessage"
    ORDER BY "Time" ASC;

SELECT * FROM "MateChat";
SELECT * FROM "DeletedMateChat";

UPDATE "MateMessage" SET "FromUserId" = 2
WHERE "Id" = 2;

SELECT "Id",
       "Text",
       "Time",
       "FromUserId" AS "UserId"
FROM "MateMessage"
WHERE "MateChatId" = 1
ORDER BY "Time" DESC;


SELECT "Id",
       "Text",
       "Time",
       "FromUserId" AS "UserId"
FROM "MateMessage"
WHERE "MateChatId" = 1
ORDER BY "Time" DESC
LIMIT 1
OFFSET 0;

DELETE FROM "MateMessage"
WHERE "MateChatId" = 1;