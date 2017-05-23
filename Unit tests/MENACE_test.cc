// The tests are still in progress.

#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "Smartcar.h" // The Smartcar library mocks
#include "arduino-mock/Arduino.h" // Necessary to include the Serial
#include "arduino-mock/Serial.h"  // The Serial library mocks

#include "../../src/MENACE.ino" // Our production code

using ::testing::InSequence;
using ::testing::Return; // Needed to use "Return"
using ::testing::_; // Needed to use the "anything matcher"

class MENACEFixture : public ::testing::Test
{
public:
    ArduinoMock* arduinoMock; // Necessary for delay()
    SerialMock* serialMock;  // In this case, this is the Serial3/Bluetooth
    GyroscopeMock* gyroscopeMock;
    CarMock* carMock;
    SR04Mock* SR04_mock; //This is the distance sensor
    // Run this before the tests
    virtual void SetUp()
    {
	arduinoMock = arduinoMockInstance();
	serialMock = serialMockInstance();
	gyroscopeMock = gyroscopeMockInstance();
	carMock = carMockInstance();
    	SR04_mock = SR04MockInstance();
    }
    // Run this after the tests
    virtual void TearDown()
    {
	releaseArduinoMock();
	releaseSerialMock();
	releaseGyroscopeMock();
    	releaseCarMock();
    	releaseSR04Mock();
    }
};

    // Check that the sensor is attached and that the car is initialized
    TEST_F(MENACEFixture, initsAreCalled) {
    EXPECT_CALL(*serialMock, begin(9600));  // Baud rate is 9600
    EXPECT_CALL(*gyroscopeMock, attach());
    EXPECT_CALL(*carMock, begin());        // Pass the gyroscope as an argument
    InSequence seq;
    // Everything below this has to happen in the specific sequence
    EXPECT_CALL(*SR04_mock, attach(TRIGGER_PIN_F, ECHO_PIN_F)); // front Sensor
    EXPECT_CALL(*SR04_mock, attach(TRIGGER_PIN_B, ECHO_PIN_B)); // back Sensor
    setup();
}
/* checks the forward movment of the car,
 * in case no Obstacles are found
 */
TEST_F(MENACEFixture, goAuto_mode) {
    EXPECT_CALL(*SR04_mock, getDistance())
    .WillOnce(Return('a'));
    //tests if both motors have 100 for speed,which will make the car go forward
    EXPECT_CALL(*carMock, setMotorSpeed(100,100));
    loop();
}
/*
 * Checks when there is an Obstacle in front,
 * the car is in autonomous mode
 */
TEST_F(MENACEFixture, ObstacleGoAuto_mode) {
    EXPECT_CALL(*SR04_mock, getDistance())
    .WillOnce(Return("ObstacleFront is found, autonomous mode"));
    EXPECT_CALL(*carMock, rotate(84)); // this checks the rotation angle
    EXPECT_CALL(*carMock, stop()); // this checks if the car stops
    loop();
}

// Check that the car stops when Serial3 receives a 'q'
TEST_F(MENACEFixture, serial_stop) {
    EXPECT_CALL(*serialMock, read())
    .WillOnce(Return('q'));
    EXPECT_CALL(*carMock, stop());
    loop();
}

int main(int argc, char* argv[]) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
