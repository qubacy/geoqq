SELECT *
FROM "MateMessage";


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


