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

SELECT 
    "MateMessage"."Id" AS "Id",
    "Text",
    "Time",
    "FromUserId" AS "UserId",
    "Read"
FROM "MateMessage"
INNER JOIN "MateChat" ON (
    "MateChat"."Id" = "MateMessage"."MateChatId" 
    AND "MateMessage"."MateChatId" = 1
    AND ( 
        "FirstUserId" = 13 OR
        "SecondUserId" = 13
    ) -- access check, without returning obvious errors!
)
ORDER BY "Time" DESC
LIMIT 5 OFFSET 0;

-- -----------------------------------------------------------------------

SELECT 
    "Id",
    "Text",
    "Time",
    "FromUserId" AS "UserId",
    "Read"
FROM "MateMessage"
WHERE "MateChatId" = 1 AND (
    "FromUserId" = 13 OR
    "FromUserId" != 13
)
ORDER BY "Time" DESC
LIMIT 5 OFFSET 0;

-- -----------------------------------------------------------------------

WITH "MateChatMessages" AS
    (SELECT 
        "Id",
        "Text",
        "Time",
        "FromUserId" AS "UserId",
        "Read" -- will return value before update
    FROM "MateMessage"
    WHERE "MateChatId" = 1
    ORDER BY "Time" DESC
    LIMIT 5 OFFSET 0)
UPDATE "MateMessage" 
    SET "Read" = 
        CASE "MateMessage"."FromUserId"
            WHEN 13 THEN TRUE
            ELSE "MateMessage"."Read" -- already set value
        END
FROM "MateChatMessages"
WHERE (
    "MateMessage"."Id" = "MateChatMessages"."Id"
) RETURNING "MateChatMessages".*;

-- Предложение RETURNING указывает,
-- что команда UPDATE должна вычислить и возвратить 
-- значения для каждой фактически изменённой строки. 

-- -----------------------------------------------------------------------

WITH "MateChatMessages" AS
    (SELECT 
        "MateMessage"."Id" AS "Id",
        "Text",
        "Time",
        "FromUserId" AS "UserId",
        "Read" /* will return value before update */
    FROM "MateMessage"
    INNER JOIN "MateChat" ON (
        "MateChat"."Id" = "MateMessage"."MateChatId" 
        AND "MateMessage"."MateChatId" = 4
        AND ( 
            "FirstUserId" = 14 OR
            "SecondUserId" = 14
        ) /* access check, without returning obvious errors */
    )
    ORDER BY "Id" DESC 
    LIMIT 5 OFFSET 0
    )
UPDATE "MateMessage" 
    SET "Read" = 
        CASE "MateMessage"."FromUserId"
            WHEN 13 THEN "MateMessage"."Read" /* already set value */
            ELSE TRUE 
        END
FROM "MateChatMessages"
WHERE (
    "MateMessage"."Id" = "MateChatMessages"."Id"
) RETURNING "MateChatMessages".*;