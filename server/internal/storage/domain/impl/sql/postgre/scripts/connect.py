path_to_config = "\\".join(__file__.split("\\")[:-7]) + "\\config"
print("path to config:", path_to_config) # when will this code be executed?

def read_postgre_config():
    import yaml
    with open(path_to_config + '\\' + 'config.yml', 'r') as config_file:
        all_config = yaml.load(config_file, Loader=yaml.FullLoader)
        return all_config["storage"]["domain"]["sql"]["postgre"]
    
connection_params: dict = read_postgre_config() # at what point is it initialized?
print("connection params:", connection_params)

def copy_connection_params(with_database: bool):
    result_conn_params = connection_params.copy()
    if with_database:
        return result_conn_params
    del result_conn_params["database"]   
    return result_conn_params

# create conns!
# ------------------------------------------------------------------------

import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

def create_global_connection():
    global_connection = psycopg2.connect(
        **copy_connection_params(False))
    
    global_connection.set_isolation_level(
        ISOLATION_LEVEL_AUTOCOMMIT) 
    return global_connection

def create_geoqq_connection():
    geoqq_connection = psycopg2.connect(
        **copy_connection_params(True))
    
    geoqq_connection.set_isolation_level(
        ISOLATION_LEVEL_AUTOCOMMIT) 
    return geoqq_connection