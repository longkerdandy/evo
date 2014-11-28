package redis

import (
	"testing"
)

// Test set/get device's token.
func TestDeviceToken(t *testing.T) {
	// data access layer
	dal := NewDAL("127.0.0.1:6379", "")

	// set device's token
	if err := dal.SetUserToken("longkerdandy", "1234567890", "qwertyuiop"); err != nil {
		t.Error("test device token, token can't be set.", err)
	}
	if err := dal.SetUserToken("longkerdandy", "1234567890", "asdfghjkl"); err != nil {
		t.Error("test device token, token can't be set.", err)
	}
	if err := dal.SetUserToken("longkerdandy", "9876543210", "zxcvbnm"); err != nil {
		t.Error("test device token, token can't be set.", err)
	}

	// get device's token
	if b, err := dal.IsUserTokenCorrect("longkerdandy", "1234567890", "asdfghjkl"); err != nil || b != true {
		t.Error("test device token, token mismatch.", err)
	}
	if b, err := dal.IsUserTokenCorrect("longkerdandy", "9876543210", "zxcvbnm"); err != nil || b != true {
		t.Error("test device token, token mismatch.", err)
	}
	if b, err := dal.IsUserTokenCorrect("longkerdandy", "device_not_exist", "zxcvbnm"); b != false {
		t.Error("test device token, device not existed but token not empty.", err)
	}
}

// Test user/device follow relationship.
func TestUserDeviceFollow(t *testing.T) {
	// data access layer
	dal := NewDAL("127.0.0.1:6379", "")

	// set user follow device
	if err := dal.SetUserFollowDevice("longkerdandy", "1234567890", 1); err != nil {
		t.Error("test user follow device failed.", err)
	}

	// is user follow device
	if b, err := dal.IsUserFollowDevice("longkerdandy", "1234567890", 1); err != nil || b != true {
		t.Error("test is user follow device failed.", err)
	}
	if b, err := dal.IsUserFollowDevice("longkerdandy", "9876543210", 1); b != false {
		t.Error("test is user follow device, expect false but found true.", err)
	}

	// get user following
	if devices, err := dal.GetUserFollowingDevices("longkerdandy", 1, 3); err != nil {
		t.Error("test get user following devices failed.", err)
	} else {
		if !contains(devices, "1234567890") {
			t.Error("test get user following devices but result doesn't contain the device.")
		}
	}

	// get device followers
	if users, err := dal.GetDeviceFollowerUsers("1234567890", 1, 3); err != nil {
		t.Error("test get device follower users failed.", err)
	} else {
		if !contains(users, "longkerdandy") {
			t.Error("test get device follower users but result doesn't contain the user.")
		}
	}

	// set user un-follow device
	if err := dal.SetUserUnFollowDevice("longkerdandy", "1234567890"); err != nil {
		t.Error("test user un-follow device failed.", err)
	}
}

func contains(strs []string, target string) bool {
	for _, s := range strs {
		if s == target {
			return true
		}
	}
	return false
}
