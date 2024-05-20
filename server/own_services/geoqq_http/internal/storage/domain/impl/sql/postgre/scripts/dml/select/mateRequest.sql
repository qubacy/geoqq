SELECT *
FROM "MateRequest";

SELECT * FROM "UserEntry"
ORDER BY "Id" DESC;


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


SELECT "MateRequest"."Id", "FromUserId", "UserEntry"."Username", "ToUserId",
       "RequestTime", "Result" FROM "MateRequest"
INNER JOIN "UserEntry" ON "UserEntry"."Id" = "FromUserId"
WHERE "ToUserId" = 14 AND "Result" = 0
ORDER BY "RequestTime" DESC, "MateRequest"."Id" DESC;


SELECT * FROM "MateRequest"
WHERE "ToUserId" = 14 AND "Result" = 0 AND "FromUserId" = 28
ORDER BY "RequestTime" DESC, "Id" DESC;

UPDATE "MateRequest"
SET "Result" = 0
WHERE "ToUserId" = 14;

SELECT * FROM "MateRequest"
WHERE "ToUserId" = 14 AND "Result" = 0
ORDER BY "RequestTime" DESC, "Id" DESC 
LIMIT 20 OFFSET 0;

SELECT COUNT (*) FROM "MateRequest"
WHERE "ToUserId" = 14 AND "Result" = 0;

SELECT * FROM "MateRequest"
WHERE "ToUserId" = 14 AND "Result" = 0
ORDER BY "RequestTime" DESC, "Id" DESC 
LIMIT 5 OFFSET 4;

SELECT * FROM "MateRequest"
WHERE "ToUserId" = 14
ORDER BY "RequestTime" DESC, "Id" DESC;