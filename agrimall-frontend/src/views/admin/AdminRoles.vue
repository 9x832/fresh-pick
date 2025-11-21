<template>
  <section class="role-manage">
    <header class="page-header">
      <div>
        <h2>角色管理</h2>
        <p>维护后台角色与权限范围，可创建、编辑角色并分配菜单权限。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchName"
          type="text"
          class="search-input"
          placeholder="按角色名称搜索"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn ghost" @click="resetSearch">清空</button>
        <button type="button" class="btn primary" @click="openCreate">新增角色</button>
      </div>
    </header>

    <section class="card">
      <table class="role-table">
        <thead>
          <tr>
            <th style="width: 80px;">ID</th>
            <th style="width: 200px;">角色名称</th>
            <th>角色描述</th>
            <th style="width: 180px;">创建时间</th>
            <th style="width: 180px;">更新时间</th>
            <th style="width: 200px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="loading-cell">角色列表加载中...</td>
          </tr>
          <tr v-else-if="!roles.length">
            <td colspan="6" class="empty-cell">暂无角色记录</td>
          </tr>
          <tr v-for="role in roles" :key="role.id">
            <td class="center">{{ role.id }}</td>
            <td class="center">{{ role.name || '-' }}</td>
            <td>{{ role.description || '未填写描述' }}</td>
            <td class="center">{{ formatDate(role.createTime) }}</td>
            <td class="center">{{ formatDate(role.updateTime) }}</td>
            <td class="center">
              <div class="row-actions">
                <button type="button" class="btn ghost" @click="openEdit(role)">编辑</button>
                <button type="button" class="btn secondary" @click="openAuthority(role)">权限</button>
                <button type="button" class="btn danger ghost" @click="handleDelete(role)">删除</button>
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
        <span>共 {{ pageInfo.total || 0 }} 个角色</span>
      </footer>
    </section>

    <AdminRoleDialog
      :open="dialog.visible"
      :mode="dialog.mode"
      :role="dialog.target"
      :submitting="dialog.submitting"
      @close="closeDialog"
      @submit="handleSubmit"
    />

    <RoleAuthorityDialog
      v-if="authority.visible"
      :open="authority.visible"
      :role-name="authority.role?.name"
      :menus="authority.menus"
      :selected="authority.selected"
      :saving="authority.saving"
      @close="closeAuthority"
      @submit="saveAuthority"
    />
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import {
  createRole,
  deleteRole,
  fetchRoleAuthority,
  fetchRoles,
  saveRoleAuthority,
  updateRole,
} from '@/apis/admin';
import AdminRoleDialog from './components/AdminRoleDialog.vue';
import RoleAuthorityDialog from './components/RoleAuthorityDialog.vue';

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
  target: null,
  submitting: false,
});

const authority = reactive({
  visible: false,
  role: null,
  menus: {},
  selected: [],
  saving: false,
});

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

const loadRoles = async () => {
  loading.value = true;
  try {
    const res = await fetchRoles({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      name: searchName.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      roles.value = res.data.list || [];
      Object.assign(pageInfo, {
        pageNum: res.data.pageNum || 1,
        pageSize: res.data.pageSize || pageInfo.pageSize,
        pages: res.data.pages || 0,
        total: res.data.total || 0,
      });
    } else {
      roles.value = [];
      window.alert(res?.msg || '加载角色列表失败');
    }
  } catch (error) {
    console.error('加载角色列表失败', error);
    window.alert('网络错误，加载角色列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadRoles();
};

const resetSearch = () => {
  searchName.value = '';
  handleSearch();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadRoles();
};

const openCreate = () => {
  dialog.visible = true;
  dialog.mode = 'create';
  dialog.target = null;
};

const openEdit = (role) => {
  dialog.visible = true;
  dialog.mode = 'edit';
  dialog.target = { ...role };
};

const closeDialog = () => {
  if (dialog.submitting) return;
  dialog.visible = false;
  dialog.target = null;
  dialog.mode = 'create';
};

const handleSubmit = async (payload) => {
  dialog.submitting = true;
  try {
    let res;
    if (dialog.mode === 'create') {
      res = await createRole(payload);
    } else if (dialog.target?.id) {
      res = await updateRole(dialog.target.id, payload);
    }
    if (res?.code === 0) {
      window.alert(res?.msg || '操作成功');
      closeDialog();
      await loadRoles();
    } else {
      window.alert(res?.msg || '保存角色信息失败');
    }
  } catch (error) {
    console.error('保存角色信息失败', error);
    window.alert('网络错误，保存角色信息失败');
  } finally {
    dialog.submitting = false;
  }
};

const handleDelete = async (role) => {
  if (!role?.id) return;
  if (!window.confirm(`确认删除角色「${role.name || role.id}」吗？`)) {
    return;
  }
  try {
    const res = await deleteRole(role.id);
    if (res?.code === 0) {
      window.alert(res?.msg || '删除成功');
      if (roles.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadRoles();
    } else {
      window.alert(res?.msg || '删除角色失败');
    }
  } catch (error) {
    console.error('删除角色失败', error);
    window.alert('网络错误，删除角色失败');
  }
};

const openAuthority = async (role) => {
  if (!role?.id) return;
  try {
    const res = await fetchRoleAuthority(role.id);
    if (res?.code === 0 && res.data) {
      authority.role = role;
      authority.menus = res.data;
      authority.selected = (res.data.authorityList || []).map((item) => item.menuId);
      authority.visible = true;
    } else {
      window.alert(res?.msg || '获取角色权限信息失败');
    }
  } catch (error) {
    console.error('获取角色权限失败', error);
    window.alert('网络错误，获取角色权限失败');
  }
};

const closeAuthority = () => {
  if (authority.saving) return;
  authority.visible = false;
  authority.role = null;
  authority.menus = {};
  authority.selected = [];
};

const saveAuthority = async (menuIds) => {
  if (!authority.role?.id) return;
  authority.saving = true;
  try {
    const res = await saveRoleAuthority(authority.role.id, menuIds || []);
    if (res?.code === 0) {
      window.alert(res?.msg || '权限保存成功');
      closeAuthority();
    } else {
      window.alert(res?.msg || '保存权限失败');
    }
  } catch (error) {
    console.error('保存角色权限失败', error);
    window.alert('网络错误，保存角色权限失败');
  } finally {
    authority.saving = false;
  }
};

onMounted(() => {
  loadRoles();
});
</script>

<style scoped>
.role-manage {
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

.role-table {
  width: 100%;
  border-collapse: collapse;
}

.role-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.role-table th,
.role-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.role-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.6);
}

.center {
  text-align: center;
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
  background: rgba(16, 185, 129, 0.16);
  color: #047857;
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

  .role-table th,
  .role-table td {
    font-size: 13px;
  }
}
</style>

