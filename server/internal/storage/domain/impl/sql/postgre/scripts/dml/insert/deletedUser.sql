SELECT * FROM "DeletedUser";
DELETE FROM "DeletedUser";

INSERT INTO "DeletedUser" ("UserId", "Time")
VALUES (10, NOW()::timestamp);