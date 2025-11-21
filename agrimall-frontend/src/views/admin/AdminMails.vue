<template>
  <section class="mail-manage">
    <header class="page-header">
      <div>
        <h2>邮件列表</h2>
        <p>集中查看管理员往来邮件，支持搜索、查看与删除附件。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchTitle"
          type="text"
          class="search-input"
          placeholder="按标题搜索"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn ghost" @click="resetSearch">清空</button>
        <button type="button" class="btn primary" @click="openCompose">写新邮件</button>
      </div>
      <div class="view-toggle">
        <button
          type="button"
          class="btn ghost"
          :class="{ active: viewMode === 'receive' }"
          @click="switchView('receive')"
        >
          收件箱
        </button>
        <button
          type="button"
          class="btn ghost"
          :class="{ active: viewMode === 'sent' }"
          @click="switchView('sent')"
        >
          已发送
        </button>
      </div>
    </header>

    <section class="card">
      <table class="mail-table">
        <thead>
          <tr>
            <th style="width: 80px;">编号</th>
            <th style="width: 140px;">{{ correspondentLabel }}</th>
            <th>标题</th>
            <th style="width: 180px;">发送时间</th>
            <th style="width: 120px;">附件数</th>
            <th style="width: 160px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="loading-cell">邮件加载中...</td>
          </tr>
          <tr v-else-if="!mails.length">
            <td colspan="6" class="empty-cell">暂无邮件</td>
          </tr>
          <tr v-for="mail in mails" :key="mail.id">
            <td class="center">{{ mail.id }}</td>
            <td class="center">{{ resolveCounterparty(mail) }}</td>
            <td>{{ mail.title }}</td>
            <td class="center">{{ formatDate(mail.createTime) }}</td>
            <td class="center">{{ mail.attachmentCount ?? '-' }}</td>
            <td class="center">
              <div class="row-actions">
                <button type="button" class="btn ghost" @click="openDetail(mail.id)">查看</button>
                <button type="button" class="btn danger ghost" @click="handleDelete(mail)">
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
        <span>共 {{ pageInfo.total || 0 }} 封邮件</span>
      </footer>
    </section>

    <MailDetailDialog
      v-if="detailState.visible"
      :mail-id="detailState.mailId"
      :mail="detailMail"
      :admins="detailAdmins.length ? detailAdmins : adminOptions"
      :open="detailState.visible"
      :loading="detailLoading"
      @close="detailState.visible = false"
      @delete-attachment="removeAttachment"
    />
    <MailComposeDialog
      :open="composeState.visible"
      :admins="adminOptions"
      :sending="composeState.sending"
      @close="closeCompose"
      @submit="submitCompose"
    />
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  deleteAttachment,
  deleteMail,
  fetchMailDetail,
  fetchReceiveMails,
  fetchSentMails,
  sendMail,
} from '@/apis/admin';
import MailDetailDialog from './components/MailDetailDialog.vue';
import MailComposeDialog from './components/MailComposeDialog.vue';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const mails = ref([]);
const adminOptions = ref([]);
const viewMode = ref('receive');

const searchTitle = ref('');
const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  pages: 0,
  total: 0,
});

const detailState = reactive({
  visible: false,
  mailId: null,
});

const detailMail = ref(null);
const detailAdmins = ref([]);

const composeState = reactive({
  visible: false,
  sending: false,
});

const correspondentLabel = computed(() => (viewMode.value === 'receive' ? '发件人' : '收件人'));

const clearRouteAction = () => {
  if (!route.query.action) return;
  const query = { ...route.query };
  delete query.action;
  router.replace({ path: route.path, query });
};

const handleRouteAction = () => {
  const action = route.query.action;
  if (!action) return;
  if (action === 'compose') {
    openCompose();
  } else if (action === 'sent' || action === 'receive') {
    viewMode.value = action === 'sent' ? 'sent' : 'receive';
    pageInfo.pageNum = 1;
    loadMails();
  }
  clearRouteAction();
};

