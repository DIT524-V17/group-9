#include <Smartcar.h>

SR04 sensor;
Gyroscope gyro(6);
const int TRIGGER_PIN = 6;
const int ECHO_PIN = 5;
unsigned int tempSpeed;
Car car;

int led = 13;
char data = 0;

void setup() {
sensor.attach(TRIGGER_PIN, ECHO_PIN);
Serial.begin(9600);
gyro.attach();
car.begin(gyro);
gyro.begin();

// initialize the digital pin as an output.
pinMode(led, OUTPUT);

}

void loop() {
unsigned int distance = sensor.getDistance();
// since initially, the speed is set to 30,
// the car will maintain that speed until
// it encounters an obstacle
moveAuto(30);

if (distance > 0 && distance < 20){
   // obstacle encountered, speed is set to 0
  // tempSpeed = 0;
   moveAuto(0);
  //  delay(1000);
      blink();
 // car backs out without blinking its lights
      car.go(-15);
      turnRight();      
    //car.go(30);
}
else {
   moveAuto(30);
  }


}
void blink(){
     digitalWrite(led, HIGH);   // turn the LED on (HIGH is the voltage level)
     //delay(100);               // wait for a second
     digitalWrite(led, LOW);    // turn the LED off by making the voltage LOW
     //delay(100);
}
void turnRight(){
   car.rotate(1);

}
void moveAuto(int tempSpeed){
  tempSpeed = tempSpeed;
    car.setSpeed(tempSpeed);
}

//void goBack(int tempSpeed) {
//   tempSpeed = tempSpeed;
//   car.setSpeed(tempSpeed);
//}
