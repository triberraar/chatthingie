<template lang="pug">
  span
    v-navigation-drawer(fixed 
      :clipped="$vuetify.breakpoint.width > 1264"
      v-model="roomDrawer"
      app
      dark)
      v-list
        v-list-tile(@click="getRooms")
          v-list-tile-action
            v-icon refresh
          v-list-tile-title Refresh
      v-divider
      v-list(dense)
        v-list-tile(@click="joinRoomClicked(room.id)" v-for="room in rooms" :key="room.id")
          v-list-tile-action(v-if="currentRoom && room.id === currentRoom.id")
            v-icon chat
          v-list-tile-content
            v-list-tile-title {{room.name}}
          v-list-tile-action {{room.users.length}}
    v-navigation-drawer(fixed
      :clipped="$vuetify.breakpoint.width > 1264"
      v-model="peopleDrawer"
      right
      app)
      v-list(dense v-if="currentRoom")
        v-list-tile(v-for="user in currentRoom.users" :key="user.id")
          v-list-tile-content
            v-list-tile-title {{user.username}}
    v-toolbar(dense fixed clipped-left clipped-right app)
      v-toolbar-title
        v-toolbar-side-icon(@click.stop="roomDrawer = !roomDrawer")
        | Chattingie
      v-spacer
      v-icon(:color="connectionStatusColor" v-bind:class="connectionStatusStyle" @click="connectClicked") flash_on
      v-menu(offset-y)
        v-btn(slot="activator") {{username}}
        v-list
          v-list-tile(v-if="isAdmin" @click="adminClicked")
            v-icon accessibility
            v-list-tile-title.pl-1 Admin
          v-list-tile(@click="logoutClicked")
            v-icon lock_open
            v-list-tile-title.pl-1 Logout
    v-content()
      v-card(v-if="currentRoom")
        v-toolbar
          v-toolbar-title {{currentRoom.name}}
          v-spacer
          v-btn(icon @click.stop="peopleDrawer = !peopleDrawer")
            v-badge(left)
              span(slot="badge") {{currentRoom.users.length}}
              v-icon perm_identity
          
        v-card-text(style="height: 70vh")
          div(class="messages" style="height:65vh; overflow-y: scroll" id="messageBox")
            div(class="messages"  v-for="(message, index) in messages" :key="index")
              div
                span.subheading {{message.username}} 
                span.grey--text.text--darken-2 {{formatTimeStamp(message.timestamp)}}:
              span {{message.message}}
        v-divider
        v-card-actions
          v-layout
            v-flex(xs12)
              v-form(@submit.prevent="sendClicked")
                v-text-field(placeholder="Message..." 
                  single-line append-icon="send"
                  :append-icon-cb="sendClicked"
                  :disabled="!canChat"
                  v-model="message")
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { GET_USER,
  USER,
  IS_ADMIN,
  GET_ROOMS,
  ROOMS,
  JOIN_ROOM,
  ROOM,
  CAN_CHAT_IN_ROOM,
  CONNECTION_STATUS,
  RESET,
  MESSAGES
} from '@/store/modules/chat/constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'
import { NAMESPACE as SECURITY_NAMESPACE, LOGOUT } from '@/store/modules/security/constants'
import { LOGIN, ADMIN } from '@/router/constants'
import axios from 'axios'
import { connect, send } from '@/ws'
import moment from 'moment'

export default {
  name: 'home',
  created () {
  },
  data () {
    return {
      roomDrawer: null,
      peopleDrawer: false,
      message: null
    }
  },
  computed: {
    ...mapGetters({
      user: USER,
      isAdmin: IS_ADMIN,
      rooms: ROOMS,
      currentRoom: ROOM,
      connectionStatus: CONNECTION_STATUS,
      canChat: CAN_CHAT_IN_ROOM,
      messages: MESSAGES
    }),
    username () {
      if (this.user) { return this.user.username }
      return ''
    },
    connectionStatusStyle: function () {
      if (this.connectionStatus === 'connected') {
        return { animated: true, flash: true, infinite: false }
      } else if (this.connectionStatus === 'connecting') {
        return { animated: true, flash: true, infinite: true }
      } else if (this.connectionStatus === 'disconnected') {
        return { animated: true, flash: true, infinite: false }
      }
    },
    connectionStatusColor: function () {
      if (this.connectionStatus === 'connected') {
        return 'green'
      } else if (this.connectionStatus === 'connecting') {
        return 'orange'
      } else if (this.connectionStatus === 'disconnected') {
        return 'red'
      }
    }
  },
  beforeMount () {
    this.reset()
    this.getUser()
    this.getRooms()
  },
  methods: {
    ...mapActions({
      getUser: GET_USER,
      getRooms: GET_ROOMS,
      showSnackbar: SHOW_SNACKBAR,
      joinRoom: JOIN_ROOM
    }),
    ...mapMutations(SECURITY_NAMESPACE, {
      logout: LOGOUT
    }),
    ...mapMutations({
      showSnackbar: SHOW_SNACKBAR,
      reset: RESET
    }),
    logoutClicked () {
      axios.post('logout').then(response => {
        this.logout()
        this.$router.push({ name: LOGIN })
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'Logout failed'})
      })
    },
    adminClicked () {
      this.$router.push({ name: ADMIN })
    },
    joinRoomClicked (id) {
      this.joinRoom(id)
      this.message = null
    },
    connectClicked () {
      if (this.connectionStatus === 'disconnected') {
        connect(true)
      }
    },
    sendClicked () {
      if (this.message != null) {
        const chatMessage = {type: 'chat', message: {message: this.message, userId: this.user.id, roomId: this.currentRoom.id}}
        send(chatMessage)
        this.$nextTick(this.scrollToEnd)
        this.message = null
      }
    },
    scrollToEnd () {
      var container = this.$el.querySelector('#messageBox')
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },
    formatTimeStamp (date) {
      return moment(date).format('DD/MM/YYYY HH:mm:ss')
    }
  },
  watch: {
    messages: function () {
      this.$nextTick(this.scrollToEnd)
    }
  }
}
</script>
