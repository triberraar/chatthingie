import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/pages/Login'
import Home from '@/pages/Home'
import Admin from '@/pages/Admin'
import AdminUsers from '@/pages/Admin/Users'
import AdminUser from '@/pages/Admin/Users/details'
import AdminRooms from '@/pages/Admin/Rooms'
import store from '@/store'
import { LOGIN, HOME, ADMIN_ROOMS, ADMIN_USERS, ADMIN_USER } from './constants'
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
      component: Admin,
      meta: {
        requiresAdmin: true
      },
      children: [
        {
          path: 'rooms',
          component: AdminRooms,
          name: ADMIN_ROOMS,
          meta: {
            requiresAdmin: true
          }
        },
        {
          path: 'users',
          component: AdminUsers,
          name: ADMIN_USERS,
          meta: {
            requiresAdmin: true
          }
        },
        {
          path: 'users/:id',
          component: AdminUser,
          name: ADMIN_USER,
          props: true,
          meta: {
            requiresAdmin: true
          }
        }
      ]
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
