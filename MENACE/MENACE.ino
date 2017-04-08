#include <Smartcar.h>

SR04 sensorFront;
SR04 sensorBack;
Gyroscope gyro(6);
Odometer encoderLeft;
Odometer encoderRight;
Car car;

//Pin numbers
const int encoderPinL = 2;      // the number of the left odometers pin
const int encoderPinR = 3;      // the number of the right odometers pin
const int TRIGGER_PIN_F = 51;   // the number of the ultrasound sensor pin for the front
const int ECHO_PIN_F = 50;      // the number of the ultrasound sensor pin for the front
const int TRIGGER_PIN_B = 45;   // the number of the ultrasound sensor pin for the back
const int ECHO_PIN_B = 44;      // the number of the ultrasound sensor pin for the back
const int ledRight =  48;       // the number of the LED pin
const int ledLeft =  49;        // the number of the LED pin

//Variables used
char input = 0;                      // for the bluetooth connection
unsigned int tempSpeed = 0;          // for setting the velocity
int ledStateLeft = LOW;              // led state used to set the LED
int ledStateRight = LOW;             // led state used to set the LED
const long intervalLeft = 1500;      // interval to blink (milliseconds)
const long intervalRight = 1500;     // interval to blink (milliseconds)
const int blinkDuration = 500;       // number of millisecs that Led's are on - all three leds use this
unsigned long currentMillis = 0;    // stores the value of millis() in each iteration of loop()
unsigned long previousMillisL = 0;   // to store last time LED at the left side was updated
unsigned long previousMillisR = 0;   // to store last time LED at the right side was updated
unsigned int distanceEnL = 0;
unsigned int distanceEnR = 0;
unsigned int distanceOb = 0;
unsigned int distanceObF = 0;
unsigned int distanceObB = 0;

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

void loop() {

  currentMillis = millis();

  if (Serial3.available() > 0) { // Send data only when you receive data:
    input = Serial3.read();

    switch (input) {

      //case Auto from the control and from the mode
      case 'a':   //If the start button on the autonomous mode is selected
        goAuto();
        break;

      default: goManual();

    }
  }
}


void blinkRight() {       //Method to turnOn the right light

  if (ledStateRight == LOW) {
    if (currentMillis - previousMillisR >= intervalRight) {
      ledStateRight = HIGH;
      previousMillisR += intervalRight;
    }
  }
  else {
    if (currentMillis - previousMillisR >= blinkDuration) {
      ledStateRight = LOW;
      previousMillisR += blinkDuration;
    }
  }
  digitalWrite(ledRight, ledStateRight);
}

void blinkLeft() {      //Method to turnOn the left light

  if (ledStateLeft == LOW) {
    if (currentMillis - previousMillisL >= intervalLeft) {
      ledStateLeft = HIGH;
      previousMillisL += intervalLeft;
    }
  }
  else {
    if (currentMillis - previousMillisL >= blinkDuration) {
      ledStateLeft = LOW;
      previousMillisL += blinkDuration;
    }
  }
  digitalWrite(ledLeft, ledStateLeft);
}

void blinkOff() {           //Method to turn Off both lights
  ledStateLeft = LOW;
  ledStateRight = LOW;
  digitalWrite(ledLeft, ledStateLeft);
  digitalWrite(ledRight, ledStateRight);
}

void blinkAlert() {     //Method to make both lights blink 4 times - Could be in a loop for an certain time - but it works for now

  blinkLeft();
  blinkRight();
  delay(1000);
  blinkOff();
  delay(1000);
  blinkLeft();
  blinkRight();
  delay(1000);
  blinkOff();
  delay(1000);
  blinkLeft();
  blinkRight();
  delay(1000);
  blinkOff();
  delay(1000);
  blinkLeft();
  blinkRight();
  delay(1000);
  blinkOff();
}

void turnRight() {      //Method to make the car turn right + blink the right light

  blinkRight();       //First blink
  car.rotate(55);     //Rotate
  blinkOff();
}

void turnLeft() {       //Method to make the car turn left + blink the left light

  blinkLeft();      //First blink
  car.rotate(-55);  //Rotate
  blinkOff();
}

void moveCar(int tempSpeedL, int tempSpeedR) { //Method to make the car move given a speed
  car.setMotorSpeed(tempSpeedR, tempSpeedL);
}

void moveCarM(int tempSpeedL, int tempSpeedR) { //Method to make the car move given a speed for a certain distance
  car.setMotorSpeed(tempSpeedR, tempSpeedL);
  //car.go(40); //this method cames from his library and it is supposed to make the car run for X cm

  //while(isMoving()){       //here we need a loop to keep checking the distance when the method is called in order to stop the car
  //checkDistanceL();
  //if (distanceEnL == 40){
  //stopCar();
}
//}
//it is needed a distance checker

//}
void stopCar() {      //Method to make the car stop
  car.stop();
}

void goBack(int tempSpeedL, int tempSpeedR) {      //Method to make the car go backwards for a limited distance
  car.setMotorSpeed(-(tempSpeedR), -(tempSpeedL));

  //it is needed a distance checker for 20 cm
}

void Obstacle() {

  if (ObstacleFront() == true) {

    stopCar();
    delay(1000);
  }
}

boolean ObstacleFront() {              // Method to identify if there is an obstacle or not in front of the car - Not tested after small changes
  distanceObF = sensorFront.getDistance();
  if (distanceObF > 0 && distanceObF < 20) {
    return true;
  }
  else {
    return false;
  }
}

boolean ObstacleBack() {              // Method to identify if there is an obstacle or not in the back of the car - not tested
  distanceObB = sensorBack.getDistance();
  if (distanceObB > 0 && distanceObB < 20) {
    return true;
  }
  else {
    return false;
  }
}

int checkDistanceL() {
  distanceEnL = encoderLeft.getDistance();
  return distanceEnL;
}

int checkDistanceR() {
  distanceEnR = encoderRight.getDistance();
  return distanceEnR;
}

boolean isMoving() {
  if (checkDistanceR() == 0 || checkDistanceL() == 0) {
    return false;
  }
  return true;

}

void goAuto() { //Not working properly - the car jsut move
  if (ObstacleFront()) { //we need to make the car check all the times, not just in the begining
    Obstacle();
    turnRight();
  }
  moveCar(30, 30);
}

void goManual() {
  if (input == 'q') {
    stopCar();
  }
  if (input == 'f') {
    //if(ObstacleFront()){  //we need a check obstacles - if not we can make a scenario to fit this
    //stopCar();
    //promptUser();
    //}
    moveCarM(30, 30);
  }
  if (input == 'b') {
    // while (ObstacleBack()) { //we need a check obstacles - if not we can make a scenario to fit this
    //stopCar();
    //promptUser();
    //}
    goBack(30, 30);
  }
  if (input == 'l') {
    turnLeft();
  }
  if (input == 'r') {
    turnRight();
  }
  if (input == 'j') {
    blinkAlert();
  }
}

String promptUser() { //method to prompt the user for a new direction

}










