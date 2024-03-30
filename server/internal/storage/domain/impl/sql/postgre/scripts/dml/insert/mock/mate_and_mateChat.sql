SELECT *
FROM "UserEntry";


SELECT *
FROM "Mate";


SELECT *
FROM "MateChat";

do $$
begin
for i in 1..34 loop
    insert into "Mate"("FirstUserId", "SecondUserId")
    values(i, 13) ON CONFLICT DO NOTHING;

    insert into "MateChat"("FirstUserId", "SecondUserId")
    values(i, 13) ON CONFLICT DO NOTHING;
    end loop;
end;
$$;


FOR i IN 1..5 LOOP
INSERT INTO "Mate" ("FirstUserId",
                    "SecondUserId")
VALUES (i,
        13)
INSERT INTO "MateChat" ("FirstUserId",
                        "SecondUserId")
VALUES (i,
        13) END LOOP;

