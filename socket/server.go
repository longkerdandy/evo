package socket

import (
	"net"
	"sync"
	"time"
)

// Server represents a socket server (TCP WS)
type Server struct {
	mu       sync.Mutex
	opts     *Options
	running  bool
	listener net.Listener
	clients  map[string]*client
	done     chan bool
	start    time.Time
}

// New will setup a new server struct after parsing the options.
func New(opts *Options) *Server {
	processOptions(opts)

	s := &Server{
		opts:  opts,
		done:  make(chan bool, 1),
		start: time.Now(),
	}

	s.mu.Lock()
	defer s.mu.Unlock()

	// For tracking clients
	s.clients = make(map[string]*client)

	s.handleSignals()

	return s
}

// Signal Handling
func (s *Server) handleSignals() {
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt)
	go func() {
		for sig := range c {
			Debugf("Trapped Signal; %v", sig)
			// FIXME, trip running?
			Noticef("Server Exiting..")
			os.Exit(0)
		}
	}()
}
