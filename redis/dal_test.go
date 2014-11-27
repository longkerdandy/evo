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
