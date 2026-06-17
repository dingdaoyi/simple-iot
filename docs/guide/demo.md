# Live Demo

> A free, public read-only demo so you can poke at Simple IoT before installing anything.

## Where

**Demo URL:** <http://122.51.129.91>

> The demo runs on a 2 vCPU / 2 GB Tencent Cloud VM. There is no HTTPS yet —
> when a stable free domain is in place this page will be updated and the bare
> IP will keep redirecting. **Articles and READMEs link to this page on
> purpose**, so the demo address can move without breaking anything.

## Read-only credentials

| Username | Password | Permissions |
|---|---|---|
| `demo` | `demo123` | read-only on dashboard, devices, rules, history |

> ⚠️ Please don't change passwords or delete shared resources. The demo is
> wiped and reset every night at 03:00 (UTC+8), but please be kind to other
> visitors during the day.

## What you can do

- **Dashboard** — see live telemetry from a few simulated devices (temperature, humidity, pressure)
- **Device Management** — browse the device list, products, attributes
- **Rule Engine** — open one of the existing rules to see how a drag-and-drop chain looks
- **Protocol Drivers** — view the script-driven decoder gallery
- **History** — replay yesterday's data from InfluxDB

## What you can't do (yet)

- Create / delete / edit anything (read-only login)
- Connect a real MQTT device — the broker port `1883` is open but rate-limited and topics are namespaced
- Access InfluxDB / Postgres / RustFS directly — only the application UI is exposed
- File uploads / firmware OTA — disabled on the demo

## Want to try the full experience?

Spin up your own copy on any 2 GB VPS in 60 seconds:

```bash
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot
cp .env.example .env
./deploy.sh deploy
```

Open `http://localhost`, log in with `admin / admin`, and you have a full
read-write platform — no demo limits, no shared state.

## Reporting issues

If the demo is down, slow, or behaving oddly, open an issue:
<https://github.com/dingdaoyi/simple-iot/issues>

(Don't worry about being polite — "demo is broken" is a perfectly fine title.)
