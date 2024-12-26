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
        },
      },
      {
        path: '/productType',
        name: 'productType',
        // component: productType,
        meta: {
          title: '产品管理',
        },
        children: [
          {
            path: '/productType',
            name: '产品类型',
            component: productType,
            meta: {
              title: '首页',
            },
          },
          {
            path: '/protocol',
            name: '协议管理',
            component: protocol,
            meta: {
              title: '协议管理',
            },
          },
          {
            path: '/product',
            name: '产品管理',
            component: product,
            meta: {
              title: '产品管理',
            },
          },
          {
            path: '/rule',
            name: '规则处理',
            component: rule,
            meta: {
              title: '规则处理',
            },
          },
          {
            path: '/device',
            name: '设备管理',
            component: device,
            meta: {
              title: '设备管理',
            },
          },
          {
            path: '/tslModel',
            name: '物模型配置',
            component: tslModel,
            meta: {
              hidden: true,
              title: '物模型配置',
            },
          },
          {
            path: '/deviceDetails',
            name: '设备详情',
            component: deviceDetails,
            meta: {
              hidden: true,
              title: '设备详情',
            },
          },
        ],
      },
      {
        path: '/system',
        name: 'system',
        meta: {
          title: '系统管理',
        },
        children: [
          {
            path: '/icon',
            name: '图标管理',
            component: icon,
            meta: {
              title: '图标管理',
            },
          },
          {
            path: '/message',
            name: '消息通知',
            component: messageReceive,
            meta: {
              title: '消息通知',
            },
          },
        ],
      },
    ],
  },
]
