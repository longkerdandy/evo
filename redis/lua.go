package redis

import (
	"github.com/garyburd/redigo/redis"
)

// ==================== Redis Lua Script ====================

var (
	// KEYS[1]: userTokenKey ARGV[1]: token
	isUserTokenCorrect = redis.NewScript(1, `local token = redis.call('GET', KEYS[1]) if not not token and token == ARGV[1] then return true else return false end`)
	// KEYS[1]: userFollowingDevicesKey KEYS[2]: deviceFollowerUsersKey ARGV[1]: user id ARGV[2]: device id ARGV[3]: privilege
	setUserFollowDevice = redis.NewScript(2, `redis.call('ZADD', KEYS[1], ARGV[3], ARGV[2]) redis.call('ZADD', KEYS[2], ARGV[3], ARGV[1])`)
	// KEYS[1]: userFollowingDevicesKey KEYS[2]: deviceFollowerUsersKey ARGV[1]: user id ARGV[2]: device id
	setUserUnFollowDevice = redis.NewScript(2, `redis.call('ZREM', KEYS[1], ARGV[2]) redis.call('ZREM', KEYS[2], ARGV[1])`)
	// KEYS[1]: deviceFollowerUsersKey ARGV[1]: user id ARGV[2]: privilege
	isUserFollowDevice = redis.NewScript(1, `local privilege = redis.call('ZSCORE', KEYS[1], ARGV[1]) if not not privilege and privilege >= ARGV[2] then return true else return false end`)
)
