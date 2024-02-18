SELECT *
FROM "MateRequest";


SELECT "Result"
FROM "MateRequest"
WHERE "Id" = 1;


SELECT COUNT(*)
FROM "MateRequest"
WHERE "ToUserId" = 2
    AND "Id" = 1;


SELECT COUNT(*)
FROM "MateRequest"
WHERE "FromUserId" = 1
    AND "ToUserId" = 2
    AND "Result" = 0; -- Waiting!