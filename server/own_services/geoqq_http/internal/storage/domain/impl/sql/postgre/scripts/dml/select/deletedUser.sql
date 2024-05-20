SELECT * FROM "UserEntry";
SELECT * FROM "DeletedUser";

INSERT INTO "DeletedUser"("UserId", "Time")
VALUES (9, NOW()::timestamp);

-- WasUserDeleted
-- -----------------------------------------------------------------------

SELECT case
           when COUNT(*) > 0 then TRUE
           else FALSE
       end as "IsDeleted"
FROM "DeletedUser"
WHERE "UserId" = 3;

-- WasUserWithLoginDeleted
-- -----------------------------------------------------------------------

SELECT case
           when COUNT(*) > 0 then TRUE
           else FALSE
       end as "IsDeleted"
FROM "DeletedUser"
INNER JOIN "UserEntry" ON (
    "Id" = "UserId" AND "Login" = 'test_user2');

-- -----------------------------------------------------------------------

SELECT * FROM "UserEntry";

SELECT  "Id",
    case
        when "DeletedUser"."UserId" IS NULL then FALSE
        else TRUE
    end as "IsDeleted"
FROM "UserEntry" 
LEFT JOIN "DeletedUser" ON "UserId" = "Id"
WHERE "Login" = 'Test';