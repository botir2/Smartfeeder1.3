#!/usr/bin/env python

import io
import picamera
import logging
import socketserver
from threading import Condition
from http import server
import paho.mqtt.client as mqtt
from threading import Thread
import json
from datetime import time, datetime
from time import sleep
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(17, GPIO.OUT)

PAGE="""\
<html>
<head>
<title>Feeding machine Camera</title>
</head>
<body>
<center><h1>Feeding machine Camera</h1></center>
<center><img src="stream.mjpg" width="640" height="480"></center>
</body>
</html>
"""

class StreamingOutput(object):
    def __init__(self):
        self.frame = None
        self.buffer = io.BytesIO()
        self.condition = Condition()

    def write(self, buf):
        if buf.startswith(b'\xff\xd8'):
            # New frame, copy the existing buffer's content and notify all
            # clients it's available
            self.buffer.truncate()
            with self.condition:
                self.frame = self.buffer.getvalue()
                self.condition.notify_all()
            self.buffer.seek(0)
        return self.buffer.write(buf)

class StreamingHandler(server.BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/':
            self.send_response(301)
            self.send_header('Location', '/index.html')
            self.end_headers()
        elif self.path == '/index.html':
            content = PAGE.encode('utf-8')
            self.send_response(200)
            self.send_header('Content-Type', 'text/html')
            self.send_header('Content-Length', len(content))
            self.end_headers()
            self.wfile.write(content)
        elif self.path == '/stream.mjpg':
            self.send_response(200)
            self.send_header('Age', 0)
            self.send_header('Cache-Control', 'no-cache, private')
            self.send_header('Pragma', 'no-cache')
            self.send_header('Content-Type', 'multipart/x-mixed-replace; boundary=FRAME')
            self.end_headers()
            try:
                while True:
                    with output.condition:
                        output.condition.wait()
                        frame = output.frame
                    self.wfile.write(b'--FRAME\r\n')
                    self.send_header('Content-Type', 'image/jpeg')
                    self.send_header('Content-Length', len(frame))
                    self.end_headers()
                    self.wfile.write(frame)
                    self.wfile.write(b'\r\n')
            except Exception as e:
                logging.warning(
                    'Removed streaming client %s: %s',
                    self.client_address, str(e))
        else:
            self.send_error(404)
            self.end_headers()

class StreamingServer(socketserver.ThreadingMixIn, server.HTTPServer):
    allow_reuse_address = True
    daemon_threads = True
#
################################################################################
#
#   Json
#
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
#
################################################################################
#
#   Fedder Motor
#

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
        GPIO.output(17, GPIO.HIGH)
        sleep(3)
        GPIO.output(17, GPIO.LOW)
        GPIO.cleanup()
        print("MoroR_____________ feeding 20 gr")
    elif '30' in str(RotateMotor):
        print("MoroR_____________ feeding 30 gr")
    elif '40' in str(RotateMotor):
        print("MoroR_____________ feeding 40 gr")

#
################################################################################
#
#   for Mqttt remote control
#
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))
    # Subscribing in on_connect() - if we lose the connection and
    # reconnect then subscriptions will be renewed.
    client.subscribe("$feeder/feed")
    client.subscribe("$feeder/topic")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.payload, 'utf-8'))
    JSon = is_json(str(msg.payload, 'utf-8'))
    print(JSon[0])
    print("Time: " + check_data(JSon))
    if msg.payload == b"hi":
        print("Received message #1, Motor Feed")
        # Do something
    if msg.payload == b"yes!":
        print("Received message #2, do something else")
        # Do something else

class RemoteMqtt(Thread):
    def __init__(self):
        super(RemoteMqtt, self).__init__()
    def run(self):
        # Create an MQTT client and attach our routines to it.
        client = mqtt.Client()
        client.on_connect = on_connect
        client.on_message = on_message
        client.connect("192.168.0.2", 1883, 60)
        client.loop_forever()

if __name__ == "__main__":
    with picamera.PiCamera(resolution='640x480', framerate=24) as camera:
        output = StreamingOutput()
        #called Thread Class
        th = RemoteMqtt()
        th.setDaemon(True)
        th.start()

        #t1.join() in case of it does not work
        # Uncomment the next line to change your Pi's Camera rotation (in degrees)
        camera.rotation = 180
        camera.start_recording(output, format='mjpeg')
        try:
            address = ("192.168.0.2", 8000)
            server = StreamingServer(address, StreamingHandler)
            server.serve_forever()
        finally:
            camera.stop_recording()