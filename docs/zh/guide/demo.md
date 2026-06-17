# 在线演示

> 免费公开的演示环境，让你在动手部署之前先体验一下 Simple IoT。

## 演示地址

**Demo URL：** <https://daily-expectations-listprice-reproductive.trycloudflare.com>

> 演示环境跑在北京区 2 核 2G 的腾讯云轻量服务器上，前面套了一层
> [Cloudflare Quick Tunnel](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/do-more-with-tunnels/trycloudflare/)，
> 顺手白嫖了 HTTPS，国内国外都能直连。**文章和 README 故意都指向本页面**，
> 这样以后演示地址变化（Quick Tunnel URL 在进程不重启时稳定，重启会换一个）
> 只需要更新这一页。
>
> 如果上面的链接挂了，裸 IP `http://122.51.129.91` 在境外可能仍然可用。

## 演示账号

| 用户名 | 密码 | 说明 |
|---|---|---|
| `admin` | `123456` | 完整管理员权限，请文明使用 |

> ⚠️ **这是管理员账号**，跟你自己 `./deploy.sh deploy` 出来的默认账号一模一样，
> 这样所有功能都能跑给你看。**为了不影响其他访客：**
>
> - 别修改密码（改了别人就登不进 — 但演示数据每天 03:00（UTC+8）会自动重置）
> - 别删除预置的设备 / 产品 / 规则 —— 仪表盘看起来"有内容"全靠这些
> - 别上传你不愿意贴公告栏的东西
>
> 滥用会让我把演示锁到邀请制账号。

## 可以看什么

- **仪表盘** — 实时看几个模拟设备的遥测数据（温度、湿度、压力）
- **设备管理** — 浏览设备列表、产品、属性
- **规则引擎** — 打开已有规则看看「拖拽 + 表达式」长什么样；可以克隆一份玩
- **协议驱动** — 脚本化解码器画廊（Java / JavaScript / Groovy / Lua）
- **历史数据** — 回放昨天 InfluxDB 里的数据

你**可以**新建 / 编辑 / 删除 —— 但别把预置内容清空。任何新建的东西凌晨都会
跟其他东西一起清掉。

## 故意限制了什么

- **MQTT Broker** —— `1883` 端口虽然开着但有限速，topic 也做了命名空间隔离（你看不到也接不到真实设备，但 broker 自己的监控指标是可见的）
- **InfluxDB / Postgres / RustFS** —— 仅暴露应用 UI，存储后端在防火墙后面
- **固件 OTA / 文件上传** —— 演示环境关闭，省带宽防滥用

## 想完整试用？

任何 2 GB 内存的 VPS，60 秒能跑起来：

```bash
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot
cp .env.example .env
./deploy.sh deploy
```

打开 `http://localhost`，用 `admin / 123456` 登录（**外网部署前请务必改密码！**），
就是完整的读写功能，没有演示限制，没有共享数据，也不会每晚被清。

## 反馈问题

如果演示挂了、卡了或者行为异常，提个 issue：
<https://github.com/dingdaoyi/simple-iot/issues>

（标题随便起，「demo is broken」就行。）
