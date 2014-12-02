package socket

import "errors"

var (
	// ErrConnectionClosed represents error condition on a closed connection.
	ErrConnectionClosed = errors.New("Connection closed")

	// ErrAuthorization represents error condition on failed authorization.
	ErrAuthorization = errors.New("Authorization Error")
)
