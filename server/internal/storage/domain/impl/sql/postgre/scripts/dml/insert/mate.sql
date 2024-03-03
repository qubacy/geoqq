SELECT *
FROM "Mate";


DELETE
FROM "Mate"
WHERE "FirstUserId" = 1
        AND "SecondUserId" = 2;


INSERT INTO "Mate" ("FirstUserId",
                    "SecondUserId")
VALUES (1,
        2) RETURNING "Id";


INSERT INTO "Mate" ("FirstUserId",
                    "SecondUserId")
VALUES (2,
        1) RETURNING "Id";

