# local
# -----------------------------------------------

SOURCE_MIGS = file://./own_services/common/storage/domain/sql/postgre/migrations

POSTGRE_ADDR = localhost:5432
POSTGRE_USER = postgres
POSTGRE_PASS = postgres
POSTGRE_DB = domain

POSTGRE_CONN_STR = postgres://$(POSTGRE_USER):$(POSTGRE_PASS)@$\
	$(POSTGRE_ADDR)/$(POSTGRE_DB)?sslmode=disable

# ***

MIGRATE_CMD = migrate -source $(SOURCE_MIGS) \
	-database $(POSTGRE_CONN_STR)

apply_migs:
	$(MIGRATE_CMD) up $(lvl)

cancel_migs:
	$(MIGRATE_CMD) down $(lvl)

# make mnext lvl=3

# docker
# -----------------------------------------------

NET_NAME = external_net

init:
	docker network prune -f && \
	docker network create $(NET_NAME)

# -----------------------------------------------

up_external: down_external
	cd external_services && \
	docker compose --env-file ./../.env up -d

down_external:
	cd external_services && \
	docker compose --env-file ./../.env down
	
# -----------------------------------------------

up: down
	docker compose up -d

down:
	docker compose down

# -----------------------------------------------

all_down: down down_external
	docker network prune -f 

