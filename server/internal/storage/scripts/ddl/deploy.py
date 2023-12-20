create_catalog_path = "internal/storage/scripts/ddl/create/"
delete_catalog_path = "internal/storage/scripts/ddl/delete/"

# ------------------------------------------------------------------------

import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
 
# utils
# ------------------------------------------------------------------------

def create_global_connection():
    global_connection = psycopg2.connect(
        user="postgres", password="admin",
        host="127.0.0.1", port="5432")
    global_connection.set_isolation_level(
        ISOLATION_LEVEL_AUTOCOMMIT) 
    return global_connection

def create_geoqq_connection():
    geoqq_connection = psycopg2.connect(
        user="postgres", password="admin",
        host="127.0.0.1", port="5432", database="geoqq")
    geoqq_connection.set_isolation_level(
        ISOLATION_LEVEL_AUTOCOMMIT) 
    return geoqq_connection

# ------------------------------------------------------------------------

def select_db_names() -> list:
    db_names = []
    with create_global_connection() as global_connection:
        with global_connection.cursor() as cursor:
            cursor.execute('SELECT datname FROM pg_database;')
            datnames = cursor.fetchall()
            for datname in datnames:
                db_names.append(datname[0])
    return db_names
        
def select_geoqq_tables() -> list:
    table_names = []
    geoqq_connection = create_geoqq_connection()
    with geoqq_connection.cursor() as cursor:
        cursor.execute(
            "SELECT table_name "
            "FROM information_schema.tables "
            "WHERE table_schema='public' AND table_type='BASE TABLE';")
        tables = cursor.fetchall()   
        for table in tables:
            table_names.append(table[0])
    geoqq_connection.close()    
    return table_names
        
# main
# ------------------------------------------------------------------------
        
db_names = select_db_names()
print("db names before delete:", db_names)

# delete all if needed
# ------------------------------------------------------------------------
   
if db_names.count('geoqq'):
    print("geoqq tables before delete:", select_geoqq_tables())
    
    geoqq_connection = create_geoqq_connection()
    with geoqq_connection.cursor() as cursor:
        input_file = open(delete_catalog_path + "all.sql", "r") # <--- only tables.
        deletion_request = input_file.read(); input_file.close()
        cursor.execute(deletion_request)
    geoqq_connection.close()
       
    # ***   
       
    input_file = open(delete_catalog_path + "database.sql", "r")
    deletion_request = input_file.read(); input_file.close()
    
    global_connection = create_global_connection()
    cursor = global_connection.cursor() 
    cursor.execute(deletion_request)
    global_connection.close()
        
    print("db names after delete:", select_db_names())
    
# deploy all
# ------------------------------------------------------------------------

input_file = open(create_catalog_path + "database.sql", "r")
db_creation_request = input_file.read(); input_file.close()

global_connection = create_global_connection()
cursor = global_connection.cursor() 
cursor.execute(db_creation_request)
global_connection.close()

geoqq_connection = create_geoqq_connection()
with geoqq_connection.cursor() as cursor:
    input_file = open(create_catalog_path + "all.sql", "r")
    creation_request = input_file.read(); input_file.close()
    cursor.execute(creation_request)
geoqq_connection.close()

# ------------------------------------------------------------------------

print()
print("db names after deploy:", select_db_names())
print("geoqq tables after deploy:", select_geoqq_tables())