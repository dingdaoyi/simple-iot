import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Simple IoT',
  description: 'The Minimal IoT Platform — self-hosted, single-binary, ready in 60 seconds.',
  cleanUrls: true,
  lastUpdated: true,
  base: '/simple-iot/',
  ignoreDeadLinks: [
    /^https?:\/\/localhost/,
    /^https?:\/\/127\.0\.0\.1/,
    /^https?:\/\/host\.docker\.internal/,
    /^https?:\/\/172\.\d+\.\d+\.\d+/,
  ],
  head: [
    ['link', { rel: 'icon', href: '/simple-iot/logo.svg' }],
    ['meta', { name: 'theme-color', content: '#42b883' }],
  ],
  themeConfig: {
    logo: '/logo.svg',
    socialLinks: [
      { icon: 'github', link: 'https://github.com/dingdaoyi/simple-iot' },
    ],
    search: { provider: 'local' },
    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2025-present Simple IoT contributors',
    },
  },
  locales: {
    root: {
      label: 'English',
      lang: 'en',
      themeConfig: {
        nav: [
          { text: 'Guide', link: '/guide/getting-started' },
          { text: 'API', link: '/api/openapi' },
          { text: 'Live Demo', link: '/guide/demo' },
          { text: 'Changelog', link: 'https://github.com/dingdaoyi/simple-iot/blob/main/CHANGELOG.md' },
        ],
        sidebar: {
          '/guide/': [
            {
              text: 'Try It',
              items: [
                { text: 'Live Demo', link: '/guide/demo' },
                { text: 'Getting Started', link: '/guide/getting-started' },
                { text: 'Architecture', link: '/guide/architecture' },
              ],
            },
            {
              text: 'Operations',
              items: [
                { text: 'Deploy with Docker', link: '/guide/deploy' },
                { text: 'MQTT Quick Test', link: '/guide/mqtt-test' },
                { text: 'SMS Integration', link: '/guide/sms' },
                { text: 'IM Push Config', link: '/guide/im-push' },
                { text: 'Schedule Trigger', link: '/guide/schedule-trigger' },
                { text: 'WebSocket Real-time', link: '/guide/websocket' },
                { text: 'Data Retention', link: '/guide/data-retention' },
              ],
            },
            {
              text: 'Device Management',
              items: [
                { text: 'Device Topology', link: '/guide/device-topology' },
                { text: 'Batch Commands', link: '/guide/batch-command' },
              ],
            },
          ],
          '/api/': [
            {
              text: 'REST API',
              items: [
                { text: 'OpenAPI', link: '/api/openapi' },
                { text: 'Dictionary Endpoints', link: '/api/dict' },
              ],
            },
          ],
        },
      },
    },
    zh: {
      label: '简体中文',
      lang: 'zh-CN',
      link: '/zh/',
      themeConfig: {
        nav: [
          { text: '指南', link: '/zh/guide/getting-started' },
          { text: 'API', link: '/zh/api/openapi' },
          { text: '在线演示', link: '/zh/guide/demo' },
          { text: '更新日志', link: 'https://github.com/dingdaoyi/simple-iot/blob/main/CHANGELOG.md' },
        ],
        sidebar: {
          '/zh/guide/': [
            {
              text: '试用',
              items: [
                { text: '在线演示', link: '/zh/guide/demo' },
                { text: '快速开始', link: '/zh/guide/getting-started' },
                { text: '架构总览', link: '/zh/guide/architecture' },
              ],
            },
            {
              text: '运维',
              items: [
                { text: 'Docker 部署', link: '/zh/guide/deploy' },
                { text: 'MQTT 快速测试', link: '/zh/guide/mqtt-test' },
                { text: '短信集成', link: '/zh/guide/sms' },
                { text: 'IM 推送配置', link: '/zh/guide/im-push' },
                { text: '定时规则触发', link: '/zh/guide/schedule-trigger' },
                { text: 'WebSocket 实时推送', link: '/zh/guide/websocket' },
                { text: '数据保留策略', link: '/zh/guide/data-retention' },
              ],
            },
            {
              text: '设备管理',
              items: [
                { text: '设备拓扑', link: '/zh/guide/device-topology' },
                { text: '批量设备指令', link: '/zh/guide/batch-command' },
              ],
            },
          ],
          '/zh/api/': [
            {
              text: 'REST API',
              items: [
                { text: 'OpenAPI', link: '/zh/api/openapi' },
                { text: '字典端点', link: '/zh/api/dict' },
              ],
            },
          ],
        },
        outline: { label: '本页目录' },
        docFooter: { prev: '上一篇', next: '下一篇' },
        lastUpdatedText: '最后更新',
        editLink: {
          pattern: 'https://github.com/dingdaoyi/simple-iot/edit/main/docs/:path',
          text: '在 GitHub 编辑此页',
        },
      },
    },
  },
})
