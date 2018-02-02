import axios from 'axios'
import { GET_USER, USER } from './constants'
import { BOOTSTRAP } from '@/store/modules/bootstrap/constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'

const initial = {
  user: null
}

const mutations = {
  [USER]: (state, user) => {
    state.user = user
  }
}

const actions = {
  [GET_USER]: ({commit}) => {
    axios.get('users/me').then((response) => {
      commit(USER, response.data)
    }).catch(() => {
      commit(SHOW_SNACKBAR, {type: 'error', text: 'Could not retrieve user'})
    })
  },
  [BOOTSTRAP]: () => {

  }
}

const getters = {
}

export default {
  state: initial,
  mutations,
  getters,
  actions,
  strict: true
}
