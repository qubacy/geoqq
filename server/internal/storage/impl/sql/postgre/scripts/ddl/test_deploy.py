import unittest

import os
import sys
import unittest

class TestDeploy(unittest.TestCase):
    def setUp(self):
        pass

    def test_sys_path(self):
        for one_path in sys.path:
            print(one_path)

    def test_os_curdir(self):
        print(os.curdir)
        print(os.path.abspath(os.curdir))
        print(os.path.abspath(__file__))

    def test_os_cur_file(self):
        print("abs __file__:", os.path.abspath(__file__))
        print("only __file__:", __file__)

        file_parts = __file__.split("\\")
        print("file_parts:", file_parts)
        file_parts = file_parts[:-2]
        print("file_parts:", file_parts)

        root_path = "\\".join(file_parts)
        print("root path:", root_path)

    def test_os_cur_file_v1(self):
        print("root path v1:", \
              "\\".join(__file__.split("\\")[:-2]))

if __name__ == '__main__':
    unittest.main()