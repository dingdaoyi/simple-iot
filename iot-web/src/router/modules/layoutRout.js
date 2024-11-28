import home from '@/views/home'
import layout from '@/layout'
import productType from '@/views/productType'
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
      }
    ]
  }
]
