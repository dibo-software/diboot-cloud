import request from '@/utils/request'
import storage from 'store'
import store from '@/store'
import qs from 'qs'
import { ACCESS_TOKEN, REFRESH_TOKEN, TOKEN_EXPIRES_TIME, TOKEN_TYPE } from '@/store/mutation-types'

const userApi = {
  Login: '/auth-server/oauth/token',
  Logout: '/auth-server/oauth/token',
  // get my info
  UserInfo: '/auth-server/oauth/userInfo'
}

/**
 * login func
 * parameter: {
 *     username: '',
 *     password: '',
 *     remember_me: true,
 *     captcha: '12345'
 * }
 * @param parameter
 * @returns {*}
 */
export function login (parameter) {
  return request({
    url: userApi.Login,
    method: 'post',
    data: qs.stringify(parameter)
  })
}

export function refreshToken () {
  const data = {
    client_id: 'pc',
    client_secret: 'secret',
    grant_type: 'refresh_token',
    refresh_token: storage.get(REFRESH_TOKEN)
  }
  return request({
    url: userApi.Login,
    method: 'post',
    data: qs.stringify(data),
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    }
  }).then(res => {
    if (res.code === 0) {
      setLoginResult(res.data)
      return Promise.resolve()
    } else {
      throw new Error('登录失效')
    }
  })
    .catch(() => {
      return Promise.reject(new Error('登录失效'))
    })
}

export function setLoginResult (data) {
  // 提取各项登录数据
  // eslint-disable-next-line camelcase
  const { access_token, token_type, refresh_token, expires_in } = data
  // 将系列数据长期记录到页面中
  storage.set(ACCESS_TOKEN, access_token, 7 * 24 * 60 * 60 * 1000)
  storage.set(TOKEN_TYPE, token_type, 7 * 24 * 60 * 60 * 1000)
  // eslint-disable-next-line camelcase
  storage.set(TOKEN_EXPIRES_TIME, (new Date()).getTime() + (expires_in * 1000))
  storage.set(REFRESH_TOKEN, refresh_token, 90 * 24 * 60 * 60 * 1000)
  store.commit('SET_TOKEN', access_token)
}

export function clearLoginResult () {
  store.commit('SET_TOKEN', '')
  store.commit('SET_ROLES', [])
  storage.remove(ACCESS_TOKEN)
  storage.remove(TOKEN_TYPE)
  storage.remove(TOKEN_EXPIRES_TIME)
  storage.remove(REFRESH_TOKEN)
}

export function getInfo () {
  return request({
    url: userApi.UserInfo,
    method: 'get',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function logout (token) {
  return request({
    url: userApi.Logout,
    method: 'delete',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
