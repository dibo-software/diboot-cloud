import api from './index'
import { axios } from '@/utils/request'
import qs from 'qs'

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

/**
 * get user 2step code open?
 * @param parameter {*}
 */
export function get2step (parameter) {
  return axios({
    url: api.twoStepCode,
    method: 'post',
    data: parameter
  })
}
