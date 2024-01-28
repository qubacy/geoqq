INSERT INTO "Avatar" ("GeneratedByServer",
                      "Time",
                      "Hash")
VALUES (TRUE,
        NOW()::timestamp,
        '640ab2bae07bedc4c163f679a746f7ab7fb5d1fa') RETURNING "Id";