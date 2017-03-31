#include <Smartcar.h>

SR04 sensorFront;
//SR04 sensorBack;
Gyroscope gyro(6);
Odometer encoder; 
Car car;  

//Pin numbers       
const int encoderPin = 2;     // the number of one of the odometers pin
const int TRIGGER_PIN_F = 51;   // the number of the ultrasound sensor pin for the front
const int ECHO_PIN_F = 50;      // the number of the ultrasound sensor pin for the front
//const int TRIGGER_PIN_B = 51;   // the number of the ultrasound sensor pin for the back
//const int ECHO_PIN_B = 50;      // the number of the ultrasound sensor pin for the back
const int ledRight =  48;     // the number of the LED pin
const int ledLeft =  49;      // the number of the LED pin

//Variables used
char data = 0;                // for the bluetooth connection
unsigned int tempSpeed = 0;   // for setting the velocity
char input;                   // holds the input from the app
int ledState = LOW;           // led state used to set the LED
const long interval = 100;           // interval to blink (milliseconds)
unsigned long previousMillisL = 0;   // to store last time LED at the left side was updated
unsigned long previousMillisR = 0;   // to store last time LED at the right side was updated


void setup() {
sensorFront.attach(TRIGGER_PIN_F, ECHO_PIN_F);
//sensor.attach(TRIGGER_PIN_B, ECHO_PIN_B);
Serial.begin(9600);
//Serial.setTimeout(200);
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
                                  //Maybe jsut for the autonomous mode
  unsigned int distanceOd = encoder.getDistance();  //Geting the odometer distance not the sensor
   tempSpeed = tempSpeed;
   car.setSpeed(-(tempSpeed));
   if(distanceOd > 5){
    stopCar();
   }
}

// Method to identify if there is an obstacle or not in front of the car - Not tested after small changes

boolean ObstacleFront(){               
  unsigned int distance = sensorFront.getDistance();
  if (distance > 0 && distance < 20){
    return true; 
  }
  else {
    return false;
  }
}

// Method to identify if there is an obstacle or not in the back of the car - not tested

//boolean ObstacleBack(){               
//  unsigned int distance = sensorBack.getDistance();
 // if (distance > 0 && distance < 20){
 //   return true; 
//  }
 // else {
  //  return false;
 // }
//}

void loop() {
  
if(Serial.available() > 0){  // Send data only when you receive data:
  data = Serial.read();
  switch (input){ //Needs the input from the App - to be done
  
    case 'A': //The case where the user selects the car to be AUTONOMOUS
    
      switch (input){   //Needs the input from the App - to be done
        case 'S':   //If the START button on the autonomous mode is selected
      
          //moveCar(30); // This gives problem together with the other methods, but alone works fine
          if(ObstacleFront()){

            blinkAlert();
            goBack(50); //Do not change this number for lower - does not run the car
            turnRight();  
          } 
          break;
        case 'P':  //If the STOP button on the autonomous mode is selected
           stopCar();
           break;
        case 'M': //If the MANUAL button on the autonomous "page" is selected
            //to be done
            break;
      }
    case 'M': // To be done; MANUAL
      switch (input){    // Needs the input from the App - to be done
        case 'F':       // FORWARD
          moveCar(30); 
        case 'B':       // BACKWARD 
          goBack(50);
        case 'L':      // LEFT
          turnLeft();
        case 'R':      // RIGHT
          turnRight();
        case 'P':      // STOP
          stopCar();
        case 'E':     // ALERT
          blinkAlert();
        //case 'Auto': //Needs to be done
            
              
  //  case 'Follow': // To be done
    
    }
  }
}
}
