package redis

// ==================== Redis Keys ====================

// User Token --- String
func userTokenKey(user, device string) string {
	return "users:" + user + ":token:" + device
}

// User Following Devices --- Sorted Set
func userFollowingDevicesKey(user string) string {
	return "users:" + user + ":following:devices"
}

// Deivce Follower Users --- Sorted Set
func deviceFollowerUsersKey(device string) string {
	return "devices:" + device + ":follower:users"
}
