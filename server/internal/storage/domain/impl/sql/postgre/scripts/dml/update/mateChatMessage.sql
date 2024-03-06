SELECT *
FROM "MateMessage";


UPDATE "MateMessage"
SET "MateChatId" = 2
WHERE "Id" = 7;


UPDATE "MateMessage"
SET "Read" = FALSE
WHERE "Id" IN
        (SELECT "Id"
         FROM "MateMessage"
         WHERE "MateChatId" = 1
         ORDER BY "Time" DESC);

-- -----------------------------------------------------------------------

WITH "MateChatMessages" AS
    (SELECT "Id",
            "Text",
            "Time",
            "FromUserId" AS "UserId"
     FROM "MateMessage"
     WHERE "MateChatId" = 1
     ORDER BY "Time" DESC
     LIMIT 3 OFFSET 0)
UPDATE "MateMessage"
SET "Read" = TRUE
FROM "MateChatMessages"
WHERE "MateMessage"."Id" = "MateChatMessages"."Id" 
    RETURNING "MateChatMessages".*;

-- -----------------------------------------------------------------------