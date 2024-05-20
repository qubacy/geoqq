root_path = "\\".join(__file__.split("\\")[:-2])
print("root path with scripts:", root_path)

catalog_path = root_path + "\\dml\\insert\\mockaroo\\"
print("catalog path:", catalog_path)

# ------------------------------------------------------------------------

import sys
sys.path.append(root_path)
from connect import create_geoqq_connection

# main
# ------------------------------------------------------------------------

from faker import Faker

fk = Faker()

print(fk.first_name())

