import paho.mqtt.client as mqtt
import time
import RPi.GPIO as GPIO

#imported libs
import json
from datetime import time, datetime
from time import sleep

# JSON format checker
def is_json(myjson):
    try:
        json_object = json.loads(myjson)
    except KeyboardInterrupt:
        return False
    return json_object

def check_data(datas):
    try:
        dt_obj = time(datas[1], datas[2])
        date_str = dt_obj.strftime('%H:%M:%S')
    except:
        return "null"
    return date_str

def feederMotor(foodAmound, times):
    if "null" in str(times):
        print("Nullcha")
        Motor(foodAmound)
    elif foodAmound:
        while True:
            today = datetime.today().strftime('%H:%M:%S')
            if times == today:
               Motor(foodAmound)
               print("______________________________")
               break
            print("Counting time...")
            sleep(1)

def Motor(RotateMotor):
    if '20' in str(RotateMotor):
        try:
            GPIO.setmode(GPIO.BCM)
            GPIO.setup(4, GPIO.OUT)
            GPIO.output(4, True)
            sleep(5)
            GPIO.output(4, GPIO.LOW)
            GPIO.cleanup()
        except:
            GPIO.cleanup()
        print("MoroR_____________ feeding 20 gr")
    elif '30' in str(RotateMotor):
        print("MoroR_____________ feeding 30 gr")

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
    JSonTime = is_json(str(msg.payload, 'utf-8'))
    times = check_data(JSonTime)
    feederMotor(JSon[0], times)
    print("Time: " + check_data(JSonTime))
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