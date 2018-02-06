<template lang="pug">
  v-layout(fill-width)
    v-flex(xs12)
      v-data-table(v-bind:headers="headers" :items="users" hide-actions)
        template(slot="items" slot-scope="props")
          tr(@click="goToUser(props.item.userId)")
            td {{props.item.username}}
            td()
              v-icon.green--text.text--darken-2(v-if="props.item.enabled") done
              v-icon.red--text.text--darken-2(v-else) clear
</template>

<script>
import axios from 'axios'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'
import { mapMutations } from 'vuex'
import { ADMIN_USER } from '@/router/constants'

export default {
  name: 'AdminUsers',
  data () {
    return {
      headers: [
        {
          text: 'Username',
          align: 'left',
          value: 'name'
        }, {
          text: 'Enabled',
          align: 'left',
          sortable: false
        }
      ],
      users: []
    }
  },
  beforeMount () {
    this.getUsers()
  },
  methods: {
    ...mapMutations({
      showSnackbar: SHOW_SNACKBAR
    }),
    getUsers () {
      axios.get('admin/user').then(response => {
        this.users = response.data
      }).catch(() => {
        this.showSnackbar({type: 'error', text: 'Can\'t get users'})
      })
    },
    goToUser (id) {
      this.$router.push({ name: ADMIN_USER, params: { id } })
    }
  }
}
</script>