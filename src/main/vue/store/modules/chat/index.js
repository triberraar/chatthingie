import axios from 'axios'
import { GET_USER,
  USER,
  IS_ADMIN,
  CONNECTED,
  DISCONNECTED,
  CONNECTING,
  CONNECTION_STATUS
} from './constants'
import { BOOTSTRAP } from '@/store/modules/bootstrap/constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'

const initial = {
  user: null,
  connectionStatus: 'disconnected'
}

const mutations = {
  [USER]: (state, user) => {
    state.user = user
  },
  [CONNECTED]: (state) => {
    state.connectionStatus = 'connected'
  },
  [CONNECTING]: (state) => {
    state.connectionStatus = 'connecting'
  },
  [DISCONNECTED]: (state) => {
    state.connectionStatus = 'disconnected'
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
  [IS_ADMIN]: (state) => {
    if (!state.user) { return false }
    return !!state.user.roles.find(ele => ele === 'ROLE_ADMIN')
  },
  [CONNECTION_STATUS]: (state) => {
    return state.connectionStatus
  }
}

export default {
  state: initial,
  mutations,
  getters,
  actions,
  strict: true
}
