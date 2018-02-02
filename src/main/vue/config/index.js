import axios from 'axios'
import router from '@/router'
import store from '@/store'
import { LOGIN } from '@/router/constants'
import { LOGOUT, NAMESPACE } from '@/store/modules/security/constants'
import { disconnect } from '@/ws'

if (__DEV__) {
  console.log('true')
  axios.defaults.baseURL = 'api/'
}

axios.interceptors.response.use(function (response) {
  return response
}, function (error) {
  if (error.response.status === 401) {
    store.commit(`${NAMESPACE}/${LOGOUT}`)
    disconnect()
    router.push({ name: LOGIN })
  }
  return Promise.reject(error)
})
