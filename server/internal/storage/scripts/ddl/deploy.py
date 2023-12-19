create_catalog_path = "internal/storage/scripts/ddl/create/"
delete_catalog_path = "internal/storage/scripts/ddl/delete/"

# ------------------------------------------------------------------------

import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

connection = psycopg2.connect(
    user="postgres", password="admin",
    host="127.0.0.1", port="5432"
)
connection.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT) 

db_names = []
with connection.cursor() as cursor:
    cursor.execute('SELECT datname FROM pg_database;')
    datnames = cursor.fetchall()
    for datname in datnames:
        db_names.append(datname[0])
print(db_names)

# delete all if needed
# ------------------------------------------------------------------------
   
if db_names.count('geoqq'):
    with connection.cursor() as cursor:
        input_file = open(delete_catalog_path + "all.sql", "r")
        deletion_request = input_file.read()
        cursor.execute(deletion_request)
       
    cursor = connection.cursor() 
    input_file = open(delete_catalog_path + "database.sql", "r")
    deletion_request = input_file.read()
    cursor.execute(deletion_request)
    
# deploy all
# ------------------------------------------------------------------------

# input_file = open(create_catalog_path + "database.sql", "r")
# db_creation_request = input_file.read()
# cursor.execute(db_creation_request)
# connection.commit()

# connection.close()