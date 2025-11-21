<template>
  <div class="dialog-backdrop">
    <div class="dialog">
      <header>
        <h3>{{ menu?.id ? '编辑菜单' : '新增菜单' }}</h3>
      </header>
      <section class="dialog-body">
        <label class="form-label" for="menu-name">菜单名称</label>
        <input
          id="menu-name"
          v-model.trim="form.name"
          type="text"
          maxlength="32"
          placeholder="请输入菜单名称（1-32 字）"
        />

        <label class="form-label" for="menu-url">路由地址</label>
        <input
          id="menu-url"
          v-model.trim="form.url"
          type="text"
          maxlength="256"
          placeholder="请输入菜单访问路径，例如 /admin/announcements"
        />

        <label class="form-label" for="menu-sort">排序值</label>
        <input
          id="menu-sort"
          v-model.number="form.sort"
          type="number"
          min="0"
          max="1024"
          placeholder="排序值 0-1024，数值越大越靠前"
        />

        <label class="form-label" for="menu-icon">图标</label>
        <input
          id="menu-icon"
          v-model.trim="form.icon"
          type="text"
          maxlength="64"
          placeholder="请输入图标类名，示例：ri-dashboard-line"
        />

        <label class="form-label" for="menu-parent">上级菜单</label>
        <select id="menu-parent" v-model.number="form.parentId">
          <option v-for="item in parentOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </option>
        </select>

        <label class="form-label" for="menu-state">状态</label>
        <select id="menu-state" v-model.number="form.state">
          <option :value="1">启用</option>
          <option :value="2">停用</option>
        </select>
      </section>
      <footer class="dialog-footer">
        <button type="button" class="btn secondary" @click="$emit('close')" :disabled="loading">
          取消
        </button>
        <button type="button" class="btn primary" @click="submit" :disabled="loading">
          {{ loading ? '保存中...' : '保存' }}
        </button>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue';

const props = defineProps({
  menu: {
    type: Object,
    default: null,
  },
  parentOptions: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'submit']);

const form = reactive({
  id: null,
  name: '',
  url: '',
  sort: 0,
  icon: '',
  parentId: 0,
  state: 1,
});

watch(
  () => props.menu,
  (menu) => {
    if (menu) {
      form.id = menu.id ?? null;
      form.name = menu.name ?? '';
      form.url = menu.url ?? '';
      form.sort = menu.sort ?? 0;
      form.icon = menu.icon ?? '';
      form.parentId = menu.parentId ?? 0;
      form.state = menu.state ?? 1;
    } else {
      form.id = null;
      form.name = '';
      form.url = '';
      form.sort = 0;
      form.icon = '';
      form.parentId = 0;
      form.state = 1;
    }
  },
  { immediate: true },
);

const submit = () => {
  if (!form.name) {
    window.alert('菜单名称不能为空');
    return;
  }
  if (form.parentId === form.id) {
    window.alert('菜单不能选择自己作为上级');
    return;
  }
  emit('submit', { ...form });
};
</script>

<style scoped>
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
  width: min(520px, 100%);
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
}

.form-label {
  font-weight: 600;
  color: #475569;
}

input,
select {
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
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn {
  border: none;
  border-radius: 10px;
  padding: 0 16px;
  height: 38px;
  cursor: pointer;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn.primary {
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
  color: #fff;
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(14, 165, 233, 0.3);
}

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
}

.btn.secondary:hover {
  background: #cbd5f5;
}
</style>

