<template>
  <div v-if="open" class="dialog-backdrop" @click.self="handleClose">
    <section class="dialog">
      <header class="dialog-header">
        <div>
          <h3>{{ isEdit ? '编辑管理员' : '新增管理员' }}</h3>
          <p class="hint">
            {{ isEdit ? '更新管理员基础信息，可选修改密码。' : '默认初始密码为：123456。' }}
          </p>
        </div>
        <button type="button" class="icon-btn" @click="handleClose" :disabled="submitting || uploading">
          <i class="ri-close-line"></i>
        </button>
      </header>

      <form class="dialog-content" @submit.prevent="submit">
        <div class="form-grid">
          <div class="full-row">
            <div class="avatar-wrapper">
              <img :src="avatarPreview" alt="管理员头像" class="avatar-preview" />
              <div class="avatar-actions">
                <button
                  type="button"
                  class="btn secondary"
                  @click="triggerUpload"
                  :disabled="uploading || submitting"
                >
                  {{ uploading ? '上传中...' : '上传头像' }}
                </button>
                <button
                  v-if="!isDefaultAvatar"
                  type="button"
                  class="btn ghost"
                  @click="resetAvatar"
                  :disabled="uploading || submitting"
                >
                  恢复默认
                </button>
                <small class="hint">支持 jpg / png，自动保存后端返回路径</small>
              </div>
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                class="hidden-input"
                @change="handleFileChange"
              />
            </div>
          </div>

          <div class="form-field left">
            <label class="form-label" for="admin-name">
              管理员名称
              <span class="required">*</span>
            </label>
            <input
              id="admin-name"
              v-model.trim="form.name"
              type="text"
              maxlength="16"
              placeholder="请输入 1-16 字的管理员名称"
              :disabled="submitting || uploading"
              required
            />
          </div>

          <div class="form-field right">
            <label class="form-label" for="admin-role">
              所属角色
              <span class="required">*</span>
            </label>
            <select
              id="admin-role"
              v-model.number="form.roleId"
              :disabled="submitting || uploading"
              required
            >
              <option disabled value="">请选择角色</option>
              <option v-for="role in roles" :key="role.id" :value="role.id">
                {{ role.name }}
              </option>
            </select>
          </div>

          <div class="form-field left">
            <label class="form-label" for="admin-mobile">
              手机号
              <span class="required">*</span>
            </label>
            <input
              id="admin-mobile"
              v-model.trim="form.mobile"
              type="text"
              inputmode="numeric"
              maxlength="11"
              placeholder="请输入 11 位手机号"
              :disabled="submitting || uploading"
              required
            />
          </div>

          <div class="form-field right">
            <label class="form-label" for="admin-sex">性别</label>
            <select
              id="admin-sex"
              v-model.number="form.sex"
              :disabled="submitting || uploading"
            >
              <option :value="3">未知</option>
              <option :value="1">男</option>
              <option :value="2">女</option>
            </select>
          </div>

          <div class="form-field left">
            <label class="form-label" for="admin-address">联系地址</label>
            <input
              id="admin-address"
              v-model.trim="form.address"
              type="text"
              maxlength="128"
              placeholder="可选填写联系地址"
              :disabled="submitting || uploading"
            />
          </div>

          <div class="form-field right">
            <label class="form-label">状态</label>
            <button
              type="button"
              class="switch"
              :class="{ active: form.state === 1 }"
              @click="toggleState"
              :disabled="submitting || uploading"
            >
              {{ form.state === 1 ? '启用' : '冻结' }}
            </button>
          </div>

          <template v-if="isEdit">
            <div class="form-field left">
              <label class="form-label" for="admin-password">重置密码</label>
              <input
                id="admin-password"
                v-model.trim="form.password"
                type="password"
                maxlength="16"
                placeholder="留空则保持不变"
                :disabled="submitting || uploading"
              />
            </div>
          </template>
        </div>

        <footer class="dialog-footer">
          <button type="button" class="btn ghost" @click="handleClose" :disabled="submitting || uploading">
            取消
          </button>
          <button type="submit" class="btn primary" :disabled="submitting || uploading">
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </footer>
      </form>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue';
import { uploadAdminPhoto } from '@/apis/admin';

const DEFAULT_AVATAR = 'common/default_img.jpg';
const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  mode: {
    type: String,
    default: 'create',
  },
  admin: {
    type: Object,
    default: null,
  },
  roles: {
    type: Array,
    default: () => [],
  },
  submitting: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'submit']);

const form = reactive({
  name: '',
  roleId: '',
  mobile: '',
  sex: 3,
  address: '',
  state: 1,
  headPic: DEFAULT_AVATAR,
  password: '',
});

const fileInput = ref(null);
const uploading = ref(false);

const isEdit = computed(() => props.mode === 'edit');
const isDefaultAvatar = computed(() => !form.headPic || form.headPic === DEFAULT_AVATAR);

const avatarPreview = computed(() => {
  const path = form.headPic || DEFAULT_AVATAR;
  if (/^https?:\/\//i.test(path)) {
    return path;
  }
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
});

