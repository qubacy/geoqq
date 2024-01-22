import unittest

import unittest

class TestConnect(unittest.TestCase):
    def setUp(self):
        pass

# tests
# ------------------------------------------------------------------------

    def test_copy_connection_params(self):
        import connect
        
        without_db = connect.copy_connection_params(False)
        self.assertFalse("database" in without_db)
        
        print("conn params without database:", connect.copy_connection_params(False))
        print("conn params with database:", connect.copy_connection_params(True))


        
# experiments
# ------------------------------------------------------------------------

    def test_path_to_config(self):
        any_path = "\\".join(__file__.split("\\")[:-6])
        print("path to scripts:", any_path)
        
        any_path += "\\config"
        print("path to config:", any_path)

    def test_read_config(self):
        any_path = "\\".join(__file__.split("\\")[:-6]) + "\\config"

        import yaml
        with open(any_path + '\\' + 'config.yml', 'r') as config_file:
            all_config = yaml.load(config_file, Loader=yaml.FullLoader)
            postgre_config = all_config["storage"]["sql"]["postgre"]

        print("storage.type:", all_config["storage"]["type"])
        print("postgre sql config:")

        print("host:", postgre_config["host"])
        print("port:", postgre_config["port"])
        print("user:", postgre_config["user"])
        print("password:", postgre_config["password"])
        print("database:", postgre_config["database"])

    def test_input_kwargs(self):
        sub_fun = lambda **kwargs: \
            print("input kwargs in sub fun:", kwargs)

        bas_fun = lambda **kwargs:( 
            print("input kwargs in bas fun:", kwargs),
            sub_fun(**kwargs, company="Test")
        )

        bas_fun(name="Test", age="123")

    def test_output_kwargs(self):
        bas_fun = lambda **kwargs:( 
            kwargs # return?
        )

        print("output kwargs:", \
              bas_fun(name="Test", age="123"))
        
    def test_dict_to_kwargs(self):
        input_params = {
            "name":"Test",
            "age":"123"
        }

        bas_fun = lambda **kwargs:( 
            print("input kwargs what dict:", kwargs)
        )

        print("output kwargs:", \
              bas_fun(**input_params))
        
    def test_dict_to_kwargs_v1(self):
        input_params = {
            "name":"Test",
            "age":None
        }

        bas_fun = lambda **kwargs:( 
            print("input kwargs what dict:", \
                  kwargs)
        )

        print("output kwargs:", \
              bas_fun(**input_params))

if __name__ == '__main__':
    unittest.main()