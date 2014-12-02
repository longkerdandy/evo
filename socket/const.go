package socket

import (
	"time"
)

const (
	// VERSION is the current version for the server.
	VERSION = "0.1.0"

	// DEFAULT_PORT is the deault port for client connections.
	DEFAULT_PORT = 4222

	// DEFAULT_HOST defaults to all interfaces.
	DEFAULT_HOST = "0.0.0.0"

	// ACCEPT_MIN_SLEEP is the minimum acceptable sleep times on temporary errors.
	ACCEPT_MIN_SLEEP = 10 * time.Millisecond

	// ACCEPT_MAX_SLEEP is the maximum acceptable sleep times on temporary errors
	ACCEPT_MAX_SLEEP = 1 * time.Second
)
