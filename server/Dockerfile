FROM golang:1.22.3-alpine3.19 AS build

RUN apk update --no-cache
RUN apk add make --no-cache

ENV GOOS linux
ENV CGO_ENABLED 0

WORKDIR /app

COPY . .
RUN mv Makefile.target_linux Makefile

RUN go mod download
RUN make build

FROM alpine:3.19

RUN apk update --no-cache
RUN apk add make iputils vim --no-cache

COPY --from=build /app/cmd/main /app/cmd/main
COPY --from=build /app/internal/config/config.yml /app/cmd/config.yml
COPY --from=build /app/internal/config/mode/* /app/cmd/mode/*
COPY --from=build /app/Makefile /app/Makefile

WORKDIR /app/cmd
CMD ./main