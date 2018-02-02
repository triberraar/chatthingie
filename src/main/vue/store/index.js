import Vuex from 'vuex'
import Vue from 'vue'

import security from './modules/security'
import snackbar from './modules/snackbar'
import bootstrap from './modules/bootstrap'
import chat from './modules/chat'

Vue.use(Vuex)
export default new Vuex.Store({
  modules: {
    security,
    snackbar,
    bootstrap,
    chat
  },
  strict: true
})
