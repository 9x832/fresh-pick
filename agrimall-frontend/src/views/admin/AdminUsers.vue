<template>
  <section class="user-manage">
    <header class="page-header">
      <div>
        <h2>用户管理</h2>
        <p>查看商城用户列表，支持搜索、重置密码、删除操作。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchContent"
          type="text"
          class="search-input"
          placeholder="按用户名或邮箱搜索"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn ghost" @click="resetSearch">清空</button>
      </div>
    </header>

    <section class="card">
      <table class="user-table">
        <thead>
          <tr>
            <th style="width: 80px;">用户ID</th>
            <th style="width: 80px;">头像</th>
            <th>用户名</th>
            <th style="width: 160px;">手机号</th>
            <th style="width: 200px;">邮箱</th>
            <th style="width: 160px;">注册时间</th>
            <th style="width: 160px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="loading-cell">用户数据加载中...</td>
          </tr>
          <tr v-else-if="!users.length">
            <td colspan="7" class="empty-cell">暂无用户记录</td>
          </tr>
          <tr v-for="user in users" :key="user.id">
            <td class="center">{{ user.id }}</td>
            <td class="center">
              <img
                :src="resolveAvatar(user.headPic)"
                alt="用户头像"
                class="avatar"
              />
            </td>
            <td class="center">{{ user.username || '-' }}</td>
            <td class="center">{{ user.phone || '-' }}</td>
            <td class="center">{{ user.email || '-' }}</td>
            <td class="center">{{ formatDate(user.createTime) }}</td>
            <td class="center">
              <div class="row-actions">
                <button type="button" class="btn ghost" @click="openResetDialog(user)">
                  重置密码
                </button>
                <button type="button" class="btn danger ghost" @click="handleDelete(user)">
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
        <span>共 {{ pageInfo.total || 0 }} 位用户</span>
      </footer>
    </section>

    <div v-if="showDialog" class="dialog-backdrop">
      <div class="dialog">
        <header>
          <h3>重置用户密码</h3>
        </header>
        <section class="dialog-body">
          <p>目标用户：{{ targetUser?.username || targetUser?.email || '-' }}</p>
          <label for="new-password" class="form-label">新密码</label>
          <input
            id="new-password"
            v-model.trim="newPassword"
            type="password"
            placeholder="请输入新密码（6-20 位）"
            maxlength="20"
          />
        </section>
        <footer class="dialog-footer">
          <button type="button" class="btn secondary" @click="closeDialog" :disabled="submitting">
            取消
          </button>
          <button type="button" class="btn primary" @click="submitPassword" :disabled="submitting">
            {{ submitting ? '提交中...' : '确认重置' }}
          </button>
        </footer>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { deleteUser, fetchUsers, updateUserPassword } from '@/apis/admin';

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const users = ref([]);
const loading = ref(false);
const submitting = ref(false);
const searchContent = ref('');
const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  pages: 0,
  total: 0,
});

const showDialog = ref(false);
const targetUser = ref(null);
const newPassword = ref('');

const resolveAvatar = (path) => {
  if (!path) {
    return 'https://via.placeholder.com/48x48.png?text=User';
  }
  if (/^https?:\/\//i.test(path)) {
    return path;
  }
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
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

const loadUsers = async () => {
  loading.value = true;
  try {
    const res = await fetchUsers({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      content: searchContent.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      users.value = res.data.list || [];
      Object.assign(pageInfo, {
        pageNum: res.data.pageNum || 1,
        pageSize: res.data.pageSize || pageInfo.pageSize,
        pages: res.data.pages || 0,
        total: res.data.total || 0,
      });
    } else {
      users.value = [];
      window.alert(res?.msg || '加载用户列表失败');
    }
  } catch (error) {
    console.error('加载用户列表失败', error);
    window.alert('网络错误，加载用户列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadUsers();
};

const resetSearch = () => {
  searchContent.value = '';
  handleSearch();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadUsers();
};

const openResetDialog = (user) => {
  targetUser.value = user;
  newPassword.value = '';
  showDialog.value = true;
};

const closeDialog = () => {
  if (submitting.value) return;
  showDialog.value = false;
};

const submitPassword = async () => {
  if (!targetUser.value?.id) return;
  if (!newPassword.value || newPassword.value.length < 6) {
    window.alert('请输入至少 6 位的新密码');
    return;
  }
  submitting.value = true;
  try {
    const res = await updateUserPassword(targetUser.value.id, newPassword.value);
    if (res?.code === 0) {
      window.alert(res.msg || '密码重置成功');
      showDialog.value = false;
    } else {
      window.alert(res?.msg || '重置密码失败');
    }
  } catch (error) {
    console.error('重置密码失败', error);
    window.alert('网络错误，重置密码失败');
  } finally {
    submitting.value = false;
  }
};

const handleDelete = async (user) => {
  if (!user?.id) return;
  if (!window.confirm(`确认删除用户「${user.username || user.email || user.id}」？`)) {
    return;
  }
  try {
    const res = await deleteUser(user.id);
    if (res?.code === 0) {
      window.alert(res.msg || '删除成功');
      if (users.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadUsers();
    } else {
      window.alert(res?.msg || '删除用户失败');
    }
  } catch (error) {
    console.error('删除用户失败', error);
    window.alert('网络错误，删除用户失败');
  }
};

onMounted(() => {
  loadUsers();
});
</script>

<style scoped>
.user-manage {
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
  border-color: #38bdf8;
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.25);
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 0 16px;
  height: 38px;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn.primary {
  background: linear-gradient(135deg, #6366f1, #4338ca);
  color: #fff;
}

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
}

.btn.ghost {
  background: rgba(99, 102, 241, 0.12);
  color: #3730a3;
}

.btn.danger {
  background: rgba(248, 113, 113, 0.15);
  color: #b91c1c;
}

.btn.danger:hover {
  background: rgba(248, 113, 113, 0.25);
}

.card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
  padding: 24px;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.user-table th,
.user-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.user-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.6);
}

.center {
  text-align: center;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(96, 165, 250, 0.35);
}

.row-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
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

.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  padding: 24px;
}

.dialog {
  width: min(480px, 100%);
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.25);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog header h3 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
  color: #475569;
}

.form-label {
  font-weight: 600;
}

.dialog input {
  height: 38px;
  border: 1px solid #cbd5f5;
  border-radius: 10px;
  padding: 0 12px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.dialog input:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 960px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .search-input {
    flex: 1;
    min-width: 200px;
  }
}
</style>

