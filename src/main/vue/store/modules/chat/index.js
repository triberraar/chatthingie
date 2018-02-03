import axios from 'axios'
import { GET_USER,
  USER,
  IS_ADMIN,
  CONNECTED,
  DISCONNECTED,
  CONNECTING,
  CONNECTION_STATUS,
  GET_ROOMS,
  ROOMS
} from './constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'

const initial = {
  user: null,
  connectionStatus: 'disconnected',
  rooms: []
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
  },
  [ROOMS]: (state, rooms) => {
    state.rooms = rooms
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
  [GET_ROOMS]: ({commit}) => {
    axios.get('rooms/me').then((response) => {
      commit(ROOMS, response.data)
    }).catch(() => {
      commit(SHOW_SNACKBAR, {type: 'error', text: 'Could not retrieve rooms'})
    })
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
  },
  [ROOMS]: (state) => {
    return state.rooms
  }
}

export default {
  state: initial,
  mutations,
  getters,
  actions,
  strict: true
}
