SELECT * FROM "MateRequest";

INSERT INTO "MateRequest" ("FromUserId",
                           "ToUserId",
                           "RequestTime",
                           "Result")
VALUES (1, 2, NOW()::timestamp, 0) 
RETURNING "Id";

SELECT * FROM "MateRequest";