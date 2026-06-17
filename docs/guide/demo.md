# Live Demo

> A free, public demo so you can poke at Simple IoT before installing anything.

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

## Demo credentials

| Username | Password | Notes |
|---|---|---|
| `admin` | `123456` | Full admin access — please be a polite guest |

> ⚠️ **This account has full admin permissions.** It's the same default
> `admin / 123456` you get from a fresh self-deploy, so the demo can show every
> feature. **Please be kind to other visitors:**
>
> - Don't change the password (you'll lock everyone else out — but the demo
>   resets nightly at 03:00 UTC+8)
> - Don't delete the seeded devices, products, or rules — those are what
>   makes the dashboard look populated
> - Don't upload anything you wouldn't post on a public bulletin board
>
> Repeated abuse will get the demo locked behind invite-only credentials.

## What to look at

- **Dashboard** — live telemetry from a few simulated devices (temperature, humidity, pressure)
- **Device Management** — browse the device list, products, attributes
- **Rule Engine** — open one of the existing rules to see how a drag-and-drop chain looks; feel free to clone one and tweak it
- **Protocol Drivers** — script-driven decoder gallery (Java / JavaScript / Groovy / Lua)
- **History** — replay yesterday's data from InfluxDB

You *can* create / edit / delete things — but please don't wipe the seeded
content. Anything you create gets cleaned up nightly along with everything
else.

## What's intentionally locked down

- **MQTT broker** — port `1883` is open but rate-limited and topics are namespaced (you can't connect a real device, but you can watch the broker's metrics)
- **InfluxDB / Postgres / RustFS** — only the application UI is exposed; storage backends are firewalled
- **Firmware OTA / file uploads** — disabled on the demo to save bandwidth and avoid abuse

## Want the full thing?

Spin up your own copy on any 2 GB VPS in 60 seconds:

```bash
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot
cp .env.example .env
./deploy.sh deploy
```

Open `http://localhost`, log in with `admin / 123456` (please change this
before exposing it to the internet!), and you have a full read-write platform
— no demo limits, no shared state, no nightly reset.

## Reporting issues

If the demo is down, slow, or behaving oddly, open an issue:
<https://github.com/dingdaoyi/simple-iot/issues>

(Don't worry about being polite — "demo is broken" is a perfectly fine title.)
