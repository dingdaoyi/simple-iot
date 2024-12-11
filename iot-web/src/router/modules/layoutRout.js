import home from '@/views/home'
import layout from '@/layout'
import productType from '@/views/productType'
import product from '@/views/product'
import protocol from '@/views/protocol'
import tslModel from '@/views/tslModel'
import icon from '@/views/system/icon'
import device from '@/views/device'

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
          title: '首页'
        }
      },
      {
        path: '/productType',
        name: '产品类型',
        component: productType,
        meta: {
          title: '首页'
        }
      },
      {
        path: '/tslModel',
        name: '物模型配置',
        component: tslModel,
        meta: {
          title: '物模型配置'
        }
      },
      {
        path: '/product',
        name: '产品管理',
        component: product,
        meta: {
          title: '产品管理'
        }
      },
      {
        path: '/device',
        name: '设备管理',
        component: device,
        meta: {
          title: '设备管理'
        }
      },
      {
        path: '/protocol',
        name: '协议管理',
        component: protocol,
        meta: {
          title: '协议管理'
        }
      },
      {
        path: '/icon',
        name: '图标管理',
        component: icon,
        meta: {
          title: '图标管理'
        }
      }
    ]
  }
]
