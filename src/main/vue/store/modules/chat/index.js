import axios from 'axios'
import { GET_USER,
  USER,
  IS_ADMIN,
  CONNECTED,
  DISCONNECTED,
  CONNECTING,
  CONNECTION_STATUS,
  GET_ROOMS,
  ROOMS,
  JOIN_ROOM,
  ROOM,
  UPDATE_ROOM,
  CAN_CHAT_IN_ROOM,
  RESET,
  CHAT_MESSAGE,
  MESSAGES
} from './constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'

const initial = {
  user: null,
  connectionStatus: 'disconnected',
  rooms: [],
  room: null,
  messages: []
}

const mutations = {
  [RESET]: state => {
    state.user = null
    state.room = null
    state.rooms = []
    state.messages = []
  },
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
  },
  [ROOM]: (state, room) => {
    state.room = room
    state.messages = []
  },
  [UPDATE_ROOM]: (state, room) => {
    state.rooms = state.rooms.map(it => it.id === room.id ? {...it, ...room} : it)
    if (state.room && state.room.id === room.id) {
      state.room = room
    }
  },
  [CHAT_MESSAGE]: (state, message) => {
    if (state.room.id === message.roomId) {
      state.messages.push(message)
    }
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
  },
  [JOIN_ROOM]: ({commit, state}, roomId) => {
    const previousRoomId = state.room && state.room.id
    axios.put('rooms/join', {previousRoom: previousRoomId, currentRoom: roomId}).then((response) => {
      commit(ROOM, response.data)
      commit(UPDATE_ROOM, response.data)
      // TODO also do something with the messages (like commit a new mutation and set the state.messages to the messages)
    }).catch(() => {
      commit(SHOW_SNACKBAR, {type: 'error', text: 'Can\'t join room'})
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
  },
  [ROOM]: state => {
    return state.room
  },
  [CAN_CHAT_IN_ROOM]: state => {
    return state.room && state.room.write
  },
  [MESSAGES]: state => {
    return state.messages
  }
}

export default {
  state: initial,
  mutations,
  getters,
  actions,
  strict: true
}
