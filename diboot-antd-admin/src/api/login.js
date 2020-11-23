import { axios } from '@/utils/request'
import qs from 'qs'
import Vue from 'vue'
import { ACCESS_TOKEN, REFRESH_TOKEN, TOKEN_EXPIRES_TIME, TOKEN_TYPE } from '@/store/mutation-types'
import store from '@/store'

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
  return axios({
    url: '/auth-server/oauth/token',
    method: 'post',
    data: qs.stringify(parameter)
  })
}

export function refreshToken () {
  const data = {
    client_id: 'pc',
    client_secret: 'secret',
    grant_type: 'refresh_token',
    refresh_token: Vue.ls.get(REFRESH_TOKEN)
  }
  return axios({
    url: '/auth-server/oauth/token',
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
  Vue.ls.set(ACCESS_TOKEN, access_token, 7 * 24 * 60 * 60 * 1000)
  Vue.ls.set(TOKEN_TYPE, token_type, 7 * 24 * 60 * 60 * 1000)
  // eslint-disable-next-line camelcase
  Vue.ls.set(TOKEN_EXPIRES_TIME, (new Date()).getTime() + (expires_in * 1000))
  Vue.ls.set(REFRESH_TOKEN, refresh_token, 90 * 24 * 60 * 60 * 1000)
  store.commit('SET_TOKEN', access_token)
}

export function clearLoginResult () {
  store.commit('SET_TOKEN', '')
  store.commit('SET_ROLES', [])
  Vue.ls.remove(ACCESS_TOKEN)
  Vue.ls.remove(TOKEN_TYPE)
  Vue.ls.remove(TOKEN_EXPIRES_TIME)
  Vue.ls.remove(REFRESH_TOKEN)
}

export function getInfo () {
  return axios({
    url: '/auth-server/oauth/userInfo',
    method: 'get'
  })
}

export function logout () {
  return axios({
    url: '/logout',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
