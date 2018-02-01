import { SHOW_SNACKBAR, HIDE_SNACKBAR } from './constants'

const initial = {
  showSnackbar: false,
  type: null,
  timeout: 2000,
  text: null
}

const mutations = {
  [SHOW_SNACKBAR]: (state, payload) => {
    state.showSnackbar = true
    state.type = payload.type
    state.text = payload.text
  },
  [HIDE_SNACKBAR]: (state) => {
    state.showSnackbar = false
    state.type = null
    state.text = null
  }
}

const getters = {
  showSnackbar: (state) => {
    return state.showSnackbar
  },
  type: (state) => {
    return state.type
  },
  text: (state) => {
    return state.text
  },
  timeout: (state) => {
    return state.timeout
  }
}

export default {
  state: initial,
  mutations,
  getters,
  strict: true
}
