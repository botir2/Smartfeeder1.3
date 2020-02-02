import json

# JSON format checker
def is_json(myjson):
    try:
        json_object = json.loads(myjson)
    except ValueError:
        return False
    return json_object

print(is_json("[10,14:37]"))
