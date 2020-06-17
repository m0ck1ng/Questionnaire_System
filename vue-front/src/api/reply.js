import request from '@/utils/request'

export function addReply(data) {
  return request({
    url: '/open/replys',
    method: 'post',
    data
  })
}

export function getReplyStatis(token, qid) {
  return request({
    url: '/replys',
    method: 'get',
    params: { token, qid }
  })
}

import store from '@/store'
import axios from 'axios'
import { getToken } from '@/utils/auth'
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    if (store.getters.token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['X-Token'] = getToken()
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)
export function downloadReplys(token, qid) {
  return service({
    url: '/replys/download',
    method: 'get',
    params: { token, qid },
    responseType: 'blob'
  })
}
