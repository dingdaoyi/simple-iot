# Deploy with Docker

## Quick start

```bash
# 1. Make the deploy script executable
chmod +x deploy.sh

# 2. One-shot deploy (checks/prompts InfluxDB install)
./deploy.sh deploy
```

## Commands

| Command | What it does |
|---|---|
| `./deploy.sh deploy` | Full first-time deploy (recommended) |
| `./deploy.sh start` | Start services |
| `./deploy.sh stop` | Stop services |
| `./deploy.sh restart` | Restart services |
| `./deploy.sh logs` | Tail combined logs |
| `./deploy.sh logs iot-server` | Tail backend logs |
| `./deploy.sh status` | Show service status |
| `./deploy.sh update` | Pull & redeploy |
| `./deploy.sh clean` | Wipe all data |
| `./deploy.sh influxdb` | Install InfluxDB 3 only |

## Manual deploy

```bash
# 1. Copy env template
cp .env.example .env

# 2. Install InfluxDB 3 if missing
./deploy.sh influxdb
# or manually:
curl -O https://www.influxdata.com/d/install_influxdb3.sh && sh install_influxdb3.sh

# 3. Edit .env (InfluxDB token, URL, database name)
vim .env

# 4. Build & start
docker compose up -d --build

# 5. Status / logs
docker compose ps
docker compose logs -f
```

## InfluxDB 3

InfluxDB 3 must be installed **on the host** (not inside Docker). The deploy script detects and prompts when missing.

### Manual install

```bash
# macOS / Linux
curl -O https://www.influxdata.com/d/install_influxdb3.sh && sh install_influxdb3.sh

# Start
influxdb3 serve --node-id=node0 --object-store=file --data-dir=~/.influxdb/data

# Create admin token
influxdb3 create token --admin
```

### Connection config

In `.env`:

```bash
# macOS — Docker reaching host services
INFLUXDB_URL=http://host.docker.internal:8181

# Linux — Docker reaching host services
INFLUXDB_URL=http://172.17.0.1:8181
```

## Service ports

| Service | Container port | Default host port |
|---|---|---|
| iot-server (REST) | 5010 | 5010 |
| iot-server (MQTT TCP) | 1883 | 1883 |
| iot-server (MQTT WS) | 8083 | 8083 |
| iot-web (Nginx) | 80 | 80 |
| postgres | 5432 | 5432 |
| rustfs | 9000 | 9000 |
| influxdb | 8181 | host network |

## Default credentials

```
admin / 123456
```

> **Change it before exposing the stack to a public network.**

## Common pitfalls

- **Port already in use** — adjust the host-side port in `docker-compose.yml`.
- **InfluxDB unreachable from container** — confirm the host IP works (use `host.docker.internal` on macOS, `172.17.0.1` on Linux).
- **PostgreSQL password mismatch** — `.env` and the existing volume must match. Wipe with `./deploy.sh clean` to rebuild.
- **Driver page dropdowns empty** — was a v0.1.0 bug, fixed; pull latest `main`.

## Upgrade

```bash
git pull
./deploy.sh update
```

This rebuilds the images that changed and restarts the services in place.
