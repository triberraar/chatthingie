import store from '@/store'
import { CONNECTED, DISCONNECTED } from '@/store/modules/chat/constants'

const maxReconnect = 5
const reconnectDelay = 500
var reconnect = 0
var socket = null

export const connect = function () {
  if (reconnect > maxReconnect) {
    store.commit(DISCONNECTED)
    return
  }
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
    store.commit(CONNECTED)
    console.log(`connected ${event}`)
  }

  socket.onmessage = function (event) {
    console.log(`on message ${event.data}`)
    console.log(event.data)
  }

  socket.onclose = function (event) {
    console.log(`on close ${event}`)
    store.commit(DISCONNECTED)
    setTimeout(function () {
      connect()
    }, reconnect * reconnectDelay)
  }

  socket.onerror = function (event) {
    console.log(`on error ${event}`)
  }
}

export const disconnect = function () {
  if (socket) {
    socket.close()
  }
}
