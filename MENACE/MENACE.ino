#include <Smartcar.h>

SR04 sensor;
Gyroscope gyro(6);
const int TRIGGER_PIN = 51;
const int ECHO_PIN = 50;
unsigned int tempSpeed = 0;
Car car;

char data = 0;

const int ledLeft =  49; // the number of the LED pin
const int ledRight =  48; // the number of the LED pin

int ledState = LOW;             // ledState used to set the LED

unsigned long previousMillisL = 0;        // will store last time LED was updated
unsigned long previousMillisR = 0;

const long interval = 100;           // interval at which to blink (milliseconds)



void setup() {
sensor.attach(TRIGGER_PIN, ECHO_PIN);
Serial.begin(9600);
gyro.attach();
car.begin(gyro);
gyro.begin();

// initialize the digital pin as an output.
pinMode(ledLeft, OUTPUT);
pinMode(ledRight, OUTPUT);

}


void blinkRight(){
    unsigned long currentMillis = millis();

  if (currentMillis - previousMillisR >= interval) {
    // save the last time you blinked the LED
    previousMillisR = currentMillis;

    // if the LED is off turn it on and vice-versa:
    if (ledState == LOW) {
      ledState = HIGH;
    } else {
      ledState = LOW;
    }
    
    // set the LED with the ledState of the variable:
    digitalWrite(ledRight, ledState);
  }
}
void blinkLeft(){
    unsigned long currentMillis = millis();

  if (currentMillis - previousMillisR >= interval) {
    // save the last time you blinked the LED
    previousMillisL = currentMillis;

    // if the LED is off turn it on and vice-versa:
    if (ledState == LOW) {
      ledState = HIGH;
    } else {
      ledState = LOW;
    }
   
    // set the LED with the ledState of the variable:
    digitalWrite(ledLeft, ledState);
  }
}
void blinkAlert(){
    unsigned long currentMillis = millis();

  if (currentMillis - previousMillisR >= interval && currentMillis - previousMillisL >= interval ) {
    // save the last time you blinked the LED
    previousMillisL = currentMillis;
    previousMillisR = currentMillis;
    // if the LED is off turn it on and vice-versa:
    if (ledState == LOW) {
      ledState = HIGH;
    } else {
      ledState = LOW;
    }
    
    // set the LED with the ledState of the variable:
    digitalWrite(ledLeft, ledState);
    digitalWrite(ledRight, ledState);
   
  }
}
void turnRight(){
   car.rotate(1);
    ledState = LOW;
    // set the LED with the ledState of the variable:
    digitalWrite(ledRight, ledState);
}

void turnLeft(){
   car.rotate(-1);
   ledState = LOW;   
    // set the LED with the ledState of the variable:
    digitalWrite(ledLeft, ledState);
}

void moveCar(int tempSpeed){
  tempSpeed = tempSpeed;
    car.setSpeed(tempSpeed);
}

void stopCar(){
  tempSpeed = 0;
  car.setSpeed(tempSpeed);
}

void goBack(int tempSpeed) {
   tempSpeed = tempSpeed;
   car.setSpeed(-(tempSpeed));
}

boolean Obstacle(){
  unsigned int distance = sensor.getDistance();
  if (distance > 0 && distance < 20){
    return true; 
  }
  else {
    return false;
  }
}

void loop() {
  
unsigned int distance = sensor.getDistance();
// since initially, the speed is set to 30,
// the car will maintain that speed until
// it encounters an obstacle
if(Serial.available() > 0){  // Send data only when you receive data:
  data = Serial.read();

  switch (input){ //Needs the input from the App - to be done
  
    case 'Auto': 
    
      switch (input){ //Needs the input from the App - to be done
        case 'start': 
      
          moveCar(30);
          while(Obstacle() == true){

            blinkAlert();
            stopCar();
            goBack(15);
            stopCar();
            blinkRight();
            turnRight();
          } 
          break;
        case 'stop':  
           stopCar();
           break;
        case 'manual':
            //to be done
            break;
      }
    case 'Manual': // To be done 

    case 'Follow': // To be done
    
}
}
}