const resetForm = () => {
  form.name = '';
  form.roleId = '';
  form.mobile = '';
  form.sex = 3;
  form.address = '';
  form.state = 1;
  form.headPic = DEFAULT_AVATAR;
  form.password = '';
};

watch(
  () => props.open,
  (visible) => {
    if (!visible) {
      resetForm();
      return;
    }
    if (props.admin && isEdit.value) {
      form.name = props.admin.name ?? '';
      form.roleId = props.admin.roleId ?? '';
      form.mobile = props.admin.mobile ? String(props.admin.mobile) : '';
      form.sex = props.admin.sex ?? 3;
      form.address = props.admin.address ?? '';
      form.state = props.admin.state ?? 1;
      form.headPic = props.admin.headPic || DEFAULT_AVATAR;
      form.password = '';
    } else {
      resetForm();
    }
  },
  { immediate: true },
);

const handleClose = () => {
  if (props.submitting || uploading.value) return;
  emit('close');
};

const triggerUpload = () => {
  if (uploading.value || props.submitting) return;
  fileInput.value?.click();
};

const handleFileChange = async (event) => {
  const [file] = event.target.files || [];
  if (!file) return;
  uploading.value = true;
  try {
    const res = await uploadAdminPhoto(file);
    if (res?.code === 0 && res.data) {
      form.headPic = res.data;
      window.alert('头像上传成功');
    } else {
      window.alert(res?.msg || '上传头像失败');
    }
  } catch (error) {
    console.error('上传头像失败', error);
    window.alert('网络错误，上传头像失败');
  } finally {
    uploading.value = false;
    if (fileInput.value) {
      fileInput.value.value = '';
    }
  }
};

const toggleState = () => {
  form.state = form.state === 1 ? 2 : 1;
};

const resetAvatar = () => {
  if (uploading.value || props.submitting) return;
  form.headPic = DEFAULT_AVATAR;
};

const submit = () => {
  if (!form.name) {
    window.alert('管理员名称不能为空');
    return;
  }
  if (!form.roleId) {
    window.alert('请选择管理员角色');
    return;
  }
  if (!/^\d{11}$/.test(form.mobile)) {
    window.alert('请输入 11 位合法手机号');
    return;
  }
  if (isEdit.value && form.password && (form.password.length < 5 || form.password.length > 16)) {
    window.alert('密码长度需在 5-16 位之间');
    return;
  }
  const payload = {
    name: form.name,
    roleId: Number(form.roleId),
    mobile: Number(form.mobile),
    sex: Number(form.sex || 3),
    address: form.address || '',
    state: Number(form.state === 1 ? 1 : 2),
    headPic: form.headPic || DEFAULT_AVATAR,
  };
  if (isEdit.value && form.password) {
    payload.password = form.password;
  }
  emit('submit', payload);
};
</script>

<style scoped>
.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  z-index: 1300;
}

.dialog {
  width: min(700px, 100%);
  max-height: 90vh;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 28px 56px rgba(15, 23, 42, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.dialog-header h3 {
  margin: 0;
  font-size: 22px;
  color: #0f172a;
}

.hint {
  margin: 4px 0 0;
  font-size: 13px;
  color: #64748b;
}

.dialog-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 24px;
  row-gap: 16px;
  align-items: start;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field.left {
  padding-right: 8px;
}

.form-field.right {
  padding-left: 8px;
}

.full-row {
  grid-column: 1 / -1;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 18px;
  flex-wrap: wrap;
  background: rgba(241, 245, 249, 0.65);
  border-radius: 16px;
  padding: 18px;
  border: 1px dashed rgba(148, 163, 184, 0.6);
}

.avatar-preview {
  width: 86px;
  height: 86px;
  border-radius: 18px;
  object-fit: cover;
  background: #e2e8f0;
  border: 1px solid rgba(148, 163, 184, 0.35);
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.hidden-input {
  display: none;
}

.form-label {
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.required {
  color: #dc2626;
  font-size: 14px;
}

input,
select {
  width: 100%;
  height: 38px;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  padding: 0 12px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

input:focus,
select:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.18);
}

.switch {
  height: 36px;
  border-radius: 999px;
  border: none;
  padding: 0 20px;
  background: rgba(148, 163, 184, 0.25);
  color: #475569;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
  width: 140px;
  text-align: center;
}

.switch.active {
  background: rgba(14, 159, 110, 0.18);
  color: #0f766e;
}

.btn {
  border: none;
  border-radius: 9px;
  height: 36px;
  padding: 0 14px;
  font-weight: 600;
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
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn.primary {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: #fff;
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px rgba(99, 102, 241, 0.35);
}

.btn.secondary {
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
}

.btn.secondary:hover {
  background: rgba(99, 102, 241, 0.22);
}

.btn.ghost {
  background: rgba(148, 163, 184, 0.18);
  color: #1f2937;
}

.btn.ghost:hover {
  background: rgba(148, 163, 184, 0.3);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

.hidden-input {
  display: none;
}

@media (max-width: 820px) {
  .dialog {
    border-radius: 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .avatar-wrapper {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

