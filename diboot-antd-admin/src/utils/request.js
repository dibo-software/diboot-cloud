import Vue from 'vue'
import axios from 'axios'
import store from '@/store'
import {
  VueAxios
} from './axios'
import notification from 'ant-design-vue/es/notification'
import {
  ACCESS_TOKEN, TOKEN_TYPE
} from '@/store/mutation-types'
import router from '@/router/index'
import qs from 'qs'
import { refreshToken, clearLoginResult } from '@/api/login'
import { message } from 'ant-design-vue'

// baseURL
const BASE_URL = '/api'
// token在Header中的key，需要与后端对diboot.iam.jwt-signkey配置相同
const JWT_HEADER_KEY = 'Authorization'
// 获取token的接口
const TOKEN_API_SUFFIX = '/oauth/token'
// token自动刷新（发送心跳）的时间间隔（分钟），建议为后端配置的token过期时间的1/8
const TOKEN_REFRESH_EXPIRE = 10
// 刷新token标记
let isTokenRefreshing = false
// 重试请求队列，将在刷新token过程中新进入的请求，挂起并记录到到该队列中
let waitingQueue = []

// 创建 axios 实例
const service = axios.create({
  baseURL: BASE_URL, // api base_url
  timeout: 6000 // 请求超时时间
})

const err = (error) => {
  if (error.response) {
    const data = error.response.data
    const token = Vue.ls.get(ACCESS_TOKEN)
    if (error.response.status === 403) {
      notification.error({
        message: 'Forbidden',
        description: data.message
      })
    }
    if (error.response.status === 401 && !(data.result && data.result.isLogin)) {
      notification.error({
        message: 'Unauthorized',
        description: 'Authorization verification failed'
      })
      if (token) {
        store.dispatch('Logout').then(() => {
          setTimeout(() => {
            window.location.reload()
          }, 1500)
        })
      }
    }
  }
  return Promise.reject(error)
}

// request interceptor
service.interceptors.request.use(config => {
  // 设置请求头的token
  setAccessToken(config)
  // 只针对get方式进行序列化
  if (config.method === 'get') {
    config.paramsSerializer = function (params) {
      return qs.stringify(params, { arrayFormat: 'repeat' })
    }
  }
  return config
}, err)

// response interceptor
service.interceptors.response.use((response) => {
  // 如果返回的自定义状态码为 4001， 则token过期，需要通过refresh_token重新获取token
  if (response.data && response.data.code === 4001) {
    const config = response.config
    if (!isTokenRefreshing) {
      isTokenRefreshing = true
      // 提示自动登录，不提示可将此行注释
      const hideMessage = message.loading('自动登录中...', 0)
      return refreshToken()
        .then(() => {
          // 设置重发请求的请求头
          setAccessToken(config)
          // 关闭自动登录提示，不提示可将此行注释
          hideMessage()
          message.success('登录成功', 2.5)
          // 重新获取token成功后，如果队列中有请求，则依此重发请求，并清空等待队列
          waitingQueue.forEach(func => func())
          waitingQueue = []
          // 重发当前请求
          return service(config)
        })
        .catch(() => {
          // 清除登录信息
          clearLoginResult()
          // 关闭自动登录提示，不提示可将此行注释
          hideMessage()
          message.warning('登录失败，请重新登录', 2.5)
          // 跳转至登录页
          router.push({
            path: '/login',
            query: { redirect: router.currentRoute.fullPath }
          })
          return Promise.reject(new Error('请重新登录'))
        })
        .finally(() => {
          isTokenRefreshing = false
        })
    } else {
      // 返回一个未resolve的promise
      return new Promise(resolve => {
        // 将执行函数放入等待队列
        waitingQueue.push(() => {
          setAccessToken(config)
          resolve(service(config))
        })
      })
    }
  }

  if (response.data && response.data.code === 5000) {
    message.success('登录失败，请重新登录', 2.5)
    router.push({
      path: '/login',
      query: { redirect: router.currentRoute.fullPath }
    })
    return Promise.reject(new Error('请重新登录'))
  }

  // 如果当前请求是下载请求
  if (response.headers.filename) {
    return {
      data: response.data,
      filename: decodeURI(response.headers.filename),
      code: parseInt(response.headers['err-code'] || '0'),
      msg: decodeURI(response.headers['err-msg'] || '')
    }
  }
  return response.data
}, err)

// 自定义dibootApi请求快捷方式
const dibootApi = {
  get (url, params) {
    return service.get(url, {
      params
    })
  },
  post (url, data) {
    return service({
      method: 'POST',
      url,
      data: JSON.stringify(data),
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      }
    })
  },
  put (url, data) {
    return service({
      method: 'PUT',
      url,
      data: JSON.stringify(data),
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      }
    })
  },
  /**
   * 删除
   * @param url
   * @param params
   * @returns {AxiosPromise}
   */
  delete (url, params) {
    return service({
      url,
      method: 'DELETE',
      params,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
  },
  /***
   * 上传文件接口
   * @param url
   * @param formData
   * @returns {AxiosPromise}
   */
  upload (url, formData) {
    return service({
      url,
      method: 'POST',
      data: formData
    })
  },
  /**
   * GET下载文件
   * @param url
   * @param data
   * @returns {AxiosPromise}
   */
  download (url, params) {
    return service({
      url,
      method: 'GET',
      responseType: 'arraybuffer',
      observe: 'response',
      params,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
  },
  /**
   * POST下载文件（常用于提交json数据下载文件）
   * @param url
   * @param data
   * @returns {AxiosPromise}
   */
  postDownload (url, data) {
    return service({
      url,
      method: 'POST',
      responseType: 'arraybuffer',
      observe: 'response',
      data: JSON.stringify(data),
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      withCredentials: true
    })
  }
}

function setAccessToken (config) {
  const accessToken = Vue.ls.get(ACCESS_TOKEN)
  const tokenType = Vue.ls.get(TOKEN_TYPE)
  if (tokenType && accessToken) {
    config.headers[JWT_HEADER_KEY] = `${tokenType} ${accessToken}` // 让每个请求携带自定义 token 请根据实际情况自行修改
  }
}

const installer = {
  vm: {},
  install (Vue) {
    Vue.use(VueAxios, service)
  }
}

export {
  installer as VueAxios,
  service as axios,
  BASE_URL as baseURL,
  dibootApi
}
