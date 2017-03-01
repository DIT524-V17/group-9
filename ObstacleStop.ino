#include <Smartcar.h>

SR04 sensor;
const int TRIGGER_PIN = 3;
const int ECHO_PIN = 4;
unsigned int tempSpeed;
Car car;

void setup() {
    sensor.attach(TRIGGER_PIN, ECHO_PIN);
    car.begin();
}

void loop() {
  unsigned int distance = sensor.getDistance();
  // the car will maintain speed 50 until
  // it encounters an obstacle
    if (distance && distance < 15){
        // obstacle encountered, speed is set to 0
        tempSpeed = 0;
    } 
    else {
        tempSpeed = 50;
      }
      car.setSpeed(tempSpeed); 
}
