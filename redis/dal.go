package redis

import (
	"github.com/garyburd/redigo/redis"
	"time"
)

// Date Access Layer for Redis
type DAL struct {
	pool *redis.Pool
}

// Create a new Date Access Layer for Redis,
// with given network address and password(optional).
func NewDAL(address, password string) *DAL {
	pool := newPool(address, password)
	return &DAL{pool}
}

// Create a new redigo connection pool.
// This function is taken from redigo's official document.
func newPool(address, password string) *redis.Pool {
	return &redis.Pool{
		MaxIdle:     3,
		IdleTimeout: 240 * time.Second,
		Dial: func() (redis.Conn, error) {
			c, err := redis.Dial("tcp", address)
			if err != nil {
				return nil, err
			}
			// only auth when password provided
			if password != "" {
				if _, err := c.Do("AUTH", password); err != nil {
					c.Close()
					return nil, err
				}
			}
			return c, err
		},
		TestOnBorrow: func(c redis.Conn, t time.Time) error {
			_, err := c.Do("PING")
			return err
		},
	}
}

// Set user's token bind with device.
func (dal *DAL) SetUserToken(user, device, token string) error {
	// connection from pool
	conn := dal.pool.Get()
	defer conn.Close()

	// set user's token which will be expired in 1 year
	key := userTokenKey(user, device)
	_, err := conn.Do("SET", key, token, "EX", "31536000")

	return err
}

// Is user's token (bind with device) correct?
func (dal *DAL) IsUserTokenCorrect(user, device, token string) (bool, error) {
	// connection from pool
	conn := dal.pool.Get()
	defer conn.Close()

	// get user's token
	key := userTokenKey(user, device)
	return redis.Bool(isUserTokenCorrect.Do(conn, key, token))
}

// Set user follow device with privilege.
func (dal *DAL) SetUserFollowDevice(user, device string, privilege int) error {
	// connection from pool
	conn := dal.pool.Get()
	defer conn.Close()

	// set user follow device
	key1 := userFollowingDevicesKey(user)
	key2 := deviceFollowerUsersKey(device)
	_, err := setUserFollowDevice.Do(conn, key1, key2, user, device, privilege)

	return err
}

// Set user un-follow device with privilege.
func (dal *DAL) SetUserUnFollowDevice(user, device string) error {
	// connection from pool
	conn := dal.pool.Get()
	defer conn.Close()

	// set user un-follow device
	key1 := userFollowingDevicesKey(user)
	key2 := deviceFollowerUsersKey(device)
	_, err := setUserUnFollowDevice.Do(conn, key1, key2, user, device)

	return err
}

// Is user follow device with at least privilege.
func (dal *DAL) IsUserFollowDevice(user, device string, privilege int) (bool, error) {
	// connection from pool
	conn := dal.pool.Get()
	defer conn.Close()

	// is user follow device
	key := deviceFollowerUsersKey(device)
	return redis.Bool(isUserFollowDevice.Do(conn, key, user, privilege))
}

// Get user's following devices.
func (dal *DAL) GetUserFollowingDevices(user string, privilegeMin, privilegeMax int) ([]string, error) {
	// connection from pool
	conn := dal.pool.Get()
	defer conn.Close()

	// get user's following devices
	key := userFollowingDevicesKey(user)
	return redis.Strings(conn.Do("ZRANGEBYSCORE", key, privilegeMin, privilegeMax))
}

// Get device's follower users.
func (dal *DAL) GetDeviceFollowerUsers(device string, privilegeMin, privilegeMax int) ([]string, error) {
	// connection from pool
	conn := dal.pool.Get()
	defer conn.Close()

	// get user's following devices
	key := deviceFollowerUsersKey(device)
	return redis.Strings(conn.Do("ZRANGEBYSCORE", key, privilegeMin, privilegeMax))
}
