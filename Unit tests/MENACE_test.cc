// The tests are still in progress.

#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "Smartcar.h" // The Smartcar library mocks
#include "arduino-mock/Arduino.h" // Necessary to include the Serial
#include "arduino-mock/Serial.h"  // The Serial library mocks
#include "Smartcar.h" // The Smartcar library mocks

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
    SR04Mock* SR04_mock;
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
	EXPECT_CALL(*carMock, begin(_));        // Pass the gyroscope as an argument
	InSequence seq;
    // Everything below this has to happen in the specific sequence
    EXPECT_CALL(*SR04_mock, attach(51, 50));
    EXPECT_CALL(*SR04_mock, attach(45, 44));
	setup();
}

int main(int argc, char* argv[]) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}