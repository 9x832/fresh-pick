<template>
  <div v-if="open" class="dialog-backdrop" @click.self="handleClose">
    <section class="dialog">
      <header class="dialog-header">
        <div class="headline">
          <p class="eyebrow">写信</p>
          <h3>写邮件</h3>
        </div>
        <button type="button" class="btn ghost" @click="handleClose">取消</button>
      </header>

      <form class="dialog-content" @submit.prevent="submit">
        <div class="form-item">
          <label class="form-label">收件人</label>
          <div class="recipient-toolbar">
            <input
              v-model.trim="searchKeyword"
              type="text"
              class="search"
              placeholder="搜索姓名 / 账号 / ID"
            />
            <span class="count">{{ selectedCount }} / {{ totalCount }}</span>
            <button type="button" class="text-btn" @click="toggleSelectAll" :disabled="!totalCount">
              {{ isAllSelected ? '取消全选' : '全部选择' }}
            </button>
            <button
              type="button"
              class="text-btn danger"
              @click="clearSelection"
              :disabled="!selectedCount"
            >
              清空
            </button>
          </div>
          <div v-if="!filteredAdmins.length" class="empty-state">未找到匹配的管理员</div>
          <div v-else class="chip-container">
            <button
              v-for="admin in filteredAdmins"
              :key="admin.id"
              type="button"
              class="chip"
              :class="{ selected: form.receiverIds.includes(admin.id) }"
              @click="toggleReceiver(admin.id)"
            >
              {{ admin.name || admin.username || admin.id }}
            </button>
          </div>
          <p v-if="selectedCount" class="selected-preview">已选择：{{ selectedNames }}</p>
        </div>

        <div class="form-item">
          <label class="form-label" for="mail-title">标题</label>
          <input
            id="mail-title"
            v-model.trim="form.title"
            type="text"
            class="input"
            placeholder="请输入邮件主题"
            :maxlength="titleLimit"
            @input="handleTitleInput"
            required
          />
          <div class="field-meta">
            <span>字符上限：{{ titleLimit }}</span>
            <span>{{ form.title.length }}/{{ titleLimit }}</span>
          </div>
        </div>

        <div class="form-item">
          <label class="form-label" for="mail-content">正文</label>
          <textarea
            id="mail-content"
            v-model.trim="form.content"
            class="textarea"
            placeholder="请输入正文内容"
            :maxlength="contentLimit"
            @input="handleContentInput"
            required
          />
          <div class="field-meta">
            <span>建议清晰描述发送目的，可附上附件说明</span>
            <span>{{ form.content.length }}/{{ contentLimit }}</span>
          </div>
        </div>

        <div class="form-item">
          <label class="form-label">附件（最多3个）</label>
          <div class="attachment-section">
            <div v-for="(attachment, index) in attachments" :key="attachment.id || index" class="attachment-item">
              <div class="attachment-info">
                <span class="file-icon">📎</span>
                <span class="file-name">{{ attachment.name || '上传中...' }}</span>
                <span v-if="attachment.size" class="file-size">{{ formatSize(attachment.size) }}</span>
              </div>
              <button
                type="button"
                class="btn-remove"
                @click="removeAttachment(index)"
                :disabled="uploading"
              >
                删除
              </button>
            </div>
            <button
              v-if="attachments.length < 3"
              type="button"
              class="btn-upload"
              :disabled="uploading"
              @click="triggerFileInput"
            >
              {{ uploading ? '上传中...' : '+ 添加附件' }}
            </button>
            <input
              ref="fileInputRef"
              type="file"
              style="display: none"
              @change="handleFileSelect"
              :disabled="uploading || attachments.length >= 3"
            />
          </div>
        </div>

        <footer class="dialog-footer">
          <button type="button" class="btn ghost" @click="handleClose">取消</button>
          <button type="submit" class="btn primary" :disabled="sending">
            {{ sending ? '发送中...' : '发送' }}
          </button>
        </footer>
      </form>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue';
import { uploadAttachment } from '@/apis/admin';

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  admins: {
    type: Array,
    default: () => [],
  },
  sending: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'submit']);

const initialForm = () => ({
  receiverIds: [],
  title: '',
  content: '',
});

const form = reactive(initialForm());
const searchKeyword = ref('');
const titleLimit = 64;
const contentLimit = 2000;
const attachments = ref([]);
const uploading = ref(false);
const fileInputRef = ref(null);

watch(
  () => props.open,
  (open) => {
    if (!open) {
      Object.assign(form, initialForm());
      searchKeyword.value = '';
      attachments.value = [];
      uploading.value = false;
    }
  },
);

const toggleReceiver = (id) => {
  const index = form.receiverIds.indexOf(id);
  if (index >= 0) {
    form.receiverIds.splice(index, 1);
  } else {
    form.receiverIds.push(id);
  }
};

const filteredAdmins = computed(() => {
  if (!Array.isArray(props.admins)) return [];
  const keyword = searchKeyword.value.trim().toLowerCase();
  if (!keyword) return props.admins;
  return props.admins.filter((admin) => {
    const label = `${admin.name || ''} ${admin.username || ''} ${admin.id ?? ''}`.toLowerCase();
    return label.includes(keyword);
  });
});

const selectedCount = computed(() => form.receiverIds.length);
const totalCount = computed(() => (Array.isArray(props.admins) ? props.admins.length : 0));
const isAllSelected = computed(
  () => totalCount.value > 0 && selectedCount.value === totalCount.value,
);

const selectedNames = computed(() => {
  if (!selectedCount.value) return '';
  const map = new Map();
  (props.admins || []).forEach((admin) => {
    map.set(admin.id, admin.name || admin.username || admin.id);
  });
  return form.receiverIds
    .map((id) => map.get(id) || id)
    .filter(Boolean)
    .join('、');
});

const toggleSelectAll = () => {
  if (!Array.isArray(props.admins) || !props.admins.length) {
    return;
  }
  if (isAllSelected.value) {
    form.receiverIds.splice(0, form.receiverIds.length);
  } else {
    form.receiverIds.splice(0, form.receiverIds.length, ...props.admins.map((admin) => admin.id));
  }
};

const clearSelection = () => {
  if (!form.receiverIds.length) return;
  form.receiverIds.splice(0, form.receiverIds.length);
};

const handleTitleInput = () => {
  if (form.title.length > titleLimit) {
    form.title = form.title.slice(0, titleLimit);
  }
};

