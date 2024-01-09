import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

connection_params = {
    "user": "postgres", "password": "admin",
    "host": "127.0.0.1", "port": "5433",

    "database": "geoqq"
}
print("connection params:", connection_params)

# ------------------------------------------------------------------------

def create_global_connection():
    global_connection = psycopg2.connect(
        user=connection_params["user"], password=connection_params["password"],
        host=connection_params["host"], port=connection_params["port"])
    global_connection.set_isolation_level(
        ISOLATION_LEVEL_AUTOCOMMIT) 
    return global_connection

def create_geoqq_connection():
    geoqq_connection = psycopg2.connect(
        user=connection_params["user"], password=connection_params["password"],
        host=connection_params["host"], port=connection_params["port"],
        database=connection_params["database"])
    geoqq_connection.set_isolation_level(
        ISOLATION_LEVEL_AUTOCOMMIT) 
    return geoqq_connection