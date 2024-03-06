SELECT *
FROM "MateChat";


INSERT INTO "MateChat"("FirstUserId",
                       "SecondUserId")
VALUES(2,
       3) RETURNING "Id";