package app

import (
	deliveryHttp "geoqq/internal/delivery/http"
	"geoqq/internal/server"
	"geoqq/internal/storage"
	storagePostgre "geoqq/internal/storage/impl/sql/postgre"

	"github.com/spf13/viper"
)

type App struct {
	server *server.Server
}

func NewApp() (*App, error) {

	// *** storage

	// *** service

	// *** delivery

	deliveryHttpDeps := deliveryHttp.Dependencies{
		Services: nil,
	}
	httpHandler, err := deliveryHttp.NewHandler(deliveryHttpDeps)
	if err != nil {
		return nil, err
	}

	// *** server

	serverDeps := server.Dependencies{
		Engine: httpHandler.GetEngine(),
	}

	server, err := server.NewServer(serverDeps)
	if err != nil {
		return nil, err
	}

	return &App{
		server: server,
	}, nil
}

func (a *App) Run() error {
	return a.server.Start()
}

// private
// -----------------------------------------------------------------------

func storageInstance() storage.Storage {
	if viper.GetString("") == "postgre" {
		return storagePostgre.NewStorage(storagePostgre.Dependencies{})
	}

	return nil
}
