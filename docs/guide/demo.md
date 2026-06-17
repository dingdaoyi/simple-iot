# Live Demo

> A free, public read-only demo so you can poke at Simple IoT before installing anything.

## Where

**Demo URL:** <https://daily-expectations-listprice-reproductive.trycloudflare.com>

> The demo runs on a 2 vCPU / 2 GB Tencent Cloud VM in Beijing, fronted by a
> [Cloudflare Quick Tunnel](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/do-more-with-tunnels/trycloudflare/)
> so you get free HTTPS and a reachable URL no matter which side of the GFW you
> sit on. **Articles and READMEs link to this page on purpose**, so when the
> demo URL changes (Quick Tunnel hostnames are stable while the process runs but
> rotate on restart) only this one page needs updating.
>
> If the link above is dead, the bare IP `http://122.51.129.91` may still work
> from outside mainland China.

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
