// Element Plus 图标
import {
  Bell,
  Box,
  ChatDotRound,
  Connection,
  Document,
  House,
  Message,
  Operation,
  Picture,
  Promotion,
  Setting,
  Tools,
  TrendCharts,
  Warning,
} from '@element-plus/icons-vue'
import layout from '@/layout'

const home = () => import('@/views/home')
const productType = () => import('@/views/productType')
const protocol = () => import('@/views/protocol')
const product = () => import('@/views/product')
const device = () => import('@/views/device')
const tslModel = () => import('@/views/tslModel')
const deviceDetails = () => import('@/views/device/widget/details.vue')
const icon = () => import('@/views/system/icon')
const sms = () => import('@/views/system/sms')
const email = () => import('@/views/system/email')
const messageReceive = () => import('@/views/system/messageReceive')
const pushConfig = () => import('@/views/push-config/index.vue')
const driver = () => import('@/views/driver/index.vue')
const ruleChain = () => import('@/views/rule-chain/index.vue')
const ruleChainEditor = () => import('@/views/rule-chain/editor/index.vue')
const alarm = () => import('@/views/alarm/index.vue')

export default [
  {
    path: '/',
    name: 'layout',
    component: layout,
    children: [
      {
        path: '/home',
        name: 'home',
        component: home,
        meta: {
          title: '首页',
          icon: House,
        },
      },
      {
        path: '/productType',
        name: 'productType',
        meta: {
          title: '产品管理',
          icon: Box,
        },
        children: [
          {
            path: '/productType',
            name: 'productTypePage',
            component: productType,
            meta: {
              title: '产品类型',
              icon: Box,
            },
          },
          {
            path: '/driver',
            name: 'driver',
            component: driver,
            meta: {
              title: '驱动管理',
              icon: Tools,
            },
          },
          {
            path: '/protocol',
            name: 'protocol',
            component: protocol,
            meta: {
              title: '协议管理',
              icon: Connection,
            },
          },
          {
            path: '/product',
            name: 'product',
            component: product,
            meta: {
              title: '产品管理',
              icon: TrendCharts,
            },
          },
          {
            path: '/device',
            name: 'device',
            component: device,
            meta: {
              title: '设备管理',
              icon: Document,
            },
          },
          {
            path: '/tslModel',
            name: 'tslModel',
            component: tslModel,
            meta: {
              hidden: true,
              title: '物模型配置',
              icon: Setting,
            },
          },
          {
            path: '/deviceDetails',
            name: 'deviceDetails',
            component: deviceDetails,
            meta: {
              hidden: true,
              title: '设备详情',
              icon: Document,
            },
          },
          {
            path: '/rule-chain',
            name: 'ruleChain',
            component: ruleChain,
            meta: {
              title: '规则引擎',
              icon: Operation,
            },
          },
          {
            path: '/rule-chain/editor',
            name: 'ruleChainEditor',
            component: ruleChainEditor,
            meta: {
              hidden: true,
              title: '规则引擎编辑器',
              icon: Operation,
            },
          },
          {
            path: '/rule-chain/editor/:id',
            name: 'ruleChainEditorEdit',
            component: ruleChainEditor,
            meta: {
              hidden: true,
              title: '编辑规则引擎',
              icon: Operation,
            },
          },
          {
            path: '/alarm',
            name: 'alarm',
            component: alarm,
            meta: {
              title: '告警管理',
              icon: Warning,
            },
          },
        ],
      },
      {
        path: '/system',
        name: 'system',
        meta: {
          title: '系统管理',
          icon: Setting,
        },
        children: [
          {
            path: '/icon',
            name: 'iconManagement',
            component: icon,
            meta: {
              title: '图标管理',
              icon: Picture,
            },
          },
          {
            path: '/sms',
            name: 'sms',
            component: sms,
            meta: {
              title: '短信配置',
              icon: ChatDotRound,
            },
          },
          {
            path: '/email',
            name: 'email',
            component: email,
            meta: {
              title: '邮箱配置',
              icon: Message,
            },
          },
          {
            path: '/message',
            name: 'message',
            component: messageReceive,
            meta: {
              title: '消息通知',
              icon: Bell,
            },
          },
          {
            path: '/push-config',
            name: 'pushConfig',
            component: pushConfig,
            meta: {
              title: '推送配置',
              icon: Promotion,
            },
          },
        ],
      },
    ],
  },
]
