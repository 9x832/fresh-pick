<template>
  <div v-if="open" class="dialog-backdrop" @click.self="$emit('close')">
    <section class="dialog">
      <header class="dialog-header">
        <div class="headline">
          <p class="eyebrow">邮件详情</p>
          <h3>{{ mailTitle }}</h3>
          <div class="meta-line">
            <span>发件人：{{ senderName }}</span>
            <template v-if="receiverChips.length">
              <span class="separator">·</span>
              <span>收件人：</span>
              <span class="recipient-chip" v-for="chip in receiverChips" :key="chip">{{ chip }}</span>
            </template>
            <span v-else>· 收件人：—</span>
          </div>
        </div>
        <button type="button" class="btn ghost" @click="$emit('close')">关闭</button>
      </header>

      <section class="dialog-content">
        <div class="info-grid">
          <div class="info-card">
            <span class="label">发送时间</span>
            <span class="value">{{ formatDate(mailEntity.createTime) || '—' }}</span>
          </div>
          <div class="info-card">
            <span class="label">最后更新</span>
            <span class="value">{{ formatDate(mailEntity.updateTime) || '—' }}</span>
          </div>
          <div class="info-card">
            <span class="label">邮件编号</span>
            <span class="value">{{ mailEntity.id ?? '—' }}</span>
          </div>
          <div class="info-card">
            <span class="label">附件</span>
            <span class="value">{{ attachments.length ? `${attachments.length} 个` : '无附件' }}</span>
          </div>
        </div>

        <section class="body-section">
          <header>
            <h4>正文内容</h4>
            <button type="button" class="btn text" @click="copyContent" :disabled="!mailContent">
              {{ copied ? '已复制' : '复制正文' }}
            </button>
          </header>
          <article class="body" v-if="mailContent">{{ mailContent }}</article>
          <div v-else class="empty-body">
            <span>暂无正文内容</span>
          </div>
        </section>

        <section class="attachment" v-if="attachments.length">
          <header>
            <h4>附件（{{ attachments.length }}）</h4>
            <button type="button" class="btn text" @click="downloadAll">批量下载</button>
          </header>
          <ul>
            <li v-for="item in attachments" :key="item.id">
              <div class="file-meta">
                <span class="file-icon">📎</span>
                <div>
                  <a :href="resolveAttachmentUrl(item)" target="_blank" rel="noopener noreferrer">
                    {{ item.name || item.filename || '未命名附件' }}
                  </a>
                  <p class="sub">{{ formatAttachmentMeta(item) }}</p>
                </div>
              </div>
              <button type="button" class="link danger" @click="$emit('delete-attachment', item.id)">
                删除
              </button>
            </li>
          </ul>
        </section>
        <section v-else class="attachment empty">
          <h4>附件</h4>
          <p>该邮件未包含附件</p>
        </section>
      </section>

      <transition name="fade">
        <div v-if="loading" class="loading-mask">
          <div class="spinner"></div>
          <span>加载中…</span>
        </div>
      </transition>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  mailId: {
    type: Number,
    default: null,
  },
  mail: {
    type: Object,
    default: () => ({}),
  },
  admins: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'delete-attachment']);

const data = computed(() => props.mail || {});
const adminList = computed(() => {
  if (Array.isArray(props.admins) && props.admins.length) {
    return props.admins;
  }
  const fallback = data.value?.admins;
  return Array.isArray(fallback) ? fallback : [];
});

const mailEntity = computed(() => data.value?.mail || {});
const mailTitle = computed(() => mailEntity.value.title || '邮件详情');
const mailContent = computed(() => mailEntity.value.content || '');

const resolveAdminName = (id) => {
  if (id === null || id === undefined) return '';
  const admin = adminList.value.find((item) => String(item.id) === String(id));
  if (!admin) return '';
  return admin.name || admin.username || '';
};

