SELECT *
FROM "MateRequest";


SELECT COUNT(*)
FROM "MateRequest"
WHERE "FromUserId" = 1
    AND "ToUserId" = 2
    AND "Result" = 0; -- Waiting!