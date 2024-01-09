root_path = "\\".join(__file__.split("\\")[:-2])
print("root path with scripts:", root_path)

catalog_path = root_path + "\\ddl\\"
print("catalog path:", catalog_path)
output_filename = "all.sql"

# main
# ------------------------------------------------------------------------

# for several functions (and that's the problem)!
#     |
#     V
func_and_scripts = {
    "create/": 
    [
        "resource.sql", "user.sql",
        "mate.sql", "mateChat.sql",
        "geoChat.sql"
    ],
    "delete/":
    [
        "tables.sql"
    ]
}

for func, scripts in func_and_scripts.items():
    output_file = open(catalog_path + func + output_filename, "w")
    
    for script in scripts:
        input_file = open(catalog_path + func + script, "r")
        input_content = input_file.read()
        
        output_file.write("-- " + script.split(".")[0] + "\n")
        output_file.write("-- " + ("-" * 75) + "\n")
        output_file.write(input_content + "\n")
        output_file.write("\n")
        
print("[OK]")