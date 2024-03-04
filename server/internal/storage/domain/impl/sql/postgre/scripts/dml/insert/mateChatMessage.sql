SELECT *
FROM "MateChat";


INSERT INTO "MateMessage" ("MateChatId",
                           "FromUserId",
                           "Text",
                           "Time",
                           "Read")
VALUES (1,
        1,
        'Hello!',
        NOW()::timestamp,
        FALSE) RETURNING "Id";


SELECT *
FROM "MateMessage";