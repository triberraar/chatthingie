import axios from 'axios'
import { GET_USER,
  USER,
  CONNECTED,
  DISCONNECTED
} from './constants'
import { BOOTSTRAP } from '@/store/modules/bootstrap/constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'

const initial = {
  user: null,
  connected: false
}

const mutations = {
  [USER]: (state, user) => {
    state.user = user
  },
  [CONNECTED]: (state) => {
    state.connected = true
  },
  [DISCONNECTED]: (state) => {
    state.connected = false
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
  [USER]: (state) => {
    return state.user
  },
  [CONNECTED]: (state) => {
    return state.connected
  }
}

export default {
  state: initial,
  mutations,
  getters,
  actions,
  strict: true
}
