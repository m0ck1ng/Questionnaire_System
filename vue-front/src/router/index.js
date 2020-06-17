import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  {
    path: '/shared/:id/',
    name: 'sharedForm',
    component: () => import('@/views/form/shared/index'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: 'forms',
    children: [
      {
        path: 'forms',
        component: () => import('@/views/created/index'),
        meta: { title: 'Forms', icon: 'example' }
      }
    ]
  },

  {
    path: 'new',
    component: Layout,
    children: [
      {
        path: '/new',
        component: () => import('@/views/form/template/index'),
        meta: { title: 'New', icon: 'form' }
      }
    ]
  },

  {
    path: 'statistics',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/statistics/:id/',
        component: () => import('@/views/statistics/index'),
        meta: { title: 'Statistics', icon: 'eye-open' }
      }
    ]
  },

  {
    path: 'user',
    component: Layout,
    children: [
      {
        path: '/user',
        component: () => import('@/views/user/index'),
        meta: { title: 'User', icon: 'user' }
      }
    ]
  }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
