<template lang="pug">
  v-app(light)
    v-snackbar(:timeout="timeout"
    :color="type"
    :top="false"
    :bottom="true"
    v-model="showSnackbar") {{text}}
      v-btn(dark fla @click.native="hide") Close
    router-view
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import { HIDE_SNACKBAR } from '@/store/modules/snackbar/constants'

export default {
  name: 'App',
  data () {
    return {}
  },
  computed: {
    ...mapGetters([
      'timeout',
      'type',
      'text'
    ]),
    showSnackbar: {
      get () {
        return this.$store.state.snackbar.showSnackbar
      },
      set () {
        this.hide()
      }
    }
  },
  methods: {
    ...mapMutations([
      HIDE_SNACKBAR
    ]),
    hide () {
      this[HIDE_SNACKBAR]()
    }
  }
}
</script>
