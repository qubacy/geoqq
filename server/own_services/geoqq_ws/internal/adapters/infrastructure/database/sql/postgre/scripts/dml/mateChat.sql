SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public';

-- ----------------------------------------------

SELECT * FROM "UserEntry"
WHERE "Id" IN (1, 2, 3);

SELECT * FROM "MateChat";
SELECT * FROM "Mate";

INSERT INTO "MateChat" ("FirstUserId", "SecondUserId")
VALUES (1, 1);

SELECT 
    case 
        when "FirstUserId" = 1 
            then "SecondUserId"
            else "FirstUserId"
    end as "InterlocutorId"
FROM "MateChat" WHERE "Id" = 1;

SELECT * FROM "DeletedMateChat";

-- HasMateChatWithId
-- ----------------------------------------------

SELECT 
    case 
        WHEN COUNT(*) >= 1
            then true
            else false
    end as "Has"
FROM "MateChat" WHERE "Id" = 1;
    
-- ----------------------------------------------

SELECT * FROM "MateChat"
LEFT JOIN "DeletedMateChat" ON (
    "ChatId" = 1 AND "UserId" = 1)
WHERE "Id" = 1 AND (
    "FirstUserId" = 1 OR 
    "SecondUserId" = 1);

