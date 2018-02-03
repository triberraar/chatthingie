import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/pages/Login'
import Home from '@/pages/Home'
import store from '@/store'
import { LOGIN, HOME } from './constants'
import { NAMESPACE as SECURITY_NAMESPACE } from '@/store/modules/security/constants'
import { connect } from '@/ws'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/login',
      name: LOGIN,
      component: Login
    },
    {
      path: '/home',
      name: HOME,
      component: Home
    },
    {
      path: '*',
      redirect: {
        name: HOME
      }
    }
  ]
})

// use by putting requiresAuth: true if you want the user to be logged in for this route
// use by putting requiredRole: <role> if you want the user to have a specific role for this route
router.beforeEach((to, from, next) => {
  console.log('router')
  if (to.name === LOGIN) {
    if (store.getters[SECURITY_NAMESPACE + '/' + 'loggedIn']) {
      connect(true)
      return next({ name: HOME })
    } else {
      return next()
    }
  }
  if (to.meta.requiresAuth && !store.getters[SECURITY_NAMESPACE + '/' + 'loggedIn']) {
    return next({ name: LOGIN })
  }
  if (to.meta.requiredRole) {
    const hasRole = store.getters[SECURITY_NAMESPACE + '/' + 'roles'].find(e => {
      return e === to.meta.requiredRole
    })
    if (!hasRole) {
      return next({ name: LOGIN })
    }
  }
  connect(true)
  return next()
})

export default router
