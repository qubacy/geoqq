create_catalog_path = "internal/storage/scripts/ddl/create/"
delete_catalog_path = "internal/storage/scripts/ddl/delete/"

# -----------------------------------------------------------------------

import re

table_names = []
input_file = open(create_catalog_path + "all.sql", "r")
for line in input_file.readlines():
    regex = re.compile(r'CREATE TABLE (\".+?\")')
    if matches := regex.match(line):
        table_name = matches.groups()[0]
        table_names.append(table_name)

# -----------------------------------------------------------------------

table_names.reverse()
query_text = "DROP TABLE "
for table_name in table_names:
    query_text += "\n\t" + table_name + ","
query_text = query_text[:-1] + ";"    
        
# -----------------------------------------------------------------------

output_file = open(delete_catalog_path + "tables.sql", "w")
output_file.write(query_text)
