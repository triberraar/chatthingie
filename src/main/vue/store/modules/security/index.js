import { LOGIN, LOGOUT } from './constants'

const initial = {
  loggedIn: false
}

const mutations = {
  [LOGIN]: (state) => {
    state.loggedIn = true
  },
  [LOGOUT]: (state) => {
    state.loggedIn = false
  }
}

const getters = {
  loggedIn: state => {
    return state.loggedIn
  }
}

export default {
  namespaced: true,
  state: initial,
  mutations,
  getters,
  strict: true
}
