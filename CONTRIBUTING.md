# Contributing to Simple IoT

First off — **thank you** for considering a contribution. Every bug report, typo fix, translation, protocol script, UI tweak or feature PR helps make Simple IoT better for everyone.

[English](#english) · [简体中文](#简体中文)

---

<a id="english"></a>
## English

### Ways to contribute

- 🐛 **Report bugs** — open an [issue](https://github.com/dingdaoyi/simple-iot/issues/new/choose) with reproduction steps.
- 💡 **Suggest features** — start a [Discussion](https://github.com/dingdaoyi/simple-iot/discussions) before opening a PR for non-trivial work.
- 📖 **Improve docs** — typos, clarifications, translations are always welcome.
- 🔌 **Contribute protocol scripts** — share device drivers / decoders.
- 🎨 **UI / UX improvements** — components, themes, accessibility.
- 🌐 **Translations** — help us reach more users.

### Development setup

See the [**Quick Start**](README.md#-quick-start-60-seconds-with-docker) and [**Development**](README.md#-development) sections in the README. Coding conventions, component patterns and design tokens live in [`AGENTS.md`](AGENTS.md) — please read it before opening a frontend PR.

### Workflow

1. **Fork** this repo and clone your fork.
2. **Create a branch** from `main`:
   - `feat/<short-name>` for features
   - `fix/<short-name>` for bug fixes
   - `docs/<short-name>` for documentation
3. **Code & commit** following the rules below.
4. **Push** and open a Pull Request to `main`. Fill in the PR template.
5. Wait for CI ✅ and review. Address feedback. We squash-merge by default.

### Commit messages — Conventional Commits

Please follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>
```

Allowed `<type>`: `feat` · `fix` · `docs` · `style` · `refactor` · `perf` · `test` · `build` · `ci` · `chore` · `revert`.

Examples:

```
feat(rule-engine): add HTTP callback output node
fix(mqtt): correct topic dispatch when QoS=2
docs(readme): translate quick start to Spanish
```

### Code style

- **Java** — standard Spring Boot conventions, Lombok allowed, Sonar default rules. Run `mvn package` before pushing.
- **Vue / JS** — Composition API + `<script setup>`, ESLint config from `iot-web/.eslintrc.*`. Run `pnpm lint` before pushing.
- **CSS** — use design tokens from `iot-web/src/styles/var.scss` and `global.scss`. No hard-coded colors.

### Pull request checklist

- [ ] My code follows the existing style of this project.
- [ ] I have run the build locally and it passes.
- [ ] I have added/updated tests where applicable.
- [ ] I have updated `CHANGELOG.md` under `[Unreleased]` if user-visible.
- [ ] I have updated docs (README / AGENTS / inline comments) if needed.
- [ ] My commit messages follow Conventional Commits.

### Code of Conduct

By participating, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

### License

By contributing, you agree that your contributions will be licensed under the [Apache 2.0 License](LICENSE).

---

<a id="简体中文"></a>
## 简体中文

### 贡献方式

- 🐛 **报告 Bug** — 提 [Issue](https://github.com/dingdaoyi/simple-iot/issues/new/choose)，附复现步骤。
- 💡 **提建议** — 较大的功能先开 [Discussion](https://github.com/dingdaoyi/simple-iot/discussions) 讨论一下，再写 PR。
- 📖 **完善文档** — 错别字、表述不清、翻译都欢迎。
- 🔌 **贡献协议脚本** — 分享设备驱动 / 解码逻辑。
- 🎨 **UI / UX 改进** — 组件、主题、无障碍。
- 🌐 **翻译** — 帮助项目触达更多用户。

### 本地开发

参考 README 的 [**快速开始**](README.zh-CN.md#-快速开始docker60-秒) 与 [**本地开发**](README.zh-CN.md#-本地开发)。代码规范、组件模式、设计 Token 见 [`AGENTS.md`](AGENTS.md)，做前端改动前请先读一遍。

### 工作流

1. **Fork** 并 clone 自己的副本。
2. **建分支**（从 `main`）：
   - `feat/<简称>` — 功能
   - `fix/<简称>` — 修 Bug
   - `docs/<简称>` — 文档
3. **写代码 + 提交**，遵循下方规范。
4. **Push** 并向 `main` 提 PR，填好 PR 模板。
5. 等 CI ✅ 与 Review，按反馈修改。默认 Squash 合并。

### 提交信息 — Conventional Commits

格式：

```
<type>(<scope>): <subject>
```

`<type>` 可选：`feat` / `fix` / `docs` / `style` / `refactor` / `perf` / `test` / `build` / `ci` / `chore` / `revert`。

示例：

```
feat(rule-engine): 增加 HTTP 回调输出节点
fix(mqtt): 修复 QoS=2 时主题派发错误
docs(readme): 补充西班牙语快速开始
```

### 代码风格

- **Java** — 标准 Spring Boot 规范，可用 Lombok，Sonar 默认规则。提交前先 `mvn package` 通过。
- **Vue / JS** — Composition API + `<script setup>`，遵循 `iot-web/.eslintrc.*`。提交前 `pnpm lint`。
- **CSS** — 用 `iot-web/src/styles/var.scss` / `global.scss` 里的设计 Token，禁止硬编码颜色。

### PR 检查清单

- [ ] 代码风格与项目一致
- [ ] 本地构建通过
- [ ] 新增 / 修改了必要的测试
- [ ] 用户可见的变更已写入 `CHANGELOG.md` `[Unreleased]`
- [ ] 文档（README / AGENTS / 注释）已更新
- [ ] 提交信息遵循 Conventional Commits

### 行为准则

参与本项目即表示您同意遵守 [行为准则](CODE_OF_CONDUCT.md)。

### 协议

提交贡献即视为同意按 [Apache 2.0 协议](LICENSE) 授权。
