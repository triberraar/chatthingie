<template lang="pug">
  v-layout(fill-width)
    v-flex(xs12)
      v-data-table(v-bind:headers="headers" :items="rooms" hide-actions)
        template(slot="items" slot-scope="props")
          td {{props.item.name}}
          td()
            v-icon.green--text.text--darken-2(v-if="props.item.withHistory") done
            v-icon.red--text.text--darken-2(v-else) clear
          td.text-xs-right
            v-icon(@click="deleteClicked(props.item.id)") delete
      div
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
                      v-text-field(label="Name" required v-model="newRoom.name" :rules="[v => !!v || 'Name is required']")
                    v-flex(xs12)
                      v-checkbox(label="With history" v-model="newRoom.withHistory")
            v-card-actions
              v-spacer
              v-btn.darken-1(@click.native="dialog = false") Close
              v-btn.blue.darken-1(@click="addRoomClicked") Save
   
</template>

<script>
import axios from 'axios'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'
import { mapMutations } from 'vuex'

export default {
  name: 'AdminRooms',
  data () {
    return {
      headers: [
        {
          text: 'Name',
          align: 'left',
          value: 'name'
        }, {
          text: 'With history',
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
        name: null,
        withHistory: false
      },
      rooms: []
    }
  },
  beforeMount () {
    this.getRooms()
  },
  methods: {
    ...mapMutations({
      showSnackbar: SHOW_SNACKBAR
    }),
    getRooms () {
      axios.get('admin/room').then(response => {
        this.rooms = response.data
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'Can\'t get rooms'})
      })
    },
    deleteClicked (id) {
      axios.delete(`admin/room/${id}`).then(response => {
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'Delete room failed'})
      }).finally(() => {
        this.getRooms()
      })
    },
    addRoomClicked () {
      if (this.$refs.form.validate()) {
        axios.post('admin/room', {name: this.newRoom.name, withHistory: this.newRoom.withHistory})
        .then(() => {})
        .catch(() => {
          this.showSnackbar({type: 'error', text: 'Room creation failed'})
        })
        .finally(() => {
          this.newRoom.name = undefined
          this.newRoom.withHistory = false
          this.getRooms()
          this.dialog = false
        })
      }
    }
  }
}
</script>
