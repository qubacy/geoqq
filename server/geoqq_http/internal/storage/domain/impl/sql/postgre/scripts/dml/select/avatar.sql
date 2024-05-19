SELECT * FROM "Avatar"
ORDER BY "Time" DESC;
ORDER BY "UserId" NULLS FIRST;

SELECT "HashUpdToken" FROM "UserEntry"
WHERE "Username" = 'test_user3';

-- -----------------------------------------------------------------------

SELECT COUNT(*) FROM "Avatar"
WHERE "Id" = 1;

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count"
FROM "Avatar" WHERE "Id" IN (1, 2, 3);

-- -----------------------------------------------------------------------

SELECT ("Id" + 1) AS "NextAvatarId"
FROM "Avatar"
ORDER BY "Id" DESC
LIMIT 1;

-- -----------------------------------------------------------------------

UPDATE "Avatar" SET "Label" = NULL
WHERE "Id" = 1;

SELECT 
    case 
        when COUNT(*) > 0 then TRUE
        else FALSE
    end as "Exist"
FROM "Avatar" WHERE "Label" = 'deletedUser';

-- SetRandomAvatarWithLabelForUser
-- -----------------------------------------------------------------------

SELECT "Id" FROM "Avatar"
WHERE "Label" = 'deletedUser'
ORDER BY RANDOM();

-- 
-- -----------------------------------------------------------------------

SELECT NOW()::timestamp;

SELECT EXTRACT(EPOCH FROM(NOW()::timestamp - "Time")) AS "DifferenceInSeconds"
FROM "Avatar" WHERE "UserId" = 2;

SELECT EXTRACT(EPOCH FROM NOW()::timestamp) -
       EXTRACT(EPOCH FROM "Time") AS "DifferenceInSeconds"
FROM "Avatar" WHERE "UserId" = 2;
