SELECT * FROM "Mate";

UPDATE "Mate" SET "FirstUserId" = 13
WHERE "Id" = 1;

SELECT COUNT(*)
FROM "Mate"
WHERE ("FirstUserId" = 1 AND "SecondUserId" = 2)
    OR ("FirstUserId" = 2 AND "SecondUserId" = 1);

select
    case when exists (select true
    from "Mate"
    where "FirstUserId" = 1 AND "SecondUserId" = 1)
    then 'true'
    else 'false'
end;