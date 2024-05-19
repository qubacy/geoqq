SELECT * FROM "Mate";

DELETE FROM "Mate"
WHERE ("FirstUserId" = 1 OR "SecondUserId" = 1)
     AND ("FirstUserId" = 14 OR "SecondUserId" = 14);