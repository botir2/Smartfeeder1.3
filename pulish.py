import paho.mqtt.client as paho
import time

def on_publish(client, userdata, mid):
    print("mid: "+str(mid))

client = paho.Client()
client.on_publish = on_publish
#client.connect("broker.mqttdashboard.com",1883,60)
client.connect("192.168.0.2", 1883, 60)
client.loop_start()

while True:
    temperature = "chumhuq"
    (rc, mid) = client.publish("rsp/temp", str(temperature), qos=1)
    time.sleep(1)