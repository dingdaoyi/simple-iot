# 在线演示

> 免费公开的只读演示环境，让你在动手部署之前先体验一下 Simple IoT。

## 演示地址

**Demo URL：** <https://daily-expectations-listprice-reproductive.trycloudflare.com>

> 演示环境跑在北京区 2 核 2G 的腾讯云轻量服务器上，前面套了一层
> [Cloudflare Quick Tunnel](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/do-more-with-tunnels/trycloudflare/)，
> 顺手白嫖了 HTTPS，国内国外都能直连。**文章和 README 故意都指向本页面**，
> 这样以后演示地址变化（Quick Tunnel URL 在进程不重启时稳定，重启会换一个）
> 只需要更新这一页。
>
> 如果上面的链接挂了，裸 IP `http://122.51.129.91` 在境外可能仍然可用。

## 只读账号

| 用户名 | 密码 | 权限范围 |
|---|---|---|
| `demo` | `demo123` | 仪表盘 / 设备 / 规则 / 历史数据，只读 |

> ⚠️ 请不要修改密码或删除共享资源。演示数据每天 03:00（UTC+8）自动重置，
> 但白天还请互相尊重一下别的访问者。

## 可以做什么

- **仪表盘** — 实时看几个模拟设备的遥测数据（温度、湿度、压力）
- **设备管理** — 浏览设备列表、产品、属性
- **规则引擎** — 打开已有规则看看「拖拽 + 表达式」长什么样
- **协议驱动** — 看脚本化解码器画廊
- **历史数据** — 回放昨天 InfluxDB 里的数据

## 不能做什么（暂时）

- 新建 / 删除 / 修改任何内容（只读账号）
- 接入真实 MQTT 设备 —— `1883` 端口虽然开着但有限速，topic 也做了命名空间隔离
- 直接访问 InfluxDB / Postgres / RustFS —— 仅暴露应用 UI
- 文件上传 / 固件 OTA —— 演示环境关闭

## 想完整试用？

任何 2 GB 内存的 VPS，60 秒能跑起来：

```bash
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot
cp .env.example .env
./deploy.sh deploy
```

打开 `http://localhost`，用 `admin / admin` 登录，**完整的读写功能**，没有演示限制，没有共享数据。

## 反馈问题

如果演示挂了、卡了或者行为异常，提个 issue：
<https://github.com/dingdaoyi/simple-iot/issues>

（标题随便起，「demo is broken」就行。）
