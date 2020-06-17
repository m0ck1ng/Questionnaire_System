import request from '@/utils/request'

export function addForm(data) {
  return request({
    url: '/forms',
    method: 'post',
    data
  })
}

export function queryForm(id) {
  return request({
    url: '/open/forms',
    method: 'get',
    params: { id }
  })
}

export function getAllForms(token) {
  return request({
    url: '/forms',
    method: 'get',
    params: { token }
  })
}

export function deleteForm(qid, token) {
  return request({
    url: '/forms',
    method: 'delete',
    params: { qid, token }
  })
}

export function releaseForm(qid, token, state, max_time = null) {
  return request({
    url: '/forms',
    method: 'patch',
    params: { qid, token, state, max_time }
  })
}