const resolveCounterparty = (mail) => {
  if (!mail) return '-';
  const admins = adminOptions.value || [];
  const isReceive = viewMode.value === 'receive';
  const targetId = isReceive ? mail.sendAdminId || mail.senderId : mail.receiveAdminId || mail.receiverId;
  const target = targetId ? admins.find((item) => item.id === targetId) : null;
  const fallbackName = isReceive
    ? mail.sendAdminName || mail.senderName
    : mail.receiveAdminName || mail.receiverName;
  if (target) {
    return target.name || target.username || target.nickname || fallbackName || targetId;
  }
  return fallbackName || targetId || '-';
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
  const ss = String(date.getSeconds()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
};

const loadMails = async () => {
  loading.value = true;
  try {
    const requestApi = viewMode.value === 'receive' ? fetchReceiveMails : fetchSentMails;
    const res = await requestApi({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      title: searchTitle.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      mails.value = res.data.pageInfo?.list || [];
      adminOptions.value = res.data.admins || [];
      Object.assign(pageInfo, {
        pageNum: res.data.pageInfo?.pageNum || 1,
        pageSize: res.data.pageInfo?.pageSize || pageInfo.pageSize,
        pages: res.data.pageInfo?.pages || 0,
        total: res.data.pageInfo?.total || 0,
      });
    } else {
      mails.value = [];
      window.alert(res?.msg || '加载邮件失败');
    }
  } catch (error) {
    console.error('加载邮件失败', error);
    window.alert('网络错误，加载邮件失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadMails();
};

const resetSearch = () => {
  searchTitle.value = '';
  handleSearch();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadMails();
};

const switchView = (mode) => {
  if (viewMode.value === mode) return;
  viewMode.value = mode;
  pageInfo.pageNum = 1;
  detailState.visible = false;
  loadMails();
};

const handleDelete = async (mail) => {
  if (!mail?.id) return;
  if (!window.confirm(`确认删除邮件（ID: ${mail.id}）吗？`)) return;
  try {
    const res = await deleteMail(mail.id);
    if (res?.code === 0) {
      window.alert(res.msg || '删除成功');
      if (mails.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadMails();
    } else {
      window.alert(res?.msg || '删除失败');
    }
  } catch (error) {
    console.error('删除邮件失败', error);
    window.alert('网络错误，删除失败');
  }
};

const loadMailDetail = async (mailId) => {
  try {
    const res = await fetchMailDetail(mailId);
    if (res?.code === 0 && res.data) {
      detailMail.value = res.data;
      detailAdmins.value = Array.isArray(res.data.admins) ? res.data.admins : [];
      return true;
    }
    window.alert(res?.msg || '获取邮件详情失败');
    return false;
  } catch (error) {
    console.error('获取邮件详情失败', error);
    window.alert('网络错误，获取详情失败');
    return false;
  }
};

const openDetail = async (mailId) => {
  if (!mailId) return;
  const success = await loadMailDetail(mailId);
  if (success) {
    detailState.mailId = mailId;
    detailState.visible = true;
  }
};

watch(
  () => detailState.visible,
  (visible) => {
    if (!visible) {
      detailState.mailId = null;
      detailMail.value = null;
      detailAdmins.value = [];
    }
  },
);

const removeAttachment = async (attachmentId) => {
  if (!attachmentId) return;
  if (!window.confirm('确认删除该附件吗？')) return;
  try {
    const res = await deleteAttachment(attachmentId);
    if (res?.code === 0) {
      window.alert(res.msg || '附件删除成功');
      if (detailState.mailId) {
        await loadMailDetail(detailState.mailId);
      }
    } else {
      window.alert(res?.msg || '附件删除失败');
    }
  } catch (error) {
    console.error('删除附件失败', error);
    window.alert('网络错误，删除附件失败');
  }
};

const openCompose = () => {
  if (!adminOptions.value.length) {
    window.alert('尚未加载到可选收件人，如列表仍为空请刷新页面或联系超级管理员。');
  }
  composeState.visible = true;
};

const closeCompose = () => {
  if (composeState.sending) return;
  composeState.visible = false;
};

const submitCompose = async (payload) => {
  if (!payload) return;
  composeState.sending = true;
  try {
    const res = await sendMail(payload);
    if (res?.code === 0) {
      window.alert(res.msg || '邮件发送成功');
      composeState.visible = false;
      if (viewMode.value !== 'sent') {
        viewMode.value = 'sent';
        pageInfo.pageNum = 1;
      }
      detailState.visible = false;
      await loadMails();
    } else {
      window.alert(res?.msg || '邮件发送失败');
    }
  } catch (error) {
    console.error('发送邮件失败', error);
    window.alert('网络错误，邮件发送失败');
  } finally {
    composeState.sending = false;
  }
};

onMounted(() => {
  loadMails();
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
.mail-manage {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
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

.view-toggle {
  display: flex;
  gap: 10px;
}

.view-toggle .btn {
  min-width: 90px;
}

.view-toggle .btn.active {
  background: #4338ca;
  color: #ffffff;
  box-shadow: 0 12px 22px rgba(67, 56, 202, 0.23);
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
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
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

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
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

.card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
  padding: 24px;
}

.mail-table {
  width: 100%;
  border-collapse: collapse;
}

.mail-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.mail-table th,
.mail-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.mail-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.6);
}

.row-actions {
  display: inline-flex;
  gap: 8px;
}

.center {
  text-align: center;
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
}
</style>

