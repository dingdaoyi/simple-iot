import home from '@/views/home'
import layout from "@/layout"
export default [

  {
    path: '/',
    name: 'layout',
    component: layout,
    children:[
      {
        path: '/home',
        name: 'home',
        component: home,
        meta: {
          title: '首页'
        },
      },
      {
        path: '/productType',
        name: '产品类型',
        component: home,
        meta: {
          title: '首页'
        },
      }
    ]
  }
]
