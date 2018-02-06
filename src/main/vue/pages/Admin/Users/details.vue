<template lang="pug">
  v-container(fluid grid-list-lg)
    v-layout(fill-width)
      v-flex(xs12)
        v-card
          v-card-title(primary-title)
            div.headline {{user && user.username}}
              v-icon.green--text.text--darken-2(v-if="user && user.enabled") done
              v-icon.red--text.text--darken-2(v-else) clear
            div
              v-btn(color="primary" v-if="user && user.enabled" @click="deactivate") Deactivate
              v-btn(color="primary" v-else @click="activate") Activate
          v-divider
          v-card-text
            v-data-table(v-bind:headers="headers" :items="(user && user.rights) || []" hide-actions)
              template(slot="items" slot-scope="props")
                tr
                  td {{props.item.roomName}}
                  td()
                    v-icon.green--text.text--darken-2(v-if="props.item.write") done
                    v-icon.red--text.text--darken-2(v-else) clear
                  td.text-xs-right
                    v-icon(@click="deleteClicked(props.item.roomId)") delete
            v-btn(color="primary" @click="dialog = !dialog") Add
            v-dialog(v-model="dialog" persistent max-width="500px")
              v-card
                v-card-title
                  span.headline Add room
                v-card-text
                  v-container(grid-list-md)
                  v-form(v-model="valid" ref="form" lazy-validation)
                      v-layout(wrap)
                        v-flex(xs12)
                          v-select(v-bind:items="rooms" v-model="newRoom.roomId" label="Room" single-line item-text="name" item-value="id" :rules="[v => !!v || 'Room is required']" required)
                        v-flex(xs12)
                          v-checkbox(label="Write access" v-model="newRoom.write")
                v-card-actions
                  v-spacer
                  v-btn.darken-1(@click.native="dialog = false") Close
                  v-btn.blue.darken-1(@click="addRightClicked") Save
</template>

<script>
import axios from 'axios'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'
import { mapMutations } from 'vuex'

export default {
  name: 'AdminUser',
  props: {
    id: String,
    required: true
  },
  data () {
    return {
      user: null,
      rooms: [],
      headers: [
        {
          text: 'Name',
          align: 'left',
          value: 'name'
        }, {
          text: 'Write access',
          align: 'left',
          sortable: false
        }, {
          text: '',
          sortable: false
        }
      ],
      dialog: false,
      valid: false,
      newRoom: {
        roomId: null,
        write: false
      }
    }
  },
  beforeMount () {
    this.getUser()
    this.getRooms()
  },
  methods: {
    ...mapMutations({
      showSnackbar: SHOW_SNACKBAR
    }),
    getUser () {
      axios.get(`admin/user/${this.id}`).then(response => {
        this.user = response.data
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'Can\'t get user'})
      })
    },
    deleteClicked (roomId) {
      axios.delete(`admin/user/${this.id}/room/${roomId}`).then(response => {
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'Delete room failed'})
      }).finally(() => {
        this.getUser()
      })
    },
    activate () {
      axios.put(`admin/user/${this.id}/activate`).then(response => {
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'User activation failed'})
      }).finally(() => {
        this.getUser()
      })
    },
    deactivate () {
      axios.put(`admin/user/${this.id}/deactivate`).then(response => {
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'User deactivation failed'})
      }).finally(() => {
        this.getUser()
      })
    },
    addRightClicked () {
      if (this.$refs.form.validate()) {
        axios.post(`admin/user/${this.id}/room`, {roomId: this.newRoom.roomId, write: this.newRoom.write}).then(() => {

        }).catch(() => {
          this.showSnackbar({type: 'error', text: 'Add room failed'})
        }).finally(() => {
          this.newRoom.roomId = undefined
          this.newRoom.write = false
          this.dialog = false
          this.getUser()
        })
      }
    },
    getRooms () {
      axios.get('admin/room').then(response => {
        this.rooms = response.data
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'Can\'t get rooms'})
      })
    }
  }
}
</script>