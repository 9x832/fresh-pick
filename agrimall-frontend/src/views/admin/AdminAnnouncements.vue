<template>
  <section class="announcement-manage">
    <header class="page-header">
      <div>
        <h2>公告管理</h2>
        <p>发布重要通知，及时触达所有用户。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchContent"
          type="text"
          class="search-input"
          placeholder="按公告内容关键字搜索"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn primary" @click="openCreateDialog">
          <i class="ri-add-line" aria-hidden="true"></i> 发布公告
        </button>
      </div>
    </header>

    <section class="card">
      <table class="announcement-table">
        <thead>
          <tr>
            <th style="width: 80px;">公告ID</th>
            <th>公告内容</th>
            <th style="width: 140px;">发布管理员</th>
            <th style="width: 180px;">发布时间</th>
            <th style="width: 110px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="loading-cell">数据加载中...</td>
          </tr>
          <tr v-else-if="!announcements.length">
            <td colspan="5" class="empty-cell">暂无公告，点击右上角按钮发布第一条吧！</td>
          </tr>
          <tr v-for="item in announcements" :key="item.id">
            <td class="center">{{ item.id }}</td>
            <td class="content-cell">{{ item.content }}</td>
            <td class="center">{{ item.adminId ?? '-' }}</td>
            <td class="center">{{ formatDate(item.createTime) }}</td>
            <td class="center">
              <button type="button" class="btn danger ghost" @click="handleDelete(item)">
                删除
              </button>
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
        <span>共 {{ pageInfo.total || 0 }} 条</span>
      </footer>
    </section>

    <div v-if="showDialog" class="dialog-backdrop">
      <div class="dialog">
        <header class="dialog-header">
          <h3>发布公告</h3>
          <button type="button" class="icon-btn" @click="closeDialog" :disabled="submitting">
            <i class="ri-close-line"></i>
          </button>
        </header>
        <section class="dialog-content">
          <div class="form-field">
            <label class="form-label" for="announcement-content">公告内容</label>
            <textarea
              id="announcement-content"
              v-model.trim="form.content"
              rows="5"
              maxlength="256"
              placeholder="请输入公告内容（1-256 字）"
            ></textarea>
            <small class="hint">已输入 {{ form.content.length }} / 256 字</small>
          </div>
        </section>
        <footer class="dialog-footer">
          <button type="button" class="btn ghost" @click="closeDialog" :disabled="submitting">
            取消
          </button>
          <button type="button" class="btn primary" @click="submitAnnouncement" :disabled="submitting">
            {{ submitting ? '发布中...' : '确认发布' }}
          </button>
        </footer>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { createAnnouncement, deleteAnnouncement, fetchAnnouncements } from '@/apis/admin';

const announcements = ref([]);
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
const form = reactive({
  content: '',
});

const formatDate = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const mi = String(date.getMinutes()).padStart(2, '0');
  const ss = String(date.getSeconds()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
};

const loadAnnouncements = async () => {
  loading.value = true;
  try {
    const res = await fetchAnnouncements({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      content: searchContent.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      announcements.value = res.data.list || [];
      Object.assign(pageInfo, {
        pageNum: res.data.pageNum || 1,
        pageSize: res.data.pageSize || pageInfo.pageSize,
        pages: res.data.pages || 0,
        total: res.data.total || 0,
      });
    } else {
      announcements.value = [];
      window.alert(res?.msg || '加载公告列表失败');
    }
  } catch (error) {
    console.error('加载公告列表失败', error);
    window.alert('网络错误，无法加载公告列表');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadAnnouncements();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadAnnouncements();
};

const openCreateDialog = () => {
  form.content = '';
  showDialog.value = true;
};

const closeDialog = () => {
  if (submitting.value) return;
  showDialog.value = false;
};

const submitAnnouncement = async () => {
  if (!form.content) {
    window.alert('公告内容不能为空');
    return;
  }
  if (form.content.length > 256) {
    window.alert('公告内容长度不能超过 256 字');
    return;
  }
  submitting.value = true;
  try {
    const res = await createAnnouncement({
      content: form.content,
    });
    if (res?.code === 0) {
      window.alert(res.msg || '发布成功');
      showDialog.value = false;
      pageInfo.pageNum = 1;
      await loadAnnouncements();
    } else {
      window.alert(res?.msg || '发布公告失败');
    }
  } catch (error) {
    console.error('发布公告失败', error);
    window.alert('网络错误，发布公告失败');
  } finally {
    submitting.value = false;
  }
};

const handleDelete = async (item) => {
  if (!item?.id) {
    return;
  }
  if (!window.confirm(`确定要删除公告 #${item.id} 吗？`)) {
    return;
  }
  try {
    const res = await deleteAnnouncement(item.id);
    if (res?.code === 0) {
      window.alert(res.msg || '删除成功');
      if (announcements.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadAnnouncements();
    } else {
      window.alert(res?.msg || '删除公告失败');
    }
  } catch (error) {
    console.error('删除公告失败', error);
    window.alert('网络错误，删除公告失败');
  }
};

onMounted(() => {
  loadAnnouncements();
});
</script>

<style scoped>
.announcement-manage {
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
  border: 1px solid #cbd5f5;
  border-radius: 10px;
  padding: 0 14px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-input:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
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
  transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.btn.primary {
  background: linear-gradient(135deg, #10b981, #0f766e);
  color: #fff;
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(16, 185, 129, 0.35);
}

.btn.secondary {
  background: #e2e8f0;
  color: #1e293b;
}

.btn.secondary:hover {
  background: #cbd5f5;
}

.btn.danger {
  background: #f87171;
  color: #fff;
}

.btn.danger:hover {
  background: #ef4444;
}

.btn.danger.ghost {
  background: rgba(248, 113, 113, 0.15);
  color: #b91c1c;
}

.btn.danger.ghost:hover {
  background: rgba(248, 113, 113, 0.25);
}

.card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
  padding: 24px;
  overflow: hidden;
}

.announcement-table {
  width: 100%;
  border-collapse: collapse;
}

.announcement-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.announcement-table th,
.announcement-table td {
  padding: 14px 16px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1e293b;
  text-align: left;
  vertical-align: top;
}

.announcement-table th {
  font-weight: 600;
  color: #0f172a;
}

.announcement-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.5);
}

.center {
  text-align: center;
}

.content-cell {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #1f2933;
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
  width: min(500px, 100%);
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 24px 50px rgba(15, 23, 42, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.dialog-header h3 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}

.dialog-content {
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 0 24px 20px;
}

.icon-btn {
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(226, 232, 240, 0.8);
  color: #475569;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.icon-btn:hover {
  background: rgba(148, 163, 184, 0.4);
  color: #1e293b;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dialog textarea {
  width: 92%;
  border-radius: 12px;
  border: 1px solid #cbd5f5;
  padding: 12px;
  resize: vertical;
  min-height: 120px;
  outline: none;
  font-size: 14px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.dialog textarea:focus {
  border-color: #38bdf8;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.dialog footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog .hint {
  display: block;
  margin-top: 6px;
  color: #94a3b8;
  font-size: 12px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: #475569;
  font-weight: 600;
}

.loading-card {
  text-align: center;
  color: #64748b;
  padding: 36px;
  font-size: 16px;
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

