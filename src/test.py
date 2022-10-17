from unittest import TestCase, main
from base64 import b64decode

test_username = "testUsername"
test_uid = 999999
test_object_uid = 12345

test_void_uid = 65535
test_void_object_uid = 98764

phishing_link = b64decode(
    "aHR0cHM6Ly90ZXN0c2FmZWJyb3dzaW5nLmFwcHNwb3QuY29tL3MvcGhpc2hpbmcuaHRtbA=="
).decode("utf-8")


class FirebaseScoreTest(TestCase):
    def test_reading_something(self):
        from src.modules import firebase

        a = firebase.get_score(test_uid, test_object_uid)
        self.assertNotEqual(a, 0)
        print(a)

    def test_reading_nothing(self):
        from src.modules import firebase

        self.assertEqual(firebase.get_score(test_void_uid, test_object_uid), 0)

    def test_reading_void_from_person(self):
        from src.modules import firebase

        self.assertEqual(firebase.get_score(test_uid, test_void_object_uid), 0)

    def test_writing_something(self):
        import random
        from src.modules import firebase

        obj = random.sample([random.randint(-65536, -1), random.randint(1, 65535)], 1)[
            0
        ]  # get -65536 <= num <= 65535 (num != 0)
        firebase.write_score(test_uid, test_object_uid, obj, test_username)
        self.assertEqual(firebase.get_score(test_uid, test_object_uid), obj)


class FirebaseCommandsTest(TestCase):
    def test_reading_command(self):
        from src.modules import firebase

        commands = firebase.read_commands(test_uid)
        self.assertNotEqual({}, commands)
        return commands

    def test_writing_command(self):
        from src.modules import firebase
        import random

        command = "_test_" + str(+random.randint(0, 65535))
        response = "test:" + str(random.randint(0, 65535))
        firebase.write_command(test_uid, command, response)
        self.assertEqual(
            firebase.read_commands(test_uid),
            firebase.read_commands(test_uid) | {command: response},
        )
        # equals assertDictContainsSubset({command: response}, firebase.read_commands(test_uid))
        return command

    def test_deleting_command(self):
        from src.modules import firebase

        command = self.test_writing_command()
        firebase.delete_command(test_uid, command)
        self.assertNotIn(command, firebase.read_commands(test_uid))

    @classmethod
    def tearDownClass(cls):
        # flushing test commands
        from src.modules import firebase

        commands = firebase.read_commands(test_uid)
        for i in commands:
            if i.startswith("_test_"):
                firebase.delete_command(test_uid, i)


class SafetyBrowsingTest(TestCase):
    def test_phishing_lookup(cls):
        import asyncio
        from commands import safetybrowsing as sb

        res = asyncio.run(
            sb.lookup(phishing_link)
        )
        cls.assertNotEqual(res,None)


if __name__ == "__main__":
    main()
