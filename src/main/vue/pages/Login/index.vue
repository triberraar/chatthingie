<template lang="pug">
  main
    v-content
      v-container(fluid)
        v-layout(justify-center align-center)
          v-flex(xs3)
            v-dialog(:value='true', persistent='' width='290px')
              v-card(hover='', style='background:white')
                v-toolbar
                  v-toolbar-title Login
                v-card-title
                  v-form(v-model="valid" ref="form" lazy-validation)
                    v-text-field(label = "Username" v-model='username' required :rules="[v => !!v || 'Username is required']")
                    v-text-field(type="password"  label = "Password"  v-model="password" required :rules="[v => !!v || 'Password is required']")
                    v-btn(@click.prevent="submit" type="submit" color="primary" :disabled="!valid") Login
</template>

<script>
import { mapMutations } from 'vuex'
import { NAMESPACE as SECURITY_NAMESPACE, LOGIN } from '@/store/modules/security/constants'
import { SHOW_SNACKBAR } from '@/store/modules/snackbar/constants'
import { HOME } from '@/router/constants'
import axios from 'axios'

export default {
  name: 'Login',
  data () {
    return {
      valid: true,
      username: '',
      password: ''
    }
  },
  methods: {
    ...mapMutations(SECURITY_NAMESPACE, {
      login: LOGIN
    }),
    ...mapMutations({
      showSnackbar: SHOW_SNACKBAR
    }),
    submit () {
      if (this.$refs.form.validate()) {
        axios.post('login', { username: this.username, password: this.password }).then(response => {
          this.login()
          this.$router.push({ name: HOME })
        }).catch(() => {
          this.showSnackbar({type: 'error', text: 'Login failed'})
        })
      }
    }
  },
  computed: {
  }
}
</script>

