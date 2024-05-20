SELECT * FROM "DeletedMateChat";

DELETE FROM "DeletedMateChat"
WHERE "ChatId" = 1;

SELECT 
    case when COUNT(*) > 0 then TRUE
    else FALSE
    end as "WasDeleted" FROM "DeletedMateChat"
WHERE "ChatId" = 1 AND "UserId" = 1;