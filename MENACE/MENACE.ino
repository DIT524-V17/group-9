#include<Smartcar.h>

SR04 sensorFront;
SR04 sensorBack;
Gyroscope gyro(6);
Odometer encoderLeft;
Odometer encoderRight;
Car car;
// test//
//Pin numbers
const int encoderPinL = 2; // the number of the left odometers pin
const int encoderPinR = 3; // the number of the right odometers pin
const int TRIGGER_PIN_F = 51; // the number of the ultrasound sensor pin for the front
const int ECHO_PIN_F = 50; // the number of the ultrasound sensor pin for the front
const int TRIGGER_PIN_B = 45; // the number of the ultrasound sensor pin for the back
const int ECHO_PIN_B = 44; // the number of the ultrasound sensor pin for the back
const int ledRight = 48; // the number of the LED pin
const int ledLeft = 49; // the number of the LED pin

//Variables used
char input = 0; // for the bluetooth connection
unsigned int tempSpeed = 0; // for setting the velocity
int ledStateLeft = LOW; // led state used to set the LED
int ledStateRight = LOW; // led state used to set the LED
const long intervalLeft = 1000; // interval to blink (milliseconds)
const long intervalRight = 1000; // interval to blink (milliseconds)
const int blinkDuration = 500; // number of millisecs that Led's are on - all three leds use this
unsigned long currentMillis = 0; // stores the value of millis() in each iteration of loop()
unsigned long previousMillisL = 0; // to store last time LED at the left side was updated
unsigned long previousMillisR = 0; // to store last time LED at the right side was updated
unsigned int distanceEnL = 0;
unsigned int distanceEnR = 0;
unsigned int distanceOb = 0;
unsigned int distanceObF = 0;
unsigned int distanceObB = 0;
boolean goAuto1 = false;

boolean stopped = false; // indecates if the car is stopped
boolean canDriveForward = true;
boolean canDriveBackward = true;

void setup() {
  Serial3.begin(9600);
  sensorFront.attach(TRIGGER_PIN_F, ECHO_PIN_F);
  sensorBack.attach(TRIGGER_PIN_B, ECHO_PIN_B);
  gyro.attach();
  encoderLeft.attach(encoderPinL);
  encoderRight.attach(encoderPinR);
  car.begin(gyro);
  gyro.begin();
  encoderLeft.begin();
  encoderRight.begin();

  // initialize the digital pin as an output.
  pinMode(ledLeft, OUTPUT);
  pinMode(ledRight, OUTPUT);

}

/*===============================================
                    STATE
 ===============================================
 */
void loop() {

  currentMillis = millis();
  
  checkSerialInput(); // <---- Get input from blutooth
 
  modeSelection();    // <---- Get autonmous mode change (Also from bluetooth)

  /* Enter this section when in autonomous mode */
  if (goAuto1 == true) {
    
    if (ObstacleFront()) { //< -- Always check for obstacle and act accordingly 
      turnRight();
    }

    moveCar(50, 50); // <-- Car is always moving unless the autonmous mode is off
    stopped = false; // <-- Ignore the stopped state in autonmous mode
   
     checkSerialInput();
  } else {
    
    /* Enter this section when in manual mode */
    // The car proccess the commands from user but stops incase of obstacle
   
    Obstacle(); // <----- check allways 
  }

}

/*===============================================
                    LIGHTS
 ===============================================
 */

 /* Method to turnOn the right light */
void blinkRight() {

  if (ledStateRight == LOW) {
    if (currentMillis - previousMillisR >= intervalRight) {
      ledStateRight = HIGH;
      previousMillisR += intervalRight;
    }
  } else {
    if (currentMillis - previousMillisR >= blinkDuration) {
      ledStateRight = LOW;
      previousMillisR += blinkDuration;
    }
  }
  digitalWrite(ledRight, ledStateRight);
}


/* Method to turnOn the left light */
void blinkLeft() { 

  if (ledStateLeft == LOW) {
    if (currentMillis - previousMillisL >= intervalLeft) {
      ledStateLeft = HIGH;
      previousMillisL += intervalLeft;
    }
  } else {
    if (currentMillis - previousMillisL >= blinkDuration) {
      ledStateLeft = LOW;
      previousMillisL += blinkDuration;
    }
  }
  digitalWrite(ledLeft, ledStateLeft);
}

/* Method to turn Off both lights */
void blinkOff() { 
  ledStateLeft = LOW;
  ledStateRight = LOW;
  digitalWrite(ledLeft, ledStateLeft);
  digitalWrite(ledRight, ledStateRight);
}

/* Method to make both lights blink 4 times */
void blinkAlert() { 
    blinkLeft();
    blinkRight();
  }
  
  /*===============================================
                      TURNS
   ===============================================
   */

/* Method to make the car turn right + blink the right light */
void turnRight() { 
  blinkRight(); //First blink
  car.rotate(55); //Rotate
  blinkOff();
  delay(100);
  stopCar();
}

/* Method to make the car turn right + blink the right light */
void turnRightM() { 

  blinkRight(); //First blink
  car.rotate(55); //Rotate
  blinkOff();
  delay(1000);
  stopCar();

}

/* Method to make the car turn left + blink the left light */
void turnLeftM() { 

    blinkLeft(); //First blink
    car.rotate(-55); //Rotate
    blinkOff();
    delay(1000);
    stopCar();

  }
  /*===============================================
                      MOVEMENT
   ===============================================
   */

   /* Method to make the car move given a speed */
void moveCar(int tempSpeedL, int tempSpeedR) { 
  car.setMotorSpeed(tempSpeedR, tempSpeedL);
}

/* Method to make the car move given a speed for a certain distance */
void moveCarM(int tempSpeedL, int tempSpeedR) { 
  car.setMotorSpeed(tempSpeedR, tempSpeedL);
}

/* Method to make the car stop */
void stopCar() { 
  car.stop(); 
  stopped = true;   // <-- Set the stopped state to true
  input = 0;      // <-- Dont listen to blutooth input anymore
  delay(100);     // < -- The API documentation requires a 100 ms delay (Thats what i understood :P )
}

/* Method to make the car go backwards for a limited distance */
void goBack(int tempSpeedL, int tempSpeedR) { 
    car.setMotorSpeed(-(tempSpeedR), -(tempSpeedL));  //<-- Just set the speed but in reverse
  }
  /*===============================================
                      OBSTACLES
   ===============================================
   */
 
 /* Check for both front and back obstacles */
boolean Obstacle() {
  
  // No need to check if its allready stooped
  if (ObstacleFront() && !stopped) { 
    canDriveForward = false;  // < -- If there is an obstacle infront of the car, allow to drive backwards
    canDriveBackward = true;
    return true;
  } else if (ObstacleBack() && !stopped) {
    canDriveBackward = false; // <-- Just in like the previouse case but for the back sensor
    canDriveForward = true;
    return true;
  }

  return false;
}

/* Checks the front sensor readings for obstacles */
boolean ObstacleFront() { 
  distanceObF = sensorFront.getDistance();
  if (distanceObF > 0 && distanceObF < 25) {
    blinkAlert();   // <-- Make the lights blink 
    stopCar();      // <-- Stop the car
    blinkOff();     // <-- Stop blinking
    return true;
  }
  return false;
}

/* Checks the back sensor readings for obstacles <-- Check the previous function */
boolean ObstacleBack() { 
    distanceObB = sensorBack.getDistance();
    if (distanceObB > 0 && distanceObB < 25) {
      blinkAlert();
      stopCar();
      blinkOff();
      return true;
    }
    return false;
  }
  /*===============================================
                      DISTANCE
   ===============================================
   */

  /* Checks the distance readings (NOT USED ANYMORE) */
int checkDistanceL() {
  distanceEnL = encoderLeft.getDistance();
  return distanceEnL;
}

/* Checks the distance readings (NOT USED ANYMORE) */
int checkDistanceR() {
  distanceEnR = encoderRight.getDistance();
  return distanceEnR;
}

/* Checks the distance readings to detarmine if the car is moving (NOT USED ANYMORE) */
boolean isMoving() {
  if (checkDistanceR() == 0 || checkDistanceL() == 0) {
    return false;
  }
  return true;
}

/*===============================================
                    MANUAL CONTROL
 ===============================================
 */

/* Proccess the input from the bluetooth */
void goManual() {

  if (input == 'q') {       // <---- Stop
    stopCar();
  }
  
  if (input == 'f') {       // <----  Drive forwards
    stopped = false;
    // Perform an obstacle check before driving
    if (canDriveForward) {    
      moveCarM(50, 50);
    }
  } 
  
  if (input == 'b') {     // <---- Drive backwards
    stopped = false;
    if (canDriveBackward) {
      goBack(50, 50);
    }
  }
  if (input == 'l') {     // <---- Turn left (its acctualy driving left because it moves then stop :P)
    turnLeftM();
  }
  if (input == 'r') {     // <---- Turn right
    turnRightM();
  }
  if (input == 'j') {     // <---- Blink the lights for fun
    blinkAlert();
  }
}

/*===============================================
                    MODE SELECTION
 ===============================================
 */

 /* Proccess the blutooth input for autonmous mode switcing */
void modeSelection() {
  switch (input) {
  case 'a':         // <---- Robots will invade us :D
    goAuto1 = true;
    break;
  case 's':         // <---- STATIC no movement (Toggles the autonmous mode off)
    stopCar();
    goAuto1 = false;
    break;

  default:
    goManual();

  }
}

/*===============================================
                    BLUETOOTH
 ===============================================
 */
void checkSerialInput() {
  if (Serial3.available() > 0) { // <---- Get data only when bluetooth available:
    input = Serial3.read();
  }
}
