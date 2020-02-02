import paho.mqtt.client as mqtt
import time

#imported libs
import json
from datetime import time


# JSON format checker
def is_json(myjson):
    try:
        json_object = json.loads(myjson)
    except ValueError:
        return False
    return json_object

def check_data(datas):
    try:
        dt_obj = time(datas[1], datas[2])
        date_str = dt_obj.strftime('%H:%M:%S')
    except IndexError:
        return datetime.now().strftime('%H:%M:%S')
    return date_str


def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))
    # Subscribing in on_connect() - if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("$feeder/feed")
    client.subscribe("$feeder/topic")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload,'utf-8'))
    JSon = is_json(str(msg.payload,'utf-8'))
    print(JSon[0])
    print("Time: " + check_data(JSon))
    if msg.payload == b"hi":
        print("Received message #1, Motor Feed")
        # Do something
    if msg.payload == b"yes!":
        print("Received message #2, do something else")
        # Do something else

if __name__=="__main__":
    # Create an MQTT client and attach our routines to it.
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect("192.168.0.2",1883,60)
    client.loop_forever()