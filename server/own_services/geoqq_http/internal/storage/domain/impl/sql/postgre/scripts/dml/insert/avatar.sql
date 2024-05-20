SELECT * FROM "Avatar";

SELECT * FROM "Avatar"
LIMIT 10 OFFSET 8;

-- ----------------------------------------------

WITH "hashValue" AS (VALUES ('123'))
INSERT INTO "Avatar" (
    "UserId",
    "Time",
    "Hash"
)
VALUES (
    NULL,
    NOW()::timestamp,
    (table "hashValue")
) RETURNING "Id";

-- ----------------------------------------------

WITH "hashValue" AS (VALUES ('123')),
     "label" AS (VALUES ('for_deleted_user'))
INSERT INTO "Avatar" ( 
    "UserId", "Label",
    "Time", "Hash"
)
VALUES (
    NULL,
    (table "label"),
    NOW()::timestamp,
    (table "hashValue")
) RETURNING "Id";

-- ----------------------------------------------

WITH "hashValue" AS (VALUES ('123')),
     "userId" AS (VALUES (1))
INSERT INTO "Avatar" ( 
    "UserId", "Time", "Hash"
)
VALUES (
    (table "userId"),
    NOW()::timestamp,
    (table "hashValue")
) RETURNING "Id";