import axios from 'axios'
import router from '@/router'
import { LOGIN } from '@/router/constants'

if (__DEV__) {
  console.log('true')
  axios.defaults.baseURL = 'api/'
}

axios.interceptors.response.use(function (response) {
  return response
}, function (error) {
  if (error.response.status === 401 || error.response.status === 403) {
    router.push({ name: LOGIN })
  }
  return Promise.reject(error)
})
