SELECT * FROM "MateRequest";
SELECT * FROM "UserEntry";

INSERT INTO "MateRequest" ("FromUserId",
                           "ToUserId",
                           "RequestTime",
                           "Result")
VALUES (1, 2, NOW()::timestamp, 0) 
RETURNING "Id";

do $$
begin
for i in 34..39 loop
    INSERT INTO "MateRequest" (
        "FromUserId", "ToUserId",
        "RequestTime", "Result"
        )
    VALUES (i, 14, NOW()::timestamp, 0);

    end loop;
end;
$$;