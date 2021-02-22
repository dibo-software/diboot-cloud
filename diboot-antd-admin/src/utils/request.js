import axios from 'axios'
import store from '@/store'
import storage from 'store'
import qs from 'qs'
import notification from 'ant-design-vue/es/notification'
import { VueAxios } from './axios'
import { ACCESS_TOKEN, TOKEN_TYPE } from '@/store/mutation-types'
import { refreshToken, clearLoginResult } from '@/api/login'
import router from '@/router/index'
import { message } from 'ant-design-vue'

// baseURL
const BASE_URL = process.env.VUE_APP_API_BASE_URL
// token在Header中的key，需要与后端对diboot.iam.jwt-signkey配置相同
const JWT_HEADER_KEY = 'Authorization'
// 刷新token标记
let isTokenRefreshing = false
// 重试请求队列，将在刷新token过程中新进入的请求，挂起并记录到到该队列中
let waitingQueue = []

// 创建 axios 实例
const request = axios.create({
  // API 请求的默认前缀
  baseURL: BASE_URL,
  timeout: 6000 // 请求超时时间
})

// 异常拦截处理器
const errorHandler = (error) => {
  if (error.response) {
    const data = error.response.data
    // 从 localstorage 获取 token
    const token = storage.get(ACCESS_TOKEN)
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
request.interceptors.request.use(config => {
  // 设置请求头的token
  setAccessToken(config)
  // 只针对get方式进行序列化
  if (config.method === 'get') {
    config.paramsSerializer = function (params) {
      return qs.stringify(params, { arrayFormat: 'repeat' })
    }
  }
  return config
}, errorHandler)

// response interceptor
request.interceptors.response.use((response) => {
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
          return request(config)
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
          resolve(request(config))
        })
      })
    }
  }
  return response.data
}, errorHandler)

// 自定义dibootApi请求快捷方式
const dibootApi = {
  get (url, params) {
    return request.get(url, {
      params
    })
  },
  post (url, data) {
    return request({
      method: 'POST',
      url,
      data: JSON.stringify(data),
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      }
    })
  },
  put (url, data) {
    return request({
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
    return request({
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
    return request({
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
    return request({
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
    return request({
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

const installer = {
  vm: {},
  install (Vue) {
    Vue.use(VueAxios, request)
  }
}

function setAccessToken (config) {
  const accessToken = storage.get(ACCESS_TOKEN)
  const tokenType = storage.get(TOKEN_TYPE)
  if (tokenType && accessToken) {
    config.headers[JWT_HEADER_KEY] = `${tokenType} ${accessToken}` // 让每个请求携带自定义 token 请根据实际情况自行修改
  }
}

export default request

export {
  installer as VueAxios,
  request as axios,
  BASE_URL as baseURL,
  dibootApi
}
