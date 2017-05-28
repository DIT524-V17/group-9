/**
   This sketch was created to be used to connect the car with the Raspberry Pi to identify a red object, initialize the serials attached 
   on the car and bluetooth module. There are data exchange between the pi and the mobile application (Andriod code).

   @author - Laiz and Elham
   @editor - Elham and Rema: Bluetooth connection with mobile application
   @editor - Kosara: Serial connection with the raspberry pi and the car in order to send and receive data for the Identify red object feature.
   @editor - Nina: Serial connection with the pi to send the data when the red object is faced to the App.

**/
#include<Smartcar.h>

/*===============================================
              Hardware initialization
  ===============================================
*/
Car car;

/*===============================================
            Variables initialization
  ===============================================
*/
char input = 0;         // <-- for the bluetooth connection
char piInput = 0;       // <-- for the pi connection
int val = 0;            // <-- for start the python code on the R.Pi.

/*===============================================
                    SETUP
  ===============================================
*/
void setup() {

  /* Initialize the Bluetooth serial */
  Serial3.begin(9600);        // <--  Opens serial port to the App, set data rate to 9600 bps
  Serial.begin(9600);         // <--  Opens serial port to the Pi, set data rate to 9600 bps

  /* Initialize the car with the gyroscope */
  car.begin();

}

/*===============================================
                    STATE
  ===============================================
*/
void loop() {

  checkSerialInput();       // <--  Get input from blutooth
  modeSelection();          // <--  Get the mode change (with bluetooth input)

}

/*===============================================
                    MODE SELECTION
  ===============================================
*/

/* Proccess the blutooth input */
void modeSelection() {

  switch (input) {
    
    case 'o':        // <-- Verify 'o' from the app to go inside
      val = 1;
      Serial.println(val);        // <-- To send '1' to the pi
      delay(2000);
      readSerial();               // <-- To receive the info from the pi
      delay(1500);
      Serial3.println(piInput);   // <-- To send the info to the app
      delay(1500);
      break;

    case 'w':            // <-- To break the identify red object
      break;

  }
}

/*===============================================
                    BLUETOOTH
  ===============================================
*/
void checkSerialInput() {

  if (Serial3.available() > 0) { // <-- Get data only when bluetooth available
    input = Serial3.read();
  }
}

/*===============================================
                PI CONNECTION
  ===============================================
*/
void readSerial() {

  if (Serial.available() > 0) { // <-- Get data only when Serial port is available
    piInput = Serial.read();
  }
}
