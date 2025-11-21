import request from './request';

const toFormPayload = (payload = {}) => {
  const form = new URLSearchParams();
  Object.entries(payload).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      form.append(key, value);
    }
  });
  return form;
};

export const homeLogin = (payload) =>
  request.post('/api/home/user/login', toFormPayload(payload));

export const homeRegister = (payload) =>
  request.post('/api/home/user/register', toFormPayload(payload));

export const fetchCart = () => request.get('/api/home/cart');

export const addToCart = (payload) => request.post('/api/home/cart', payload);

export const updateCart = (productId, method) =>
  request.put(`/api/home/cart/${productId}`, null, { params: { method } });

export const deleteCartItem = (productId) =>
  request.delete(`/api/home/cart/${productId}`);

export const fetchUserOrders = (params) =>
  request.get('/api/home/user/orders', { params });

export const fetchUserComments = (params) =>
  request.get('/api/home/user/comments', { params });

export const updateOrderState = (orderId, state) =>
  request.post('/api/home/order/state', toFormPayload({ orderId, state }));

export const generateOrder = (payload) =>
  request.post('/api/home/order/generate', toFormPayload(payload));

export const submitOrder = (payload) =>
  request.post('/api/home/order/submit', toFormPayload(payload));

export const fetchOrderDetail = (orderId) =>
  request.get(`/api/home/order/${orderId}`);

export const fetchHomeProductList = (params) =>
  request.get('/api/home/products', { params });

export const fetchHomeTopProducts = () =>
  request.get('/api/home/products/top');

export const fetchHotRankProducts = (params) =>
  request.get('/api/home/products/hot-rank', { params });

export const fetchHomeProductCategories = () =>
  request.get('/api/home/products/categories');

export const fetchProductDetail = (id, params) =>
  request.get(`/api/home/products/${id}`, { params });

export const fetchPersonalRecommend = (params) =>
  request.get('/api/home/recommend/personal', { params });

export const fetchPopularRecommend = (params) =>
  request.get('/api/home/recommend/popular', { params });

export const addCollect = (productId) =>
  request.post(`/api/home/collect/${productId}`);

export const removeCollect = (collectId) =>
  request.delete(`/api/home/collect/${collectId}`);

export const submitComment = (payload) =>
  request.post('/api/home/comment', payload);

export const fetchAddresses = () =>
  request.get('/api/home/address');

export const addAddress = (payload) =>
  request.post('/api/home/address', payload);

export const setDefaultAddress = (id) =>
  request.post(`/api/home/address/${id}/first`);

export const deleteAddress = (id) =>
  request.delete(`/api/home/address/${id}`);

export const updateAddress = (id, payload) =>
  request.put(`/api/home/address/${id}`, payload);

export const updateUserInfo = (payload) =>
  request.post('/api/home/user/update-info', toFormPayload(payload));

export const fetchCollects = (params) =>
  request.get('/api/home/collect', { params });

export const updateUserPassword = (payload) =>
  request.post('/api/home/user/update-password', toFormPayload(payload));

export const uploadPhoto = (file) => {
  const formData = new FormData();
  formData.append('photo', file);
  return request.post('/api/common/upload/photo', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

