SELECT *
FROM "MateChat";


INSERT INTO "MateMessage" ("MateChatId",
                           "FromUserId",
                           "Text",
                           "Time",
                           "Read")
VALUES (2,
        3,
        'Hello!',
        NOW()::timestamp,
        FALSE) RETURNING "Id";


SELECT *
FROM "MateMessage"
WHERE "MateChatId" = 4;