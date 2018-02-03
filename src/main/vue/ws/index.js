import store from '@/store'
import { CONNECTED,
  DISCONNECTED,
  CONNECTING
} from '@/store/modules/chat/constants'

const maxReconnect = 5
const reconnectDelay = 500
var reconnect = 0
var socket = null
var connecting = false

export const connect = function () {
  disconnect()
  if (reconnect > maxReconnect) {
    store.commit(DISCONNECTED)
    return
  }
  if (connecting) {
    return
  }
  store.commit(CONNECTING)
  connecting = true
  reconnect = reconnect + 1
  console.log('connecting')
  const loc = window.location
  var uri = ''
  if (loc.protocol === 'https:') {
    uri = 'wss:'
  } else {
    uri = 'ws:'
  }
  uri += '//' + loc.host
  if (__DEV__) {
    uri += '/api'
  }
  uri += '/chat'
  console.log(uri)
  socket = new WebSocket(uri)

  socket.onopen = function (event) {
    reconnect = 0
    connecting = false
    store.commit(CONNECTED)
    console.log(`connected ${event}`)
  }

  socket.onmessage = function (event) {
    console.log(`on message ${event.data}`)
    console.log(event.data)
  }

  socket.onclose = function (event) {
    store.commit(DISCONNECTED)
    console.log(`on close ${event}`)
  }

  socket.onerror = function (event) {
    console.log(`on error ${event}`)
    setTimeout(function () {
      connect()
    }, reconnect * reconnectDelay)
  }
}

export const disconnect = function () {
  if (socket) {
    socket.close()
  }
}

export const send = function (message) {
  socket.send(message)
}
