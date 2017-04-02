#include <Smartcar.h>

SR04 sensorFront;
SR04 sensorBack;
Gyroscope gyro(6);
Odometer encoder; 
Car car;  

//Pin numbers       
const int encoderPin = 2;     // the number of one of the odometers pin
const int TRIGGER_PIN_F = 51;   // the number of the ultrasound sensor pin for the front
const int ECHO_PIN_F = 50;      // the number of the ultrasound sensor pin for the front
const int TRIGGER_PIN_B = 45;   // the number of the ultrasound sensor pin for the back
const int ECHO_PIN_B = 44;      // the number of the ultrasound sensor pin for the back
const int ledRight =  48;     // the number of the LED pin
const int ledLeft =  49;      // the number of the LED pin

//Variables used
char input = 0;                // for the bluetooth connection
unsigned int tempSpeed = 0;   // for setting the velocity
int ledState = LOW;           // led state used to set the LED
const long interval = 100;           // interval to blink (milliseconds)
unsigned long previousMillisL = 0;   // to store last time LED at the left side was updated
unsigned long previousMillisR = 0;   // to store last time LED at the right side was updated


void setup() {
sensorFront.attach(TRIGGER_PIN_F, ECHO_PIN_F);
sensorBack.attach(TRIGGER_PIN_B, ECHO_PIN_B);
Serial.begin(9600);
gyro.attach();
encoder.attach(encoderPin);
car.begin(gyro);
gyro.begin();
encoder.begin();

// initialize the digital pin as an output.
pinMode(ledLeft, OUTPUT);
pinMode(ledRight, OUTPUT);

}


void blinkRight(){        //Method to blink the right light
  
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

void blinkLeft(){       //Method to blink the left light
  
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

void blinkAlert(){      //Method to make both lights blink when facing an obstacle
  
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

void turnRight(){       //Method to make the car turn right + blink the right light
  
   blinkRight();      //First blink
   car.rotate(1);     //Rotate
   ledState = LOW;
    // set the LED with the ledState of the variable:
   digitalWrite(ledRight, ledState);
}

void turnLeft(){        //Method to make the car turn left + blink the left light
  
   blinkLeft();     //First blink
   car.rotate(-1);  //Rotate
   ledState = LOW;   
    // set the LED with the ledState of the variable:
   digitalWrite(ledLeft, ledState);
}

void moveCar(int tempSpeed){  //Method to make the car move given a speed
  tempSpeed = tempSpeed;
    car.setSpeed(tempSpeed);
}

void stopCar(){       //Method to make the car stop
  tempSpeed = 0;
  car.setSpeed(tempSpeed);
}

void goBack(int tempSpeed) {      //Method to make the car go backwards for a limited distance
                                  //Maybe just for the autonomous mode
  unsigned int distanceOd = encoder.getDistance();  //Geting the odometer distance not the sensor
   tempSpeed = tempSpeed;
   car.setSpeed(-(tempSpeed));
   if(distanceOd > 5){
    stopCar();
   }
}
void goBackM(int tempSpeed) {      //Method to make the car go backwards for a limited distance
                                  //Maybe just for the autonomous mode
 unsigned int distanceOd = sensorBack.getDistance();  //Geting the odometer distance not the sensor
   tempSpeed = tempSpeed;
   car.setSpeed(-(tempSpeed));
   if(distanceOd < 20 ){
    stopCar();
   }
}


boolean ObstacleFront(){               // Method to identify if there is an obstacle or not in front of the car - Not tested after small changes
  unsigned int distance = sensorFront.getDistance();
  if (distance > 0 && distance < 20){
    return true; 
  }
  else {
    return false;
  }
}

boolean ObstacleBack(){               // Method to identify if there is an obstacle or not in the back of the car - not tested
  unsigned int distance = sensorBack.getDistance();
  if (distance > 0 && distance < 20){
    return true; 
  }
  else {
    return false;
  }
}

void loop() {
  
  if(Serial.available() > 0){  // Send data only when you receive data:
    input = Serial.read();
  
    switch (input){
       
        //case Auto from the control and from the mode    
          case 'S':   //If the start button on the autonomous mode is selected
        
            //moveCar(30); // This gives problem together with the other methods, but alone works fine
            if(ObstacleFront()){
  
              blinkAlert();
              goBack(50); //Do not change this number for lower - does not run the car
              
              if(ObstacleBack()){
                blinkAlert();
                turnLeft();
              }
              turnRight();  
            } 
            break;
            
          case 'B':  //If the stop button on the autonomous mode is selected
             stopCar();
             break;

        //case Manual    
          case 'f':
            moveCar(30); 
            break;
            
          case 'b':
            goBackM(50);
            break;

          case 'l':
            turnLeft();
            break;
          
          case 'r':
            turnRight();
            break;
          
          case 's': 
            stopCar();
            break;
          
          case 'a':
            blinkAlert();
            break;

        //case 'F': // To be done
       }
       
  }

}