const senderName = computed(() => {
  const mail = mailEntity.value || {};
  const senderId =
    mail.sendAdminId ??
    mail.senderId ??
    mail.sender_id ??
    mail.sender?.id ??
    mail.sendAdmin?.id ??
    null;
  const nameFromList = resolveAdminName(senderId);
  const fallbackName =
    mail.sendAdminName ||
    mail.senderName ||
    mail.sender?.name ||
    mail.sender?.username ||
    mail.sendAdmin?.name ||
    '';
  return nameFromList || fallbackName || '—';
});

const receiverChips = computed(() => {
  const mail = mailEntity.value || {};
  if (!mail) return [];
  const rawIds =
    mail.receiveAdminIdList ??
    mail.receiveAdminIds ??
    mail.receiverIds ??
    mail.receiveAdminId ??
    mail.receiverId ??
    mail.receiver?.id ??
    mail.receiveAdmins ??
    mail.receivers ??
    null;
  const ids = Array.isArray(rawIds) ? rawIds : rawIds != null ? [rawIds] : null;
  const idList = Array.isArray(ids)
    ? ids
    : ids
      ? String(ids)
          .split(',')
          .map((id) => id.trim())
          .filter(Boolean)
      : [];
  if (!idList.length) {
    const fallback =
      mail.receiverName ||
      mail.receiveAdminName ||
      mail.receiver?.name ||
      mail.receiver?.username ||
      mail.receiveAdmin?.name ||
      '';
    return fallback ? [fallback] : [];
  }
  return idList
    .map((id) => {
      const nameFromList = resolveAdminName(id);
      if (nameFromList) return nameFromList;
      if (typeof id === 'object' && id !== null) {
        return id.name || id.username || id.id;
      }
      return id;
    })
    .filter(Boolean);
});

