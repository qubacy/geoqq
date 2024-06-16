SELECT * FROM "UserEntry";

INSERT INTO "MateChat" ("FirstUserId", "SecondUserId")
VALUES (1, 1);

SELECT 
    case 
        when "FirstUserId" = 1 
            then "SecondUserId"
            else "FirstUserId"
    end as "InterlocutorId"
FROM "MateChat" WHERE "Id" = 1;

