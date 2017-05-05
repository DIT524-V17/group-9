#This sends a 3 to the Arduino's serial port.

import serial
import time

ser = serial.Serial('/dev/ttyACM0', 9600)

while 1:
    ser.write('3')
	time.sleep(5)