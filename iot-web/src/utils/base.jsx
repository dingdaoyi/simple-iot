export const activateOpts = [{
  label: '已激活',
  color: '#00B42A',
  value: true,
  render() {
    return <div style={{ color: this.color }}>{this.label}</div>
  },
}, {
  label: '未激活',
  color: '#A9A9A9',
  value: false,
  render() {
    return <div style={{ color: this.color }}>{this.label}</div>
  },
}]

export const onlineOpts = [{
  label: '在线',
  value: true,
  color: '#00B42A',
  render() {
    return <div style={{ color: this.color }}>{this.label}</div>
  },
}, {
  label: '离线',
  value: false,
  color: '#6E7279',
  render() {
    return <div style={{ color: this.color }}>{this.label}</div>
  },
}]