const handleContentInput = () => {
  if (form.content.length > contentLimit) {
    form.content = form.content.slice(0, contentLimit);
  }
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

const triggerFileInput = () => {
  if (uploading.value || attachments.value.length >= 3) return;
  fileInputRef.value?.click();
};

const handleFileSelect = async (event) => {
  const file = event.target.files?.[0];
  if (!file) return;
  
  if (attachments.value.length >= 3) {
    window.alert('最多只能添加3个附件');
    return;
  }

  uploading.value = true;
  try {
    const res = await uploadAttachment(file);
    if (res?.code === 0 && res.data) {
      attachments.value.push(res.data);
      event.target.value = ''; // 清空input，允许重复选择同一文件
    } else {
      window.alert(res?.msg || '附件上传失败');
    }
  } catch (error) {
    console.error('附件上传失败', error);
    window.alert('网络错误，附件上传失败');
  } finally {
    uploading.value = false;
  }
};

const removeAttachment = (index) => {
  if (uploading.value) return;
  attachments.value.splice(index, 1);
};

const submit = () => {
  if (!form.receiverIds.length) {
    window.alert('请至少选择一位收件人');
    return;
  }
  if (!form.title.trim()) {
    window.alert('请输入邮件标题');
    return;
  }
  if (!form.content.trim()) {
    window.alert('请输入邮件内容');
    return;
  }
  if (form.title.length > titleLimit) {
    window.alert(`邮件标题不能超过 ${titleLimit} 个字符`);
    return;
  }
  if (form.content.length > contentLimit) {
    window.alert(`邮件正文不能超过 ${contentLimit} 个字符`);
    return;
  }
  if (uploading.value) {
    window.alert('请等待附件上传完成');
    return;
  }
  
  // 构建附件ID数组（最多3个）
  const attachmentIds = attachments.value
    .slice(0, 3)
    .map((att) => att.id)
    .filter((id) => id != null);
  
  emit('submit', {
    receiverIds: [...form.receiverIds],
    mail: {
      title: form.title,
      content: form.content,
      attachmentOne: attachmentIds[0] || null,
      attachmentTwo: attachmentIds[1] || null,
      attachmentThree: attachmentIds[2] || null,
    },
  });
};

const handleClose = () => {
  if (props.sending) return;
  emit('close');
};
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
  width: min(720px, 100%);
  max-height: 86vh;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 22px 42px rgba(15, 23, 42, 0.28);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.headline {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.eyebrow {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.1em;
  color: #818cf8;
  text-transform: uppercase;
}

.dialog-header h3 {
  margin: 0;
  font-size: 22px;
  color: #0f172a;
}

.dialog-content {
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow-y: auto;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-label {
  font-weight: 600;
  color: #1f2937;
}

.recipient-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search {
  flex: 1 1 220px;
  height: 36px;
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.6);
  padding: 0 12px;
  font-size: 14px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
  outline: none;
}

.count {
  font-size: 13px;
  color: #6366f1;
  font-weight: 600;
}

.text-btn {
  background: none;
  border: none;
  color: #6366f1;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  padding: 6px 8px;
  border-radius: 6px;
  transition: background 0.2s ease, color 0.2s ease;
}

.text-btn:disabled {
  color: #cbd5f5;
  cursor: not-allowed;
}

.text-btn:not(:disabled):hover {
  background: rgba(99, 102, 241, 0.12);
}

.text-btn.danger {
  color: #ef4444;
}

.text-btn.danger:not(:disabled):hover {
  background: rgba(239, 68, 68, 0.1);
}

.chip-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 160px;
  overflow-y: auto;
  padding: 4px 0;
}

.chip {
  border: none;
  border-radius: 999px;
  padding: 8px 16px;
  cursor: pointer;
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
  font-size: 13px;
  transition: background 0.2s ease, transform 0.2s ease;
}

.chip.selected {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  box-shadow: 0 12px 20px rgba(99, 102, 241, 0.25);
  transform: translateY(-1px);
}

.selected-preview {
  margin: 0;
  font-size: 13px;
  color: #4c1d95;
  background: rgba(99, 102, 241, 0.08);
  padding: 10px 12px;
  border-radius: 12px;
}

.empty-state {
  font-size: 13px;
  color: #94a3b8;
  background: rgba(148, 163, 184, 0.12);
  padding: 14px 16px;
  border-radius: 12px;
}

.input,
.textarea {
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.8);
  padding: 14px 16px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  width: 100%;
  resize: none;
}

.input:focus,
.textarea:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}

.textarea {
  min-height: 200px;
}

.field-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #94a3b8;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(226, 232, 240, 0.8);
}

.btn {
  padding: 10px 20px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn.primary {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #ffffff;
}

.btn.primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn.ghost {
  background: rgba(148, 163, 184, 0.24);
  color: #334155;
}

.btn.ghost:hover {
  background: rgba(148, 163, 184, 0.34);
}

.attachment-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attachment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(226, 232, 240, 0.4);
  border-radius: 12px;
}

.attachment-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.file-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.file-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
  color: #1f2937;
}

.file-size {
  font-size: 12px;
  color: #94a3b8;
  flex-shrink: 0;
}

.btn-remove {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.2s ease;
  flex-shrink: 0;
}

.btn-remove:hover:not(:disabled) {
  background: rgba(239, 68, 68, 0.2);
}

.btn-remove:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-upload {
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
  border: none;
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: background 0.2s ease, transform 0.15s ease;
  text-align: center;
}

.btn-upload:hover:not(:disabled) {
  background: rgba(99, 102, 241, 0.2);
  transform: translateY(-1px);
}

.btn-upload:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 640px) {
  .dialog {
    border-radius: 16px;
  }

  .dialog-header,
  .dialog-content {
    padding: 20px;
  }

  .recipient-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .chip-container {
    max-height: 140px;
  }
}
</style>
