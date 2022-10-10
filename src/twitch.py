import os
import requests

token = os.environ["TWITCH_ACCESS_TOKEN"]
client_id = "gp762nuuoqcoxypju8c569th9wz7q5"


def uid_to_username(uid: int) -> str:
    endpoint = f"https://api.twitch.tv/helix/users?id={uid}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    r = requests.get(endpoint, headers=head).json()
    return r["data"][0]["login"]


def uid_to_nickname(uid: int) -> str:
    endpoint = f"https://api.twitch.tv/helix/users?id={uid}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    r = requests.get(endpoint, headers=head).json()
    return r["data"][0]["display_name"]


def username_to_uid(username: str) -> int:
    endpoint = f"https://api.twitch.tv/helix/users?login={username}"
    head = {"Authorization": f"Bearer {token}", "Client-Id": client_id}

    r = requests.get(endpoint, headers=head).json()
    return r["data"][0]["id"]