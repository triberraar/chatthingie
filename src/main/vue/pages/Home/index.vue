<template lang="pug">
  span
    v-navigation-drawer(fixed 
      :clipped="$vuetify.breakpoint.width > 1264"
      v-model="drawer"
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
    v-toolbar(dense fixed clipped-left app)
      v-toolbar-title
        v-toolbar-side-icon(@click.stop="drawer = !drawer")
        | Chattingie
      v-spacer
      v-menu(offset-y)
        v-btn(slot="activator") {{username}}
        v-list
          v-list-tile(v-if="isAdmin" @click="adminClicked")
            v-icon accessibility
            v-list-tile-title.pl-1 Admin
          v-list-tile(@click="logoutClicked")
            v-icon lock_open
            v-list-tile-title.pl-1 Logout
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { GET_USER,
  USER,
  IS_ADMIN,
  GET_ROOMS,
  ROOMS,
  JOIN_ROOM,
  ROOM
} from '@/store/modules/chat/constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'
import { NAMESPACE as SECURITY_NAMESPACE, LOGOUT } from '@/store/modules/security/constants'
import { LOGIN } from '@/router/constants'
import axios from 'axios'

export default {
  name: 'home',
  created () {
  },
  data () {
    return {
      drawer: null
    }
  },
  computed: {
    ...mapGetters({
      user: USER,
      isAdmin: IS_ADMIN,
      rooms: ROOMS,
      currentRoom: ROOM
    }),
    username () {
      if (this.user) { return this.user.username }
      return ''
    }
  },
  beforeMount () {
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
      showSnackbar: SHOW_SNACKBAR
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
      this.showSnackbar({type: 'warning', text: 'No admin stuff yet'})
    },
    joinRoomClicked (id) {
      this.joinRoom(id)
    }
  }
}
</script>
