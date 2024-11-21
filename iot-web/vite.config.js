import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'url'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import DefineOptions from 'unplugin-vue-define-options/vite'
import path from 'path'
import Unocss from 'unocss/vite'

const configProxy = {
  test: {
    iot: 'http://admin-v2.diweiyunlian.cn',
    ucenter: 'http://admin-v2.diweiyunlian.cn',
    monitor: 'http://admin-v2.diweiyunlian.cn',
    social: 'http://wbfwweb.diweiyunlian.cn'
  },
  ysc: {
    iot: 'http://alpha.center.diweiyunlian.cn',
    ucenter: 'http://alpha.center.diweiyunlian.cn',
    monitor: 'http://alpha.center.diweiyunlian.cn'
  },
  line: {
    iot: 'https://xzy.diweiyunlian.cn/',
    ucenter: 'https://xzy.diweiyunlian.cn/',
    monitor: 'https://xzy.diweiyunlian.cn/'
  }
}

const getProxy = (name) => {
  return {
    '/ucenter': {
      target: configProxy[name].ucenter,
      changeOrigin: true
    },
    '/monitor': {
      target: configProxy[name].monitor,
      changeOrigin: true
    },
    '/social': {
      target: configProxy[name].social,
      changeOrigin: true
    },
    '/iot': {
      target: configProxy[name].iot,
      changeOrigin: true,
      // rewrite: (path) => path.replace(/^\/iot/, '')
      pathRewrite: {
        '^/iot': ''
      }
    },
    '/v2': {
      target: 'https://api.map.baidu.com/place/',
      changeOrigin: true,
      pathRewrite: {
        '^/v2': ''
      }
    }
  }
}

export default defineConfig(({ command, mode }) => {
  let alias = {
    '@': fileURLToPath(new URL('./src', import.meta.url))
  }
  if (mode === 'dwui') {
    alias = {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '~': fileURLToPath(new URL('../dwyl-ui/packages', import.meta.url)),
      'dwyl-ui': fileURLToPath(new URL('../dwyl-ui/packages/index.js', import.meta.url))
    }
  }

  return {
    plugins: [
      vue(),
      DefineOptions(),
      vueJsx(),
      Unocss(),
      createSvgIconsPlugin({
      // 指定需要缓存的图标文件夹
        iconDirs: [path.resolve(process.cwd(), 'src/icons')],
        // 指定symbolId格式
        symbolId: 'icon-[dir]-[name]'
      })
    ],
    css: {
      preprocessorOptions: {
        scss: {
          // additionalData: '@import "./src/styles/var.scss";' // 全局变量
        }
      }
    },
    define: {
      'process.env': {}
    },
    resolve: {
      alias,
      extensions: ['.js', '.jsx', '.json', '.vue', '.css', '.scss']
    },
    server: {
      host: '0.0.0.0',
      port: 9999,
      hmr: true, // 启动热更新
      proxy: getProxy('line'), // test：测试， line: 线上， dyw：丁云伟
      fs: {
      // 可以为项目根目录的上一级提供服务
        allow: ['..']
      }
    }
  }
})
