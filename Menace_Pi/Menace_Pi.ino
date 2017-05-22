
#include<Smartcar.h>

Car car;

char input = 0;                     // <---- for the bluetooth connection
char output = 0;                    // <---- for the bluetooth connection
char outputN= 0;
char piInput = 0;                   // <---- for the pi connection
unsigned int tempSpeed = 0;         // <---- for setting the velocity
int val = 0;

void setup() {

  /* Initialize the Bluetooth serial */
  Serial3.begin(9600);        // <--  Opens serial port to the App, set data rate to 9600 bps
  Serial.begin(9600);         // <--  Opens serial port to the Pi, set data rate to 9600 bps

  /* Initialize the car with the gyroscope */
  car.begin();

}

void loop() {

  
  checkSerialInput();       // <--  Get input from blutooth
  modeSelection();          // <--  Get the mode change (with bluetooth input)

}

void modeSelection() {

  switch (input) {


    case 'o':            // <-- Send 'o' to the Pi to idenfity red object
    Serial.begin(9600);
    val = 1;
      Serial.println(val);  // <-- To send 'o' to the pi
      delay(2000);
      readSerial();      // <-- To receive the info from the pi
      delay(1500);
      Serial3.println(piInput);   // <-- To send the info to the app
      break;

    case 'w':            // <-- To break the identify red object
    Serial.begin(9600);
    val = 2;
      Serial.println(val); // <-- To send 'w' to the pi
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
