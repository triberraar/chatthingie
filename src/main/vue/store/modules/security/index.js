import { LOGIN } from './constants'

const initial = {
  loggedIn: false
}

const mutations = {
  [LOGIN]: (state) => {
    state.loggedIn = true
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
