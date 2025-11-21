import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/home',
    component: () => import('../views/home/HomeLayout.vue'),
    children: [
      {
        path: '',
        name: 'HomeIndex',
        component: () => import('../views/home/HomeIndex.vue'),
      },
      {
        path: 'product/:category',
        name: 'HomeProductList',
        component: () => import('../views/home/HomeProductList.vue'),
      },
      {
        path: 'product/detail/:id',
        name: 'HomeProductDetail',
        component: () => import('../views/home/HomeProductDetail.vue'),
      },
      {
        path: 'cart',
        name: 'HomeCart',
        component: () => import('../views/home/HomeCart.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'order/confirm',
        name: 'HomeOrderConfirm',
        component: () => import('../views/home/HomeOrderConfirm.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'user/order',
        name: 'HomeOrderList',
        component: () => import('../views/home/HomeOrderList.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'user/info',
        name: 'HomeUserInfo',
        component: () => import('../views/home/HomeUserInfo.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'user/comment',
        name: 'HomeUserComment',
        component: () => import('../views/home/HomeUserComment.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'collect',
        name: 'HomeCollect',
        component: () => import('../views/home/HomeCollect.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'user/password',
        name: 'HomeUserPassword',
        component: () => import('../views/home/HomeUserPassword.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'hot-rank',
        name: 'HomeHotRank',
        component: () => import('../views/home/HomeHotRank.vue'),
      },
      {
        path: 'address',
        name: 'HomeAddress',
        component: () => import('../views/home/HomeAddress.vue'),
        meta: { requiresAuth: true },
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/home/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/home/Register.vue'),
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/admin/AdminLogin.vue'),
  },
  {
    path: '/admin',
    name: 'AdminRoot',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('../views/admin/AdminDashboard.vue'),
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('../views/admin/AdminAnnouncements.vue'),
      },
      {
        path: 'announcement/index',
        redirect: { name: 'AdminAnnouncements' },
      },
      {
        path: 'menus',
        name: 'AdminMenus',
        component: () => import('../views/admin/AdminMenus.vue'),
      },
      {
        path: 'menu/buttons',
        name: 'AdminMenuButtons',
        component: () => import('../views/admin/AdminMenuButtons.vue'),
      },
      {
        path: 'menu/index',
        redirect: { name: 'AdminMenus' },
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/AdminUsers.vue'),
      },
      {
        path: 'user/index',
        redirect: { name: 'AdminUsers' },
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('../views/admin/AdminProducts.vue'),
      },
      {
        path: 'product/index',
        redirect: { name: 'AdminProducts' },
      },
      {
        path: 'product/categories',
        name: 'AdminProductCategories',
        component: () => import('../views/admin/AdminProductCategories.vue'),
      },
      {
        path: 'product_category/index',
        redirect: { name: 'AdminProductCategories' },
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('../views/admin/AdminOrders.vue'),
      },
      {
        path: 'order/index',
        redirect: { name: 'AdminOrders' },
      },
      {
        path: 'admins',
        name: 'AdminAdmins',
        component: () => import('../views/admin/AdminAdmins.vue'),
      },
      {
        path: 'admin/index',
        redirect: { name: 'AdminAdmins' },
      },
      {
        path: 'roles',
        name: 'AdminRoles',
        component: () => import('../views/admin/AdminRoles.vue'),
      },
      {
        path: 'role/index',
        redirect: { name: 'AdminRoles' },
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('../views/admin/AdminComments.vue'),
      },
      {
        path: 'user/comment',
        redirect: { name: 'AdminComments' },
      },
      {
        path: 'mails',
        name: 'AdminMails',
        component: () => import('../views/admin/AdminMails.vue'),
      },
      {
        path: 'mail/index',
        redirect: { name: 'AdminMails' },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const requiresUserAuth = to.matched.some((record) => record.meta?.requiresAuth);
  if (requiresUserAuth) {
    const token = localStorage.getItem('token');
    if (!token) {
      window.alert('请先登录');
      next({
        path: '/login',
        query: { redirect: to.fullPath },
      });
      return;
    }
  }

  const requiresAdminAuth = to.matched.some((record) => record.meta?.requiresAdmin);
  if (requiresAdminAuth) {
    const adminLoggedIn = localStorage.getItem('adminLoggedIn');
    if (!adminLoggedIn) {
      window.alert('请先登录后台');
      next({
        path: '/admin/login',
        query: { redirect: to.fullPath },
      });
      return;
    }
  }

  next();
});

export default router;

