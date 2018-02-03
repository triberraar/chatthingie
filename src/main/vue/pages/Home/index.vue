<template lang="pug">
  div home
    v-btn(label="test" @click="test")
</template>

<script>
import { mapActions } from 'vuex'
import { GET_USER } from '@/store/modules/chat/constants'
import { send } from '@/ws'

export default {
  name: 'home',
  created () {
  },
  data () {
    return {
    }
  },
  beforeMount () {
    console.log('before mount')
    this.getUser()
  },
  methods: {
    ...mapActions({
      getUser: GET_USER
    }),
    test: () => {
      console.log('test')
      send('test')
    }
  }
}
</script>
