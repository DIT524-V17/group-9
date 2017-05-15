#author Syeda Elham Shahed
from picamera import PiCamera
from time import sleep
import cv2
import numpy as np
import argparse
import serial
import time
x = 'w'

ser = serial.Serial('/dev/ttyACM0', 9600) # kosara

while True:
        
        x = ser.readline()
        print x

        if x != 'w': 
                ##Create Camera
                camera = PiCamera()
                 
                image = camera.capture('/home/pi/Desktop/image.jpg')
                camera.close() # Laiz
                # construct the argument parse and parse the arguments
                ap = argparse.ArgumentParser()
                ap.add_argument("-i", "--image", help = "path to the image")
                args = vars(ap.parse_args())
                 
                # load the image
                image = cv2.imread(args["image"])
                accumMask = np.zeros(image.shape[:2], dtype="uint8")

                # define the list of boundaries
                boundaries = [
                        #color range for red       
                        ([17, 15, 100], [50, 56, 200])
                ]
                # loop over the boundaries
                for (lower, upper) in boundaries:
                        # create NumPy arrays from the boundaries
                        lower = np.array(lower, dtype = "uint8")
                        upper = np.array(upper, dtype = "uint8")
                 
                        # find the colors within the specified boundaries and apply
                        # the mask
                        mask = cv2.inRange(image, lower, upper)                        
                       
                        # merge the mask into the accumulated masks
                        accumMask = cv2.bitwise_or(accumMask, mask)
                        
                unmasked = cv2.countNonZero(accumMask)

                if unmasked:
                    print "has red"
                    ser.write('5') # kosara
                else:
                    print "none"
                    ser.write('1') # kosara
               
                ##cv2.imshow("images", np.hstack([image, output]))
                
                cv2.waitKey(0)


