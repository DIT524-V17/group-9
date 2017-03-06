#include <Smartcar.h>

SR04 sensor;
const int TRIGGER_PIN = 6;
const int ECHO_PIN = 5;
unsigned int tempSpeed;
Car car;

void setup() {
    sensor.attach(TRIGGER_PIN, ECHO_PIN);
    car.begin();
}

void loop() {
  unsigned int distance = sensor.getDistance();
  // since initially, the speed is set to 40,
  // the car will maintain that speed until
  // it encounters an obstacle
    if (distance && distance < 20){
        // obstacle encountered, speed is set to 0
        tempSpeed = 0;
    } 
    else {
        tempSpeed = 40;
    }
      car.setSpeed(tempSpeed);
}
