import Vue from 'vue'
import { login, getInfo, logout, setLoginResult, clearLoginResult } from '@/api/login'
import { welcome } from '@/utils/util'
import { permissionListToPermissions } from '@/utils/permissions'
import defaultAvatar from '@/assets/logo.png'
import { ACCESS_TOKEN } from '@/store/mutation-types'

const user = {
  state: {
    token: '',
    name: '',
    welcome: '',
    avatar: '',
    roles: [],
    info: {}
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, { name, welcome }) => {
      state.name = name
      state.welcome = welcome
    },
    SET_AVATAR: (state, avatar) => {
      if (avatar) {
        state.avatar = avatar
      } else {
        state.avatar = defaultAvatar
      }
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_INFO: (state, info) => {
      state.info = info
    }
  },

  actions: {
    // 登录
    Login ({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        login(userInfo).then(response => {
          const data = response.data
          if (data !== undefined) {
            setLoginResult(data)
          }
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo ({ commit }) {
      return new Promise((resolve, reject) => {
        getInfo().then(response => {
          const result = response.data
          if (result.role) {
            const role = result.role
            // 更改permission的默认的列表字段
            if (result.role.permissionList.length > 0) {
              role.permissions = permissionListToPermissions(result.role.permissionList)
              role.permissionList = role.permissions.map(permission => { return permission.permissionId })
            } else {
              role.permissions = []
              role.permissionList = []
            }
            commit('SET_ROLES', result.role)
            commit('SET_INFO', result)
          } else {
            reject(new Error('请配置该账号的角色与权限！'))
          }

          commit('SET_NAME', { name: result.name, welcome: welcome() })
          commit('SET_AVATAR', result.avatar)

          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 登出
    Logout ({ commit, state }) {
      return new Promise((resolve) => {
        clearLoginResult()
        logout(state.token).then(() => {
          resolve()
        }).catch(() => {
          resolve()
        })
      })
    }

  }
}

export default user
