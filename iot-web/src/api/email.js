import request from '@/utils/request'

// 获取邮箱配置列表
export function getEmailConfigList(params) {
  return request({
    url: '/email-config/page',
    method: 'post',
    data: params,
  })
}

// 获取默认邮箱配置
export function getDefaultEmailConfig() {
  return request({
    url: '/email-config/default',
    method: 'get',
  })
}

// 添加邮箱配置
export function addEmailConfig(data) {
  return request({
    url: '/email-config',
    method: 'post',
    data,
  })
}

// 更新邮箱配置
export function updateEmailConfig(data) {
  return request({
    url: '/email-config',
    method: 'put',
    data,
  })
}

// 删除邮箱配置
export function deleteEmailConfig(id) {
  return request({
    url: `/email-config/${id}`,
    method: 'delete',
  })
}

// 设置默认配置
export function setDefaultEmailConfig(id) {
  return request({
    url: `/email-config/default/${id}`,
    method: 'put',
  })
}

// 更新状态
export function updateEmailConfigStatus(id, status) {
  return request({
    url: `/email-config/status/${id}`,
    method: 'put',
    params: { status },
  })
}

// 发送测试邮件
export function sendTestEmail(data) {
  return request({
    url: '/notification/email/test',
    method: 'post',
    data,
  })
}
