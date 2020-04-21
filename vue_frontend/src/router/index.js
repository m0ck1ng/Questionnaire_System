import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import Register from '@/components/Register'
import Form from '@/components/Form'
import Index from '@/components/Index'
import Questionnaires from '@/components/Questionnaires'
import test from '@/components/test'
Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/index',
      name: 'Index',
      redirect: "/questionnaires",
      component: Index,
      meta: {
        requireAuth: true
      },
      children: [{
        path: '/form',
        name: "Form",
        component: Form,
        meta: {
          requireAuth: true
        },
      }, {
        path: '/questionnaires',
        name: "Questionnaires",
        component: Questionnaires,
        meta: {
          requireAuth: true
        },
      }]
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },
    {
      path: '/test',
      name: 'test',
      component: test
    }
  ]
})

router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next();
  } else {
    let token = localStorage.getItem('Authorization');
    if (token === 'null' || token === '') {
      next('/login');
    } else {
      next();
    }
  }
});

export default router
