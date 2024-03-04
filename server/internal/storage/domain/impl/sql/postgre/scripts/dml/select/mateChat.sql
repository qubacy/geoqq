SELECT *
FROM "MateChat";


SELECT case
           when COUNT(*) = 1 then true
           else false
       end As "Has"
FROM "MateChat"
WHERE "Id" = 1;