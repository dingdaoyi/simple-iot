// 高德地图配置文件，可扩展其他参数
export function useAmapConfig() {
  return {
    amapKey: import.meta.env.VITE_AMAP_KEY || '你的高德Key',
  }
}
