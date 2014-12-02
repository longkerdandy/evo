package socket

import ()

// Options (Configuration) for the server
type Options struct {
	Host string `json:"addr"`
	Port int    `json:"port"`
}

func processOptions(opts *Options) {
	if opts.Host == "" {
		opts.Host = DEFAULT_HOST
	}
	if opts.Port == 0 {
		opts.Port = DEFAULT_PORT
	}
}
