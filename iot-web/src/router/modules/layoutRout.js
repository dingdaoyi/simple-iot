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
  Setting,
  Tools,
  TrendCharts,
} from '@element-plus/icons-vue'
import layout from '@/layout'
import device from '@/views/device'
import deviceDetails from '@/views/device/widget/details.vue'
import home from '@/views/home'
import product from '@/views/product'
import productType from '@/views/productType'
import protocol from '@/views/protocol'
import rule from '@/views/rule'
import icon from '@/views/system/icon'
import messageReceive from '@/views/system/messageReceive'
import sms from '@/views/system/sms'
import email from '@/views/system/email'
import tslModel from '@/views/tslModel'

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
            component: () => import('@/views/driver/index.vue'),
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
            path: '/rule',
            name: 'rule',
            component: rule,
            meta: {
              title: '规则处理',
              icon: Operation,
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
        ],
      },
    ],
  },
]
