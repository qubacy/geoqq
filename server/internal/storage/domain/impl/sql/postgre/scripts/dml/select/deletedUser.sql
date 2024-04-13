SELECT * FROM "UserEntry";
SELECT * FROM "DeletedUser";

INSERT INTO "DeletedUser"("UserId", "Time")
VALUES (9, NOW()::timestamp);

SELECT case
           when COUNT(*) > 0 then TRUE
           else FALSE
       end as "IsDeleted"
FROM "DeletedUser"
WHERE "UserId" = 9; -- has username 'test_user8'

SELECT case
           when COUNT(*) > 0 then TRUE
           else FALSE
       end as "IsDeleted"
FROM "DeletedUser"
INNER JOIN "UserEntry" ON (
    "UserEntry"."Id" = "DeletedUser"."UserId" AND
    "UserEntry"."Username" = 'test_user8');

SELECT 
    "Id", "Username",
    case
        when "DeletedUser"."UserId" IS NULL then FALSE
        else TRUE
    end as "IsDeleted"
FROM "UserEntry" 
LEFT JOIN "DeletedUser" ON "UserId" = "Id"
WHERE "Username" = 'test_user8';