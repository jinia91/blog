#!/bin/bash

# Local infrastructure management script

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose-local.yml"

case "$1" in
  start)
    echo "Starting local infrastructure..."
    docker-compose -f "$COMPOSE_FILE" up -d
    echo ""
    echo "Waiting for services to be healthy..."
    sleep 5
    echo ""
    echo "Infrastructure started!"
    echo "  - MySQL:         localhost:3307"
    echo "  - Redis:         localhost:8882"
    echo "  - Elasticsearch: localhost:9200"
    echo "  - ChromaDB:      localhost:8000"
    ;;
  stop)
    echo "Stopping local infrastructure..."
    docker-compose -f "$COMPOSE_FILE" down
    echo "Infrastructure stopped!"
    ;;
  restart)
    $0 stop
    $0 start
    ;;
  status)
    docker-compose -f "$COMPOSE_FILE" ps
    ;;
  logs)
    docker-compose -f "$COMPOSE_FILE" logs -f ${@:2}
    ;;
  clean)
    echo "Stopping and removing all data..."
    docker-compose -f "$COMPOSE_FILE" down -v
    echo "All data removed!"
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status|logs|clean}"
    echo ""
    echo "Commands:"
    echo "  start   - Start all infrastructure services"
    echo "  stop    - Stop all infrastructure services"
    echo "  restart - Restart all infrastructure services"
    echo "  status  - Show status of services"
    echo "  logs    - Show logs (optionally specify service name)"
    echo "  clean   - Stop services and remove all data volumes"
    exit 1
    ;;
esac
