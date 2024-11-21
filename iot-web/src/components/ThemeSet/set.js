import { colord, extend } from 'colord'
import mixPlugin from 'colord/plugins/mix'

extend([mixPlugin])

/**
 * @description: hex 转 rgb
 * @param {*} color 颜色
 * @return {*}
 * @author: gumingchen
 */
export function hex2Rgb (color) {
  color = color.replace('#', '')
  const result = color.match(/../g)
  for (let i = 0; i < 3; i++) {
    result[i] = parseInt(result[i], 16)
  }
  return result
}

/**
 * @description: rgb 转 hex
 * @param {*} r g b 颜色
 * @return {*}
 * @author: gumingchen
 */
export function rgb2Hex (r, g, b) {
  const hexs = [r.toString(16), g.toString(16), b.toString(16)]
  for (let i = 0; i < 3; i++) {
    if (hexs[i].length === 1) {
      hexs[i] = '0' + hexs[i]
    }
  }
  const result = '#' + hexs.join('')
  return result
}

/**
 * @description: 使颜色变淡
 * @param {*} key
 * @return {*}
 * @author: gumingchen
 */
export function lighten (color, level) {
  const rgb = hex2Rgb(color)
  for (let i = 0; i < 3; i++) {
    rgb[i] = Math.floor((255 - rgb[i]) * level + rgb[i])
  }
  const result = rgb2Hex(rgb[0], rgb[1], rgb[2])
  return result
}

/**
 * @description: 使颜色变深
 * @param {*} key
 * @return {*}
 * @author: gumingchen
 */
export function darken (color, level) {
  const rgb = hex2Rgb(color)
  for (let i = 0; i < 3; i++) {
    rgb[i] = Math.floor(rgb[i] * (1 - level))
  }
  const result = rgb2Hex(rgb[0], rgb[1], rgb[2])
  return result
}

/**
 * 颜色混合
 * @param firstColor - 第一个颜色
 * @param secondColor - 第二个颜色
 * @param ratio - 第二个颜色占比
 */
export function mixColor (firstColor, secondColor, ratio) {
  return colord(firstColor).mix(secondColor, ratio).toHex()
}

export const themeColorList = [
  '#1890ff',
  '#409EFF',
  '#2d8cf0',
  '#007AFF',
  '#5ac8fa',
  '#5856D6',
  '#536dfe',
  '#9c27b0',
  '#AF52DE',
  '#0096c7',
  '#00C1D4',
  '#34C759',
  '#43a047',
  '#7cb342',
  '#c0ca33',
  '#78DEC7',
  '#e53935',
  '#d81b60',
  '#f4511e',
  '#fb8c00',
  '#ffb300',
  '#fdd835',
  '#6d4c41',
  '#546e7a'
]

export const defaultConfig = {
  elColorPrimary: '#167EFF',
  dwSliderActiveBg: '#465D83'
}
