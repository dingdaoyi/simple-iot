import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'url'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import DefineOptions from 'unplugin-vue-define-options/vite'
import path from 'path'
import Unocss from 'unocss/vite'

const configProxy = {
  local: {
    iot: 'http://localhost:5010'
  },
  line: {
    iot: 'http://106.13.72.27:5010'
  }
}

const getProxy = (name) => {
  return {
    '/iot': {
      target: configProxy[name].iot,
      changeOrigin: true
    }
  }
}

export default defineConfig(({ command, mode }) => {
  let alias = {
    '@': fileURLToPath(new URL('./src', import.meta.url))
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
      proxy: getProxy('local'), // local：本地， line: 线上
      fs: {
      // 可以为项目根目录的上一级提供服务
        allow: ['..']
      }
    }
  }
})
