# Server Baseline — Template

> ⚠️ **真实服务器档案不在仓库里。** 它包含 IP、端口、凭证位置等敏感信息，存放在维护者本机：
> - 本项目：`~/.simple-iot/SERVER.md`
> - `.gitignore` 已锁 `infra/SERVER.md` / `infra/SERVER.local.md` / `infra/server-*.md`，防误提交

> 这份模板是 **公开可见的目录结构**，告诉新接手的人服务器档案应该长什么样。
> 拿到 SSH 凭证后，按本模板填一份本机档案。

## 1. 基本信息
| 项 | 值 |
|---|---|
| 公网 IP | `<填写>` |
| 角色 | demo / staging / prod |
| 云厂 | aliyun / tencent / aws / 其他 |
| OS | Ubuntu 24.04 / etc |
| CPU/内存/硬盘 | `<填写>` |

## 2. 登录方式
- SSH 用户、端口、私钥路径
- 是否 NOPASSWD sudo

## 3. 部署目录
- 应用根目录、备份目录、日志目录
- git remote / 当前 commit

## 4. 运行中的服务
- 容器/进程清单 + 端口映射 + 暴露范围（公网/内网）

## 5. 防火墙
- iptables / ufw / 云厂安全组规则

## 6. 端口暴露全景
- 哪些端口对公网开放、是否合理

## 7. 已知问题 / 待修
- P0/P1/P2 分级

## 8. 常用运维命令
- 日志、重启、备份、还原、滚动升级

## 9. CI/CD 自动部署
- GitHub Actions secrets 列表
- workflow 触发条件

## 10. 凭证存放（仅记位置）

## 11. 故障应急
- 网站打不开 / 后端 502 / MQTT 连不上 / SSH 进不去 / 整体重装

## 历史变更日志
- 每次大改记一笔（接管、迁移、扩容、安全事件等）
