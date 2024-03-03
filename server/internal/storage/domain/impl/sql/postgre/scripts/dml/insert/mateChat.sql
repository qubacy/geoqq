SELECT *
FROM "MateChat";


INSERT INTO "MateChat"("FirstUserId",
                       "SecondUserId")
VALUES(1,
       2) RETURNING "Id";