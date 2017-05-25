#
# The below code were created to identify a red object in front of the 
# camera. This was an extra feature to the Menace project, in which the
# car was put in a mode where a red object needs to be identified and
# the user noticed on the application that the object was found. For this
# an exchange of information between the Pi and the car must happen.
#
# @author Syeda Elham Shahed
# @editor Kosara - Provided the serial code to connect and send data
# from the pi to the car.
# @editor Laiz Figueroa - Fixed a bug in the loop, receive values sent
# by the car, and close the camera after using it. 
#

from picamera import PiCamera
from time import sleep
import cv2
import numpy as np
import argparse
import serial
import time

# Initializing the variables
x = ' ' 
ser = serial.Serial('/dev/ttyACM0', 9600) # Kosara's code

# The code must run during the Arduino is on, waiting for a command from the user. 
while 1:
        
        x = ser.readline()          # Laiz's code - Check if the car sends a value
        print x                           

        if x != ' ': 
                # Create Camera, set a resolution, save it and close the camera
                camera = PiCamera()
                camera.resolution = (100, 100)
                image = camera.capture('/home/pi/Desktop/image.jpg')
                camera.close()            # Laiz's code
                
                # Construct the argument parse and parse the arguments
                ap = argparse.ArgumentParser()
                ap.add_argument("-i", "--image", help = "path to the image")
                args = vars(ap.parse_args())
                 
                # Load the image
                image = cv2.imread(args["image"])
                accumMask = np.zeros(image.shape[:2], dtype="uint8")

                # Define the list of boundaries
                boundaries = [
                        # Color range for red       
                        ([17, 15, 100], [50, 56, 200])
                ]
                
                # Loop over the boundaries
                for (lower, upper) in boundaries:
                        # Create NumPy arrays from the boundaries
                        lower = np.array(lower, dtype = "uint8")
                        upper = np.array(upper, dtype = "uint8")
                 
                        # Find the colors within the specified boundaries and apply
                        # the mask
                        mask = cv2.inRange(image, lower, upper)                        
                       
                        # Merge the mask into the accumulated masks
                        accumMask = cv2.bitwise_or(accumMask, mask)
               

                # Show the images               
                unmasked = cv2.countNonZero(accumMask)

                if unmasked:
                    print "has red"
                    ser.write('z') # Kosara's code
                else:
                    print "none"
                    ser.write('n') # Kosara's code
               
                cv2.destroyAllWindows()
