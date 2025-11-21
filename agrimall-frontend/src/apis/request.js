import axios from 'axios';
import router from '@/router';

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000,
  withCredentials: true,
});

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res && res.code === -6) {
      window.alert(res.msg || '登录已过期，请重新登录');
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('cartTotal');
      localStorage.removeItem('email');
      localStorage.removeItem('phone');
      localStorage.removeItem('headPic');
      localStorage.removeItem('userId');
      localStorage.removeItem('adminLoggedIn');
      localStorage.removeItem('adminName');
      const current = router.currentRoute.value;
      const isAdminRoute = current?.path?.startsWith('/admin');
      const targetPath = isAdminRoute ? '/admin/login' : '/login';
      const redirectPath =
        current && current.fullPath && current.fullPath !== targetPath ? current.fullPath : undefined;
      if (redirectPath) {
        router.push({
          path: targetPath,
          query: { redirect: redirectPath },
        });
      } else {
        router.push({ path: targetPath });
      }
    } else if (res && res.code !== undefined && res.code !== 0 && res.code !== 200) {
      console.warn(res.msg || '请求失败');
    }
    return res;
  },
  (error) => {
    console.error(error);
    return Promise.reject(error);
  },
);

export default request;

