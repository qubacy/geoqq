-- ChangeUsernameForUser
-- -----------------------------------------------------------------------

SELECT * FROM "UserDetails";

UPDATE "UserDetails" SET "Username" = 'юное дарование'
WHERE "UserId" = 3;

-- SetRandomAvatarWithLabelForUser (without ignoring the error)
-- -----------------------------------------------------------------------

UPDATE "UserDetails" SET "AvatarId" = (
    SELECT "Id" FROM "Avatar" WHERE "Label" = 'ForDeleted'
    ORDER BY RANDOM() LIMIT 1
) WHERE "UserId" = 3;


