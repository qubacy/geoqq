import unittest

import os
import sys
import unittest

from faker import Faker

class TestFaker(unittest.TestCase):
    def setUp(self):
        self.fk = Faker()
        pass

    def test_faker_dir(self):
        dir(self.fk)

    def test_faker_methods(self):
        print("first_name: " + self.fk.unique.first_name())
        print("latitude: " + str(self.fk.latitude()))
        print("longitude: " + str(self.fk.longitude()))
        
        print(self.fk.random_element(('a', 'b', 'b')))

            
if __name__ == '__main__':
    unittest.main()