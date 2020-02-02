from datetime import datetime
import time
import paho.mqtt.client as mqtt

if __name__ == "__main__":
    today = datetime.today().strftime('%H:%M:%S')
    now = time.localtime()
    print(today)
