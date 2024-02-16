import unittest

import os
import sys
import unittest

class TestInflate(unittest.TestCase):
    def setUp(self):
        pass

    def test_sys_path(self):
        for one_path in sys.path:
            print(one_path)
            
if __name__ == '__main__':
    unittest.main()