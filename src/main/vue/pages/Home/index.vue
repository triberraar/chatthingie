<template lang="pug">
  div home
</template>

<script>
import { mapActions } from 'vuex'
import { GET_USER } from '@/store/modules/chat/constants'

export default {
  data () {
    return {}
  },
  beforeMount () {
    console.log('before mount')
    this.getUser()
  },
  methods: {
    ...mapActions({
      getUser: GET_USER
    })
  }
}
</script>
