<template>
  <section class="admin-accounts">
    <header class="page-header">
      <div>
        <h2>管理员管理</h2>
        <p>管理后台账号信息，可新增、编辑、启用/冻结或删除管理员。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchName"
          type="text"
          class="search-input"
          placeholder="按管理员名称搜索"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn ghost" @click="resetSearch">清空</button>
        <button type="button" class="btn primary" @click="openCreate">新增管理员</button>
      </div>
    </header>

    <section class="card">
      <table class="admin-table">
        <thead>
          <tr>
            <th style="width: 80px;">ID</th>
            <th style="width: 220px;">管理员</th>
            <th style="width: 140px;">角色</th>
            <th style="width: 150px;">手机号</th>
            <th style="width: 100px;">状态</th>
            <th style="width: 190px;">创建时间</th>
            <th style="width: 200px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="loading-cell">管理员列表加载中...</td>
          </tr>
          <tr v-else-if="!admins.length">
            <td colspan="7" class="empty-cell">暂无管理员记录</td>
          </tr>
          <tr v-for="admin in admins" :key="admin.id">
            <td class="center">{{ admin.id }}</td>
            <td>
              <div class="admin-info">
                <img :src="resolveAvatar(admin.headPic)" alt="头像" class="avatar" />
                <div>
                  <p class="admin-name">{{ admin.name || '-' }}</p>
                  <p class="admin-meta">{{ formatAddress(admin.address) }}</p>
                </div>
              </div>
            </td>
            <td class="center">{{ roleName(admin.roleId) }}</td>
            <td class="center">{{ formatMobile(admin.mobile) }}</td>
            <td class="center">
              <span class="state-tag" :class="{ open: admin.state === 1 }">
                {{ admin.state === 1 ? '启用' : '冻结' }}
              </span>
            </td>
            <td class="center">{{ formatDate(admin.createTime) }}</td>
            <td class="center">
              <div class="row-actions">
                <button type="button" class="btn ghost" @click="openEdit(admin)">编辑</button>
                <button type="button" class="btn secondary" @click="handleToggleState(admin)">
                  {{ admin.state === 1 ? '冻结' : '启用' }}
                </button>
                <button type="button" class="btn danger ghost" @click="handleDelete(admin)">
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <footer class="pagination">
        <button type="button" :disabled="pageInfo.pageNum <= 1" @click="gotoPage(pageInfo.pageNum - 1)">
          上一页
        </button>
        <span>第 {{ pageInfo.pageNum }} / {{ pageInfo.pages || 1 }} 页</span>
        <button
          type="button"
          :disabled="pageInfo.pageNum >= (pageInfo.pages || 1)"
          @click="gotoPage(pageInfo.pageNum + 1)"
        >
          下一页
        </button>
        <span>共 {{ pageInfo.total || 0 }} 位管理员</span>
      </footer>
    </section>

    <AdminAccountDialog
      :open="dialog.visible"
      :mode="dialog.mode"
      :admin="dialog.target"
      :roles="roles"
      :submitting="dialog.submitting"
      @close="closeDialog"
      @submit="handleSubmit"
    />
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  createAdminAccount,
  deleteAdminAccount,
  fetchAdminAccounts,
  toggleAdminAccountState,
  updateAdminAccount,
} from '@/apis/admin';
import AdminAccountDialog from './components/AdminAccountDialog.vue';

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const route = useRoute();
const router = useRouter();

const admins = ref([]);
const roles = ref([]);
const loading = ref(false);
const searchName = ref('');
const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  pages: 0,
  total: 0,
});

const dialog = reactive({
  visible: false,
  mode: 'create',
  submitting: false,
  target: null,
});

const roleMap = computed(() => {
  const map = new Map();
  roles.value.forEach((role) => {
    map.set(role.id, role.name);
  });
  return map;
});

const roleName = (roleId) => roleMap.value.get(roleId) || '-';

