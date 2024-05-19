SELECT * FROM "MateRequest";
SELECT * FROM "UserEntry"
    ORDER BY "Id" DESC;

INSERT INTO "MateRequest" ("FromUserId",
                           "ToUserId",
                           "RequestTime",
                           "Result")
VALUES (1, 2, NOW()::timestamp, 0) 
RETURNING "Id";

do $$
begin
for i in 3..36 loop
    INSERT INTO "MateRequest" (
        "FromUserId", "ToUserId",
        "RequestTime", "Result"
        )
    VALUES (i, 2, NOW()::timestamp, 0);

    end loop;
end;
$$;