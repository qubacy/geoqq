SELECT * FROM "UserEntry";
SELECT * FROM "Mate";

INSERT INTO "Mate" (
    "FirstUserId",
    "SecondUserId")
VALUES (10, 2);

-- -----------------------------------------------------------------------

DO $$
DECLARE
    counter INT := 1;
BEGIN
    LOOP
        INSERT INTO "Mate" (
            "FirstUserId", "SecondUserId")
        VALUES (1, counter)
            ON CONFLICT DO NOTHING;

        counter := counter + 1;
        IF counter > 5 THEN
            DELETE FROM "Mate" 
                WHERE "FirstUserId" = "SecondUserId"; -- !

            EXIT;
        END IF;

    END LOOP;
END $$;

-- -----------------------------------------------------------------------

SELECT * FROM "Mate"
    WHERE ("FirstUserId" = 1 OR 
        "SecondUserId" = 1);

SELECT
    case when "FirstUserId" = 1
        then "SecondUserId" else "FirstUserId"
        END as "UserId"
    FROM "Mate"
    WHERE ("FirstUserId" = 1 OR
        "SecondUserId" = 1);

-- -----------------------------------------------------------------------