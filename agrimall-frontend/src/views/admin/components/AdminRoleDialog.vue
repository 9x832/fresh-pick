<template>
  <div v-if="open" class="dialog-backdrop" @click.self="handleClose">
    <section class="dialog">
      <header class="dialog-header">
        <h3>{{ isEdit ? '编辑角色' : '新增角色' }}</h3>
        <button type="button" class="icon-btn" @click="handleClose" :disabled="submitting">
          <i class="ri-close-line"></i>
        </button>
      </header>

      <form class="dialog-content" @submit.prevent="submit">
        <label class="form-label" for="role-name">
          角色名称
          <span class="required">*</span>
        </label>
        <input
          id="role-name"
          v-model.trim="form.name"
          type="text"
          maxlength="32"
          placeholder="请输入 1-32 字的角色名称"
          :disabled="submitting"
          required
        />

        <label class="form-label" for="role-description">角色描述</label>
        <textarea
          id="role-description"
          v-model.trim="form.description"
          rows="3"
          maxlength="128"
          placeholder="可选填写角色描述"
          :disabled="submitting"
        ></textarea>

        <footer class="dialog-footer">
          <button type="button" class="btn ghost" @click="handleClose" :disabled="submitting">
            取消
          </button>
          <button type="submit" class="btn primary" :disabled="submitting">
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </footer>
      </form>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, watch } from 'vue';

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  role: {
    type: Object,
    default: null,
  },
  mode: {
    type: String,
    default: 'create',
  },
  submitting: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'submit']);

const form = reactive({
  name: '',
  description: '',
});

const isEdit = computed(() => props.mode === 'edit');

watch(
  () => props.open,
  (visible) => {
    if (!visible) {
      form.name = '';
      form.description = '';
      return;
    }
    if (props.role && isEdit.value) {
      form.name = props.role.name ?? '';
      form.description = props.role.description ?? '';
    } else {
      form.name = '';
      form.description = '';
    }
  },
  { immediate: true },
);

const handleClose = () => {
  if (props.submitting) return;
  emit('close');
};

const submit = () => {
  if (!form.name) {
    window.alert('角色名称不能为空');
    return;
  }
  emit('submit', {
    name: form.name,
    description: form.description,
  });
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
  width: min(500px, 100%);
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.25);
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
  gap: 18px;
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
textarea {
  width: 90%;
  border-radius: 12px;
  border: 1px solid #cbd5f5;
  padding: 0 14px;
  height: 40px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

textarea {
  height: auto;
  min-height: 96px;
  padding: 12px 14px;
  resize: vertical;
}

input:focus,
textarea:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.18);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 0 24px 20px;
}

.btn {
  border: none;
  border-radius: 10px;
  height: 38px;
  padding: 0 16px;
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
  box-shadow: 0 10px 20px rgba(99, 102, 241, 0.3);
}

.btn.ghost {
  background: rgba(148, 163, 184, 0.18);
  color: #1f2937;
}

.btn.ghost:hover {
  background: rgba(148, 163, 184, 0.28);
}
</style>

