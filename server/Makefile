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

