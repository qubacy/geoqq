SELECT * FROM "DeletedMateChat";

DELETE FROM "DeletedMateChat" WHERE "ChatId" = 1 AND "UserId" = 1;

INSERT INTO "DeletedMateChat" ("ChatId", "UserId") 
VALUES (1, 1);