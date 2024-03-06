SELECT *
FROM "MateRequest";


SELECT *
FROM "MateRequest"
WHERE "ToUserId" = 2
    AND "Result" = 0
ORDER BY "Id"
LIMIT 1
OFFSET 1;


SELECT *
FROM "MateRequest"
WHERE "ToUserId" = 2
    AND "Result" = 0
ORDER BY "RequestTime" DESC
LIMIT 1
OFFSET 1;


SELECT "Id",
       "FromUserId"
FROM "MateRequest"
WHERE "ToUserId" = 2
    AND "Result" = 0;


SELECT *
FROM "MateRequest"
WHERE "ToUserId" = 2
    AND "Result" = 0;


SELECT *
FROM "MateRequest"
WHERE "Id" = 1;


SELECT "Result"
FROM "MateRequest"
WHERE "Id" = 1;


SELECT COUNT(*)
FROM "MateRequest"
WHERE "ToUserId" = 2
    AND "Id" = 1;


SELECT COUNT(*)
FROM "MateRequest"
WHERE "ToUserId" = 2
    AND "Result" = 0;


SELECT COUNT(*)
FROM "MateRequest"
WHERE "FromUserId" = 1
    AND "ToUserId" = 2
    AND "Result" = 0; -- Waiting!