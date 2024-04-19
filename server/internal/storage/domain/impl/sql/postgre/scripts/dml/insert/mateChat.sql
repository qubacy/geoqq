SELECT *
FROM "MateChat";


INSERT INTO "MateChat"("FirstUserId",
                       "SecondUserId")
VALUES(2,
       3) RETURNING "Id";

INSERT INTO "MateChat" ("FirstUserId", "SecondUserId")
VALUES(1, 14) ON CONFLICT DO NOTHING;