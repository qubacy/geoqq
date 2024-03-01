SELECT *
FROM "Avatar";

-- -----------------------------------------------------------------------

SELECT COUNT(*)
FROM "Avatar"
WHERE "Id" = 1;

-- -----------------------------------------------------------------------

SELECT COUNT(*) AS "Count"
FROM "Avatar"
WHERE "Id" IN (1, 2, 3);