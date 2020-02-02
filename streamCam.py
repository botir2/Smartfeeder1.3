from gpiozero import MotionSensor
from picamera import PiCamera

def Camera() :
    pir = MotionSensor(4)
    camera = PiCamera()
    filename = "intruder.h264"
    while True:
        pir.wait_for_motion()
        print("Motion detected!")
        camera.start_recording(filename)
        pir.wait_for_no_motion()
        camera.stop_preview()

if __name__ == "__main__":
    Camera()