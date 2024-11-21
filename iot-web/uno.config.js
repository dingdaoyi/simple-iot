import { defineConfig, presetUno } from 'unocss'

export default defineConfig({
  exclude: ['node_modules', '.git', './stats.html'],
  presets: [presetUno({ dark: 'class' })],
  shortcuts: {
    'wh-full': 'w-full h-full',
    'flex-center': 'flex justify-center items-center',
    'flex-col-center': 'flex-center flex-col',
    'flex-x-center': 'flex justify-center',
    'flex-y-center': 'flex items-center',
    w: 'mx-20px',
    // fw: 'w-full mx-auto min-w-1140px'
    fw: 'w-1440px mx-auto'
  },
  theme: {
    colors: {
      primary: 'var(--el-color-primary)',
      danger: 'var(--el-color-danger)',
      dwbg: 'var(--dw-bg)',
      dwboxbg: 'var(--dw-box-bg)'
    }
  }
})
