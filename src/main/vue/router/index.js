import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/pages/Login'
import Home from '@/pages/Home'
import Admin from '@/pages/Admin'
import store from '@/store'
import { LOGIN, HOME, ADMIN } from './constants'
import { NAMESPACE as SECURITY_NAMESPACE } from '@/store/modules/security/constants'
import { IS_ADMIN } from '@/store/modules/chat/constants'
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
      path: '/admin',
      name: ADMIN,
      component: Admin,
      meta: {
        requiresAdmin: true
      }
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
  if (to.meta.requiresAdmin) {
    const isAdmin = store.getters[IS_ADMIN]
    if (!isAdmin) {
      return next({ name: LOGIN })
    }
  }
  connect(true)
  return next()
})

export default router
