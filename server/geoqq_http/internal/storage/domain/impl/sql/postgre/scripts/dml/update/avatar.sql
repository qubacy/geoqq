SELECT "Id" FROM "Avatar"
WHERE "Label" = 'deletedUser'
ORDER BY RANDOM() LIMIT 1;

SELECT * FROM "UserDetails"
ORDER BY "UserId" ASC;

UPDATE "UserDetails" SET "AvatarId" = (
    SELECT "Id" FROM "Avatar"
    WHERE "Label" = 'deletedUser'
    ORDER BY RANDOM() LIMIT 1
) WHERE "UserId" = 1;