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

	// set user's token
	key := "users:" + user + ":token:" + device
	_, err := conn.Do("SET", key, token, "EX", "31536000")

	return err
}

// Is user's token (bind with device) correct?
func (dal *DAL) IsUserTokenCorrect(user, device, token string) (bool, error) {
	// connection from pool
	conn := dal.pool.Get()
	defer conn.Close()

	// get user's token
	key := "users:" + user + ":token:" + device
	t, err := redis.String(conn.Do("GET", key))
	if err != nil {
		return false, err
	}

	// compare
	return t == token, err
}
