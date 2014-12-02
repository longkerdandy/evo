package socket

import (
	"bufio"
	"net"
	"sync"
)

const (
	// The size of the bufio reader/writer on top of the socket.
	defaultBufSize = 32768
)

// client represents a device's connection
type client struct {
	mu  sync.Mutex
	nc  net.Conn
	did string // deivice id
	uid string // user id
	bw  *bufio.Writer
}

// Lock should be held
func (c *client) initClient() {
	c.bw = bufio.NewWriterSize(c.nc, defaultBufSize)

	// Spin up the read loop.
	go c.readLoop()
}

func (c *client) readLoop() {
	// Grab the connection off the c, it will be cleared on a close.
	// We check for that after the loop, but want to avoid a nil dereference
	c.mu.Lock()
	nc := c.nc
	c.mu.Unlock()

	if nc == nil {
		return
	}

	b := make([]byte, defaultBufSize)

	for {
		n, err := nc.Read(b)
		if err != nil {
			c.closeConnection()
			return
		}
		if err := c.parse(b[:n]); err != nil {
			// Auth was handled inline
			if err != ErrAuthorization {
				c.closeConnection()
			}
			return
		}
		// Check to see if we got closed, e.g. slow consumer
		if c.nc == nil {
			return
		}
	}
}

func (c *client) closeConnection() {
	c.mu.Lock()
	if c.nc == nil {
		c.mu.Unlock()
		return
	}

	c.clearConnection()

	c.nc = nil

	c.mu.Unlock()
}

// Lock should be held
func (c *client) clearConnection() {
	if c.nc == nil {
		return
	}
	c.bw.Flush()
	c.nc.Close()
}

func (c *client) parse(buf []byte) error {
	return nil
}