const attachments = computed(() => {
  const list = Array.isArray(data.value?.attachments) ? data.value.attachments : [];
  const mail = mailEntity.value || {};
  const attachmentIds = [
    mail.attachmentOne,
    mail.attachmentTwo,
    mail.attachmentThree,
  ].filter((id) => id != null);
  if (!attachmentIds.length) return [];
  return list.filter((item) => attachmentIds.includes(item.id));
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
  const ss = String(date.getSeconds()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
};

const formatAttachmentMeta = (item) => {
  if (!item) return '';
  const parts = [];
  if (item.ext) {
    parts.push(String(item.ext).toUpperCase());
  }
  if (item.size) {
    parts.push(formatSize(item.size));
  }
  return parts.join(' · ');
};

const formatSize = (size) => {
  // 后端返回的 size 是 KB（BigDecimal），需要转换为字节再格式化
  const num = Number(size);
  if (!Number.isFinite(num) || num <= 0) return '';
  // 如果数值很小（小于1），可能是字节数，否则认为是KB
  if (num < 1) {
    // 字节数
    if (num < 1024) return `${num.toFixed(0)} B`;
    if (num < 1024 * 1024) return `${(num / 1024).toFixed(1)} KB`;
    if (num < 1024 * 1024 * 1024) return `${(num / 1024 / 1024).toFixed(1)} MB`;
    return `${(num / 1024 / 1024 / 1024).toFixed(2)} GB`;
  } else {
    // KB
    if (num < 1024) return `${num.toFixed(1)} KB`;
    if (num < 1024 * 1024) return `${(num / 1024).toFixed(1)} MB`;
    return `${(num / 1024 / 1024).toFixed(2)} GB`;
  }
};

const resolveAttachmentUrl = (item) => {
  if (!item) return '#';
  if (item.url && /^https?:\/\//i.test(item.url)) return item.url;
  const filename = item.path || item.url || item.filename;
  if (!filename) return '#';
  const clean = filename.startsWith('/') ? filename.slice(1) : filename;
  const base = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
  // 使用附件下载接口
  return `${base}/api/common/upload/attachment/${item.id}`;
};

const copied = ref(false);

const copyContent = async () => {
  if (!mailContent.value) return;
  try {
    await navigator.clipboard.writeText(mailContent.value);
    copied.value = true;
  } catch (error) {
    console.error('复制正文失败', error);
  }
};

const downloadAll = () => {
  attachments.value.forEach((item) => {
    const url = resolveAttachmentUrl(item);
    if (!url || url === '#') return;
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.rel = 'noopener noreferrer';
    link.download = item.name || item.filename || 'attachment';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  });
};

watch(
  () => props.open,
  (val) => {
    if (!val) {
      copied.value = false;
    }
  },
);

watch(
  () => mailContent.value,
  () => {
    copied.value = false;
  },
);

const close = () => emit('close');

defineExpose({ close });
</script>

<style scoped>
.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 12px;
  z-index: 1200;
}

.dialog {
  position: relative;
  width: min(820px, 100%);
  max-height: 86vh;
  background: linear-gradient(180deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 22px;
  box-shadow: 0 22px 48px rgba(15, 23, 42, 0.24);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding: clamp(20px, 4vw, 28px) clamp(20px, 5vw, 40px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.headline {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.eyebrow {
  font-size: 13px;
  letter-spacing: 0.08em;
  color: #818cf8;
  text-transform: uppercase;
}

.dialog-header h3 {
  margin: 0;
  font-size: clamp(20px, 3.2vw, 24px);
  color: #111827;
  line-height: 1.3;
}

.meta-line {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
  align-items: center;
}

.separator {
  color: rgba(148, 163, 184, 0.8);
}

.recipient-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(79, 70, 229, 0.12);
  color: #4338ca;
  font-size: 12px;
}

.dialog-content {
  padding: clamp(20px, 4vw, 32px) clamp(20px, 5vw, 40px) clamp(24px, 5vw, 36px);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: clamp(18px, 4vw, 28px);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.info-card {
  padding: 14px 16px;
  background: rgba(226, 232, 240, 0.45);
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-card .label {
  font-size: 12px;
  color: #64748b;
  letter-spacing: 0.04em;
}

.info-card .value {
  font-size: 14px;
  color: #1f2937;
  font-weight: 600;
}

.body-section {
  background: #ffffff;
  border-radius: 20px;
  padding: 20px 24px;
  box-shadow: inset 0 0 0 1px rgba(226, 232, 240, 0.7);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.body-section header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.body-section h4 {
  margin: 0;
  font-size: 16px;
  color: #1e293b;
}

.body {
  white-space: pre-wrap;
  line-height: 1.7;
  color: #1f2937;
  font-size: 14px;
  max-height: 320px;
  overflow-y: auto;
  padding-right: 4px;
}

.empty-body {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: #94a3b8;
  font-size: 14px;
}

.attachment {
  background: #ffffff;
  border-radius: 20px;
  padding: 20px 24px;
  box-shadow: inset 0 0 0 1px rgba(226, 232, 240, 0.7);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.attachment header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.attachment h4 {
  margin: 0;
  font-size: 16px;
  color: #0f172a;
}

.attachment.empty {
  color: #94a3b8;
}

.attachment ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.attachment li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(226, 232, 240, 0.4);
}

.file-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-icon {
  font-size: 18px;
}

.attachment a {
  color: #2563eb;
  text-decoration: none;
  font-weight: 600;
}

.attachment a:hover {
  text-decoration: underline;
}

.sub {
  margin: 6px 0 0;
  font-size: 12px;
  color: #94a3b8;
}

.btn {
  padding: 8px 16px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn.ghost {
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
}

.btn.ghost:hover {
  background: rgba(99, 102, 241, 0.2);
}

.btn.text {
  background: none;
  color: #4338ca;
  padding: 0;
}

.btn.text:disabled {
  color: #cbd5f5;
  cursor: not-allowed;
}

.link {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 13px;
  color: #6366f1;
}

.link.danger {
  color: #dc2626;
}

.loading-mask {
  position: absolute;
  inset: 0;
  background: rgba(248, 250, 255, 0.82);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 14px;
  color: #4338ca;
  backdrop-filter: blur(2px);
}

.spinner {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 3px solid rgba(99, 102, 241, 0.25);
  border-top-color: #4338ca;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 640px) {
  .dialog {
    border-radius: 18px;
  }

  .dialog-header {
    padding: 20px;
  }

  .dialog-content {
    padding: 20px;
  }

  .info-grid {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  }
}
</style>

