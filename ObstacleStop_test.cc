#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "Smartcar.h" // The Smartcar library mocks

#include "../../src/ObstacleStop.ino" // Our production code

using ::testing::Return; // Needed to use "Return"
using ::testing::_; // Needed to use the "anything matcher"

class SmartcarObstacleStopFixture : public ::testing::Test
{
   public:
     SR04Mock* SR04_mock;
	 CarMock* carMock;
    // Run this before the tests
    virtual void SetUp()
    {
     SR04_mock = SR04MockInstance();
	 carMock = carMockInstance();
    }
    // Run this after the tests
    virtual void TearDown()
    {
      releaseCarMock();
      releaseSR04Mock();
    }
};

// Check that the sensor is attached and that the car is initialized
TEST_F(SmartcarObstacleStopFixture, initsAreCalled) {
    EXPECT_CALL(*carMock, begin());
    EXPECT_CALL(*SR04_mock, attach(_, _));
    setup();
}

// Check that the car stops when the distance is 1cm
TEST_F(SmartcarObstacleStopFixture, obstacleDetectedMinimum_stop) {
	EXPECT_CALL(*SR04_mock, getDistance())
	.WillOnce(Return(1));
	EXPECT_CALL(*carMock, setSpeed(0));
	loop();
}

// Check that the car stops when the distance is less than 15cm
TEST_F(SmartcarObstacleStopFixture, obstacleDetected_stop) {
	EXPECT_CALL(*SR04_mock, getDistance())
	.WillOnce(Return(7));
	EXPECT_CALL(*carMock, setSpeed(0));
	loop();
}

// Check that the car continues moving when the distance is exactly 15cm
TEST_F(SmartcarObstacleStopFixture, obstacleDetected_go) {
	EXPECT_CALL(*SR04_mock, getDistance())
	.WillOnce(Return(15));
	EXPECT_CALL(*carMock, setSpeed(50));
	loop();
}

int main(int argc, char* argv[]) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}