SELECT * FROM "MateRequest";
DELETE FROM "MateRequest" WHERE "Id" = 1;

DELETE FROM "MateRequest"
WHERE ("FromUserId" = 1 OR 
    "ToUserId" = 1) AND
    "Result" = 0;

TRUNCATE "MateRequest";