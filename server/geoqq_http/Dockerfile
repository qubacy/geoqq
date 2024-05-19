FROM golang:1.22.3-alpine3.19 AS build

ENV GOOS linux
ENV CGO_ENABLED 0

WORKDIR /app
COPY . .

WORKDIR /app/cmd
RUN go build -ldflags="-s -w" -o ./main ./main.go

FROM alpine:3.19

RUN apk update --no-cache
RUN apk add iputils vim --no-cache

COPY --from=build /app/assets /app/assets
COPY --from=build /app/internal/config/config.yml /app/cmd/config.yml
COPY --from=build /app/internal/config/for_merge/ /app/cmd/for_merge/

COPY --from=build /app/cmd/main /app/cmd/main

WORKDIR /app/cmd
CMD ./main