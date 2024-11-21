import home from '@/views/home'
import layout from "@/layout"
import productType from '@/views/productType'

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
                },
            },
            {
                path: '/productType',
                name: '产品类型',
                component: productType,
                meta: {
                    title: '首页'
                },
            }
        ]
    }
]
