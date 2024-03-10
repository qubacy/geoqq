SELECT *
FROM "GeoMessage";


INSERT INTO "GeoMessage" ("FromUserId",
                          "Text",
                          "Time",
                          "Latitude",
                          "Longitude")
VALUES (1,
        'Hello from dml!',
        NOW()::timestamp,
        56.01839,
        92.86717) RETURNING "Id";