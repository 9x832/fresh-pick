import request from './request';

export const adminLogin = (payload) =>
  request.post('/api/admin/login', payload);

export const adminLogout = () =>
  request.post('/api/admin/logout');

export const fetchAdminProfile = () =>
  request.get('/api/admin/profile');

export const fetchAdminMenus = () =>
  request.get('/api/admin/menus/current');

export const fetchAdminAccounts = (params) =>
  request.get('/api/admin/admins', { params });

export const createAdminAccount = (data) =>
  request.post('/api/admin/admins', data);

export const updateAdminAccount = (id, data) =>
  request.put(`/api/admin/admins/${id}`, data);

export const deleteAdminAccount = (id) =>
  request.delete(`/api/admin/admins/${id}`);

export const toggleAdminAccountState = (id) =>
  request.post(`/api/admin/admins/${id}/state`);

export const fetchAdminDashboard = () =>
  request.get('/api/admin/dashboard');

export const fetchAnnouncements = (params) =>
  request.get('/api/admin/announcements', { params });

export const createAnnouncement = (data) =>
  request.post('/api/admin/announcements', data);

export const deleteAnnouncement = (id) =>
  request.delete(`/api/admin/announcements/${id}`);

export const fetchMenus = () =>
  request.get('/api/admin/menus');

export const fetchMenuLevel = (id) =>
  request.get(`/api/admin/menus/${id}/level`);

export const createMenu = (data) =>
  request.post('/api/admin/menus', data);

export const updateMenu = (id, data) =>
  request.put(`/api/admin/menus/${id}`, data);

export const deleteMenu = (id) =>
  request.delete(`/api/admin/menus/${id}`);

export const toggleMenuState = (id) =>
  request.post(`/api/admin/menus/${id}/state`);

export const fetchUsers = (params) =>
  request.get('/api/admin/users', { params });

export const deleteUser = (userId) =>
  request.delete(`/api/admin/users/${userId}`);

export const updateUserPassword = (userId, password) =>
  request.post(`/api/admin/users/${userId}/password`, null, {
    params: { password },
  });

export const fetchProducts = (params) =>
  request.get('/api/admin/products', { params });

export const fetchProductDetail = (id) =>
  request.get(`/api/admin/products/${id}`);

export const createProduct = (data) =>
  request.post('/api/admin/products', data);

export const updateProduct = (id, data) =>
  request.put(`/api/admin/products/${id}`, data);

export const deleteProduct = (id) =>
  request.delete(`/api/admin/products/${id}`);

export const fetchProductCategories = () =>
  request.get('/api/admin/products/categories');

export const fetchProductCategoryList = (params) =>
  request.get('/api/admin/product-categories', { params });

export const createProductCategory = (data) =>
  request.post('/api/admin/product-categories', data);

export const updateProductCategory = (id, data) =>
  request.put(`/api/admin/product-categories/${id}`, data);

export const deleteProductCategory = (id) =>
  request.delete(`/api/admin/product-categories/${id}`);

export const fetchOrders = (params) =>
  request.get('/api/admin/orders', { params });

export const fetchRoles = (params) =>
  request.get('/api/admin/roles', { params });

export const createRole = (data) =>
  request.post('/api/admin/roles', data);

export const updateRole = (id, data) =>
  request.put(`/api/admin/roles/${id}`, data);

export const deleteRole = (id) =>
  request.delete(`/api/admin/roles/${id}`);

export const fetchRoleAuthority = (roleId) =>
  request.get(`/api/admin/roles/${roleId}/authority`);

export const saveRoleAuthority = (roleId, menuIds) =>
  request.post(`/api/admin/roles/${roleId}/authority`, menuIds);

export const fetchOrderDetail = (orderId) =>
  request.get(`/api/admin/orders/${orderId}`);

export const updateOrderState = (orderId, state) =>
  request.post(`/api/admin/orders/${orderId}/state`, null, { params: { state } });

export const deleteOrder = (orderId) =>
  request.delete(`/api/admin/orders/${orderId}`);

export const fetchComments = (params) =>
  request.get('/api/admin/comments', { params });

export const deleteComment = (commentId) =>
  request.delete(`/api/admin/comments/${commentId}`);

export const fetchReceiveMails = (params) =>
  request.get('/api/admin/mails/receive', { params });

export const fetchSentMails = (params) =>
  request.get('/api/admin/mails/sent', { params });

export const fetchMailDetail = (mailId) =>
  request.get(`/api/admin/mails/${mailId}`);

export const sendMail = (payload) =>
  request.post('/api/admin/mails', payload);

export const deleteMail = (mailId) =>
  request.delete(`/api/admin/mails/${mailId}`);

export const deleteAttachment = (attachmentId) =>
  request.delete(`/api/admin/mails/attachments/${attachmentId}`);

export const uploadAdminPhoto = (file) => {
  const formData = new FormData();
  formData.append('photo', file);
  return request.post('/api/common/upload/photo', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

export const uploadAttachment = (file) => {
  const formData = new FormData();
  formData.append('attachment', file);
  return request.post('/api/common/upload/attachment', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

