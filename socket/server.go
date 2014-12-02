package socket

import (
	"fmt"
	"net"
	"os"
	"os/signal"
	"sync"
	"time"

	log "github.com/Sirupsen/logrus"
)

func init() {
	log.SetLevel(log.DebugLevel)
}

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
			log.WithFields(log.Fields{"signal": sig}).Debug("Trapped Signal;")
			log.Info("Server exiting...")
			os.Exit(0)
		}
	}()
}

// Protected check on running state
func (s *Server) isRunning() bool {
	s.mu.Lock()
	defer s.mu.Unlock()
	return s.running
}

// Start up the server, this will block.
// Start via a Go routine if needed.
func (s *Server) Start() {
	log.WithFields(log.Fields{"version": VERSION}).Info("Server starting...")
	s.running = true

	// Wait for clients.
	s.AcceptLoop()
}

// Shutdown will shutdown the server instance by kicking out the AcceptLoop
// and closing all associated clients.
func (s *Server) Shutdown() {
	s.mu.Lock()

	// Prevent issues with multiple calls.
	if !s.running {
		s.mu.Unlock()
		return
	}

	s.running = false

	conns := make(map[string]*client)

	// Copy off the clients
	for i, c := range s.clients {
		conns[i] = c
	}

	// Number of done channel responses we expect.
	doneExpected := 0

	// Kick client AcceptLoop()
	if s.listener != nil {
		doneExpected++
		s.listener.Close()
		s.listener = nil
	}

	s.mu.Unlock()

	// Close client connections
	for _, c := range conns {
		c.closeConnection()
	}

	// Block until the accept loops exit
	for doneExpected > 0 {
		<-s.done
		doneExpected--
	}
}

// AcceptLoop is exported for easier testing.
func (s *Server) AcceptLoop() {
	hp := fmt.Sprintf("%s:%d", s.opts.Host, s.opts.Port)
	log.WithFields(log.Fields{"address": hp}).Info("Listening for client connections;")
	ln, err := net.Listen("tcp", hp)
	if err != nil {
		log.WithFields(log.Fields{"address": hp, "error": err}).Info("Error listening on address;")
		return
	}

	log.Info("Server is ready.")

	// Setup state that can enable shutdown
	s.mu.Lock()
	s.listener = ln
	s.mu.Unlock()

	tmpDelay := ACCEPT_MIN_SLEEP

	for s.isRunning() {
		conn, err := ln.Accept()
		if err != nil {
			if ne, ok := err.(net.Error); ok && ne.Temporary() {
				log.WithFields(log.Fields{"sleeping": tmpDelay / time.Millisecond, "error": ne}).Debug("Temporary client accept error;")
				time.Sleep(tmpDelay)
				tmpDelay *= 2
				if tmpDelay > ACCEPT_MAX_SLEEP {
					tmpDelay = ACCEPT_MAX_SLEEP
				}
			} else if s.isRunning() {
				log.WithFields(log.Fields{"error": err}).Debug("Client accept error;")
			}
			continue
		}
		tmpDelay = ACCEPT_MIN_SLEEP
		s.createClient(conn)
	}
	log.Info("Server exiting...")
	s.done <- true
}

// Create a new client with given connection
func (s *Server) createClient(conn net.Conn) *client {
	c := &client{srv: s, nc: conn}

	// Grab lock
	c.mu.Lock()

	// Initialize
	c.initClient()

	log.Debug("Client connection created.")

	// Unlock to register
	c.mu.Unlock()

	return c
}

// Remove a client from our internal accounting.
func (s *Server) removeClient(c *client) {
	c.mu.Lock()
	did := c.did
	c.mu.Unlock()

	s.mu.Lock()
	delete(s.clients, did)
	s.mu.Unlock()
}

/////////////////////////////////////////////////////////////////
// These are some helpers for accounting in functional tests.
/////////////////////////////////////////////////////////////////

// NumClients will report the number of registered clients.
func (s *Server) NumClients() int {
	s.mu.Lock()
	defer s.mu.Unlock()
	return len(s.clients)
}

// Addr will return the net.Addr object for the current listener.
func (s *Server) Addr() net.Addr {
	s.mu.Lock()
	defer s.mu.Unlock()
	if s.listener == nil {
		return nil
	}
	return s.listener.Addr()
}