const resolveAvatar = (path) => {
  const fallback = 'common/default_img.jpg';
  const target = path || fallback;
  if (/^https?:\/\//i.test(target)) {
    return target;
  }
  const clean = target.startsWith('/') ? target.slice(1) : target;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const formatAddress = (address) => (address ? address : '未填写地址');

const formatMobile = (mobile) => {
  if (!mobile) return '-';
  const str = String(mobile);
  if (str.length === 11) {
    return `${str.slice(0, 3)} ${str.slice(3, 7)} ${str.slice(7)}`;
  }
  return str;
};

const formatDate = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const mi = String(date.getMinutes()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
};

const loadAdmins = async () => {
  loading.value = true;
  try {
    const res = await fetchAdminAccounts({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      name: searchName.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      admins.value = res.data.pageInfo?.list || [];
      Object.assign(pageInfo, {
        pageNum: res.data.pageInfo?.pageNum || 1,
        pageSize: res.data.pageInfo?.pageSize || pageInfo.pageSize,
        pages: res.data.pageInfo?.pages || 0,
        total: res.data.pageInfo?.total || 0,
      });
      roles.value = res.data.roles || [];
    } else {
      admins.value = [];
      window.alert(res?.msg || '加载管理员列表失败');
    }
  } catch (error) {
    console.error('加载管理员列表失败', error);
    window.alert('网络错误，加载管理员列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadAdmins();
};

const resetSearch = () => {
  searchName.value = '';
  handleSearch();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadAdmins();
};

const openCreate = () => {
  dialog.visible = true;
  dialog.mode = 'create';
  dialog.target = null;
};

const openEdit = (admin) => {
  dialog.visible = true;
  dialog.mode = 'edit';
  dialog.target = { ...admin };
};

const closeDialog = () => {
  if (dialog.submitting) return;
  dialog.visible = false;
  dialog.target = null;
  dialog.mode = 'create';
};

function clearRouteAction() {
  if (!route.query.action) return;
  const query = { ...route.query };
  delete query.action;
  router.replace({ query });
}

function handleRouteAction() {
  const action = route.query.action;
  if (!action) return;
  if (action === 'add') {
    openCreate();
  } else if (action === 'edit') {
    window.alert('请在列表中选择要修改的管理员后，点击右侧“编辑”按钮完成修改。');
  } else if (action === 'delete') {
    window.alert('删除操作需要在列表中选择具体管理员后点击“删除”按钮执行。');
  }
  clearRouteAction();
}

const handleSubmit = async (payload) => {
  dialog.submitting = true;
  let shouldClose = false;
  try {
    let res;
    if (dialog.mode === 'create') {
      res = await createAdminAccount(payload);
    } else if (dialog.target?.id) {
      res = await updateAdminAccount(dialog.target.id, payload);
    }
    if (res?.code === 0) {
      window.alert(res?.msg || '操作成功');
      shouldClose = true;
      await loadAdmins();
    } else {
      window.alert(res?.msg || '保存管理员信息失败');
    }
  } catch (error) {
    console.error('保存管理员信息失败', error);
    window.alert('网络错误，保存管理员信息失败');
  } finally {
    dialog.submitting = false;
    if (shouldClose) {
      closeDialog();
    }
  }
};

const handleToggleState = async (admin) => {
  if (!admin?.id) return;
  try {
    const res = await toggleAdminAccountState(admin.id);
    if (res?.code === 0) {
      window.alert('状态更新成功');
      await loadAdmins();
    } else {
      window.alert(res?.msg || '状态更新失败');
    }
  } catch (error) {
    console.error('更新管理员状态失败', error);
    window.alert('网络错误，更新管理员状态失败');
  }
};

const handleDelete = async (admin) => {
  if (!admin?.id) return;
  if (!window.confirm(`确认删除管理员「${admin.name || admin.id}」吗？`)) {
    return;
  }
  try {
    const res = await deleteAdminAccount(admin.id);
    if (res?.code === 0) {
      window.alert(res?.msg || '删除成功');
      if (admins.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadAdmins();
    } else {
      window.alert(res?.msg || '删除管理员失败');
    }
  } catch (error) {
    console.error('删除管理员失败', error);
    window.alert('网络错误，删除管理员失败');
  }
};

onMounted(() => {
  loadAdmins();
  handleRouteAction();
});

watch(
  () => route.query.action,
  () => {
    handleRouteAction();
  },
);
</script>

<style scoped>
.admin-accounts {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #0f172a;
}

.page-header p {
  margin: 4px 0 0;
  color: #64748b;
}

.actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 240px;
  height: 38px;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  padding: 0 14px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.18);
}

.card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
  padding: 24px;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.admin-table th,
.admin-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.admin-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.6);
}

.center {
  text-align: center;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  object-fit: cover;
  border: 2px solid rgba(99, 102, 241, 0.18);
}

.admin-name {
  margin: 0;
  font-weight: 600;
  color: #111827;
}

.admin-meta {
  margin: 2px 0 0;
  font-size: 12px;
  color: #94a3b8;
}

.state-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.3);
  color: #475569;
  font-weight: 600;
  font-size: 12px;
}

.state-tag.open {
  background: rgba(16, 185, 129, 0.18);
  color: #0f766e;
}

.row-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.btn {
  border: none;
  border-radius: 10px;
  padding: 0 14px;
  height: 34px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn.primary {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
}

.btn.secondary {
  background: rgba(96, 165, 250, 0.15);
  color: #1d4ed8;
}

.btn.ghost {
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
}

.btn.danger {
  background: rgba(248, 113, 113, 0.15);
  color: #b91c1c;
}

.btn.danger:hover {
  background: rgba(248, 113, 113, 0.25);
}

.loading-cell,
.empty-cell {
  text-align: center;
  padding: 40px 0;
  color: #94a3b8;
}

.pagination {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
  font-size: 14px;
}

.pagination button {
  height: 32px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid #cbd5f5;
  background: #f1f5f9;
  cursor: pointer;
  transition: background 0.2s ease;
}

.pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pagination button:not(:disabled):hover {
  background: #e2e8f0;
}

@media (max-width: 960px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .search-input {
    flex: 1;
    min-width: 200px;
  }

  .admin-table th,
  .admin-table td {
    font-size: 13px;
  }
}
</style>

