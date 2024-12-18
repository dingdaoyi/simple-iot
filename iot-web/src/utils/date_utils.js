import dayjs from 'dayjs'

const DateUtils = {
  // 格式化日期
  formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
    return dayjs(date).format(format)
  },

  // 获取给定日期的月初时间
  getMonthStart(date = new Date()) {
    return dayjs(date).startOf('month').toDate()
  },

  // 获取给定日期的月末时间
  getMonthEnd(date = new Date()) {
    return dayjs(date).endOf('month').toDate()
  },

  // 获取给定日期的本日开始时间
  getStartOfDay(date = new Date()) {
    return dayjs(date).startOf('day').toDate()
  },

  // 获取给定日期的本日结束时间
  getEndOfDay(date = new Date()) {
    return dayjs(date).endOf('day').toDate()
  },

  // 获取本季度的开始时间
  getQuarterStart(date = new Date()) {
    return dayjs(date).startOf('quarter').toDate()
  },

  // 获取本季度的结束时间
  getQuarterEnd(date = new Date()) {
    return dayjs(date).endOf('quarter').toDate()
  },

  // 获取本年的开始时间
  getYearStart(date = new Date()) {
    return dayjs(date).startOf('year').toDate()
  },

  // 获取本年的结束时间
  getYearEnd(date = new Date()) {
    return dayjs(date).endOf('year').toDate()
  },

  // 获取本月的开始和结束时间（格式化为字符串）
  getLocalMonthStartEnd() {
    return {
      beginTime: this.formatDate(this.getMonthStart()),
      endTime: this.formatDate(this.getMonthEnd()),
    }
  },

  // 计算两个日期之间的时间差，返回 "XX小时XX分钟" 格式
  calculateTimeDifference(date1, date2) {
    const diff = dayjs(date2).diff(dayjs(date1))
    const duration = dayjs.duration(diff)
    const hours = duration.hours()
    const minutes = duration.minutes()
    return `${hours}小时${minutes}分`
  },
}

export { DateUtils }
