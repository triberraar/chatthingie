<template lang="pug">
  span
    v-toolbar(dense fixed clipped-left clipped-right app)
      v-icon(@click="goHome") home
      v-toolbar-title
        | Chattingie
      v-btn(flat @click="goToRooms") Rooms
      v-btn(flat @click="goToUsers") Users
      v-spacer
      v-icon(:color="connectionStatusColor" v-bind:class="connectionStatusStyle") flash_on
      v-menu(offset-y)
        v-btn(flat slot="activator") {{username}}
        v-list
          v-list-tile(v-if="isAdmin" @click="adminClicked")
            v-icon accessibility
            v-list-tile-title.pl-1 Admin
          v-list-tile(@click="logoutClicked")
            v-icon lock_open
            v-list-tile-title.pl-1 Logout
    v-content
      router-view
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import {
  USER,
  IS_ADMIN,
  CONNECTION_STATUS
} from '@/store/modules/chat/constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'
import { NAMESPACE as SECURITY_NAMESPACE, LOGOUT } from '@/store/modules/security/constants'
import { HOME, ADMIN_ROOMS, ADMIN_USERS, LOGIN } from '@/router/constants'
import axios from 'axios'

export default {
  name: 'Admin',
  computed: {
    ...mapGetters({
      user: USER,
      isAdmin: IS_ADMIN,
      connectionStatus: CONNECTION_STATUS
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
  methods: {
    ...mapMutations({
      showSnackbar: SHOW_SNACKBAR
    }),
    ...mapMutations(SECURITY_NAMESPACE, {
      logout: LOGOUT
    }),
    goHome () {
      this.$router.push({ name: HOME })
    },
    goToRooms () {
      this.$router.push({ name: ADMIN_ROOMS })
    },
    goToUsers () {
      this.$router.push({ name: ADMIN_USERS })
    },
    adminClicked () {
      this.$router.push({ name: ADMIN_ROOMS })
    },
    logoutClicked () {
      axios.post('logout').then(response => {
        this.logout()
        this.$router.push({ name: LOGIN })
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'Logout failed'})
      })
    }
  }
}
</script>

