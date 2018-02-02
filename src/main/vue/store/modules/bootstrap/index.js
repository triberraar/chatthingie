import { BOOTSTRAP, CLEAR } from './constants'

const initial = {
  bootstrapped: false
}

const mutations = {
  [BOOTSTRAP]: state => {
    state.bootstrapped = true
  },
  [CLEAR]: state => {
    state.bootstrapped = false
  }
}

const actions = {
  [BOOTSTRAP]: ({ dispatch, commit, state, rootState, getters, rootGetters }) => {
    if (!state.bootstrapped) {
      dispatch(BOOTSTRAP, null, {root: true})
      commit(BOOTSTRAP)
    }
  }
}

const getters = {
  bootstrapped: state => {
    return state.bootstrapped
  }
}

export default {
  namespaced: true,
  initial,
  mutations,
  actions,
  getters,
  strict: true
}
