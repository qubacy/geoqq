SELECT * FROM "MateChat";

INSERT INTO "MateChat"("FirstUserId", "SecondUserId")
VALUES(2, 2) RETURNING "Id";

/*
Если в таблице "MateChat" уже есть запись,
где "FirstUserId" и "SecondUserId" имеют те же значения 
       или их перестановку, то будет считаться, что возник конфликт.
*/
INSERT INTO "MateChat" ("FirstUserId", "SecondUserId")
VALUES(2, 3) ON CONFLICT (
       GREATEST("FirstUserId", "SecondUserId"), 
       LEAST("FirstUserId", "SecondUserId"))
DO UPDATE SET "CreationOrReTime" = NOW()::timestamp;

INSERT INTO "MateChat" ("FirstUserId", "SecondUserId")
VALUES(2, 3) ON CONFLICT DO NOTHING;