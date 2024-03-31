SELECT *
FROM "UserEntry";


SELECT *
FROM "DeletedUser";


SELECT case
           when COUNT(*) > 0 then TRUE
           else FALSE
       end as "IsDeleted"
FROM "DeletedUser"
WHERE "UserId" = 1;

