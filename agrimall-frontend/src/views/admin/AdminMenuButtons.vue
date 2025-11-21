<template>
  <section class="menu-buttons">
    <header class="page-header">
      <div>
        <h2>按钮管理</h2>
        <p>针对二级菜单维护三级按钮（操作权限），支持新增、编辑、删除。</p>
      </div>
      <nav class="tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          type="button"
          class="tab-btn"
          :class="{ active: activeTab === tab.key }"
          @click="switchTab(tab.key)"
        >
          {{ tab.label }}
        </button>
      </nav>
    </header>

    <section v-if="loading" class="card loading-card">
      按钮数据加载中...
    </section>
    <template v-else>
      <section class="card" v-if="activeTab === 'add'">
        <h3>新增按钮</h3>
        <form class="form-grid" @submit.prevent="handleAdd">
          <label>
            所属二级菜单
            <select v-model="addForm.parentId" required>
              <option value="" disabled>请选择二级菜单</option>
              <option v-for="option in secondMenuOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </label>
          <label>
            按钮名称
            <input v-model.trim="addForm.name" type="text" maxlength="32" placeholder="例如：新增按钮" required />
          </label>
          <label>
            跳转路径/标识
            <input
              v-model.trim="addForm.url"
              type="text"
              maxlength="128"
              placeholder="请输入按钮关联路径，可留空"
            />
          </label>
          <label>
            排序值
            <input v-model.number="addForm.sort" type="number" min="0" max="1024" />
          </label>
          <label>
            图标类名
            <input v-model.trim="addForm.icon" type="text" maxlength="64" placeholder="例如：ri-function-line" />
          </label>
          <label>
            是否启用
            <select v-model.number="addForm.state">
              <option :value="1">启用</option>
              <option :value="2">停用</option>
            </select>
          </label>
          <footer class="form-actions">
            <button type="submit" class="btn primary" :disabled="submitting">
              {{ submitting ? '提交中...' : '提交' }}
            </button>
            <button type="button" class="btn ghost" @click="resetAdd" :disabled="submitting">重置</button>
          </footer>
        </form>
      </section>

      <section class="card" v-else-if="activeTab === 'edit'">
        <h3>编辑按钮</h3>
        <div class="form-grid">
          <label>
            选择按钮
            <select v-model.number="editForm.id" @change="populateEdit" required>
              <option value="" disabled>请选择需要编辑的按钮</option>
              <option v-for="button in buttonList" :key="button.id" :value="button.id">
                {{ button.displayName }}
              </option>
            </select>
          </label>
        </div>
        <form class="form-grid" @submit.prevent="handleEdit" v-if="editForm.id">
          <label>
            按钮名称
            <input v-model.trim="editForm.name" type="text" maxlength="32" required />
          </label>
          <label>
            跳转路径/标识
            <input v-model.trim="editForm.url" type="text" maxlength="128" />
          </label>
          <label>
            排序值
            <input v-model.number="editForm.sort" type="number" min="0" max="1024" />
          </label>
          <label>
            图标类名
            <input v-model.trim="editForm.icon" type="text" maxlength="64" />
          </label>
          <label>
            是否启用
            <select v-model.number="editForm.state">
              <option :value="1">启用</option>
              <option :value="2">停用</option>
            </select>
          </label>
          <footer class="form-actions">
            <button type="submit" class="btn primary" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存修改' }}
            </button>
            <button type="button" class="btn ghost" @click="resetEdit" :disabled="submitting">取消选择</button>
          </footer>
        </form>
        <p v-else class="empty-tip">请选择一个按钮后再进行编辑。</p>
      </section>

      <section class="card" v-else>
        <h3>删除按钮</h3>
        <form class="form-grid" @submit.prevent="handleDelete">
          <label>
            选择按钮
            <select v-model.number="deleteId" required>
              <option value="" disabled>请选择需要删除的按钮</option>
              <option v-for="button in buttonList" :key="button.id" :value="button.id">
                {{ button.displayName }}
              </option>
            </select>
          </label>
          <footer class="form-actions">
            <button type="submit" class="btn danger" :disabled="submitting">
              {{ submitting ? '删除中...' : '确认删除' }}
            </button>
            <button type="button" class="btn ghost" @click="deleteId = null" :disabled="submitting">重置</button>
          </footer>
        </form>
        <p class="empty-tip" v-if="!buttonList.length">暂无可删除的按钮。</p>
      </section>

      <section class="card button-table" v-if="buttonList.length">
        <header class="card-header">
          <h3>按钮列表</h3>
          <button type="button" class="btn ghost" @click="loadMenus">刷新</button>
        </header>
        <table>
          <thead>
            <tr>
              <th style="width: 80px;">ID</th>
              <th>按钮名称</th>
              <th>所属菜单</th>
              <th>路径/标识</th>
              <th style="width: 80px;">排序</th>
              <th style="width: 80px;">状态</th>
              <th style="width: 160px;">更新时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="button in buttonList" :key="button.id">
              <td>{{ button.id }}</td>
              <td>{{ button.name }}</td>
              <td>{{ button.parentNames }}</td>
              <td>{{ button.url || '-' }}</td>
              <td>{{ button.sort }}</td>
              <td>
                <span :class="['state-tag', button.state === 1 ? 'state-on' : 'state-off']">
                  {{ button.state === 1 ? '启用' : '停用' }}
                </span>
              </td>
              <td>{{ button.updateTime || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </section>
    </template>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { createMenu, deleteMenu, fetchMenus, updateMenu } from '@/apis/admin';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const submitting = ref(false);
const menuData = reactive({
  firstMenus: [],
  secondMenus: [],
  thirdMenus: [],
});

const tabs = [
  { key: 'add', label: '新增按钮' },
  { key: 'edit', label: '编辑按钮' },
  { key: 'delete', label: '删除按钮' },
];

const activeTab = ref('add');

const addForm = reactive({
  parentId: '',
  name: '',
  url: '',
  sort: 0,
  icon: 'ri-function-line',
  state: 1,
});

const editForm = reactive({
  id: null,
  name: '',
  url: '',
  sort: 0,
  icon: '',
  state: 1,
});

const deleteId = ref(null);

const secondMenuOptions = computed(() =>
  menuData.secondMenus.map((menu) => ({
    value: menu.id,
    label: `${resolveParentName(menu.parentId)} / ${menu.name}`,
  })),
);

const buttonList = computed(() =>
  menuData.thirdMenus.map((menu) => ({
    ...menu,
    parentNames: `${resolveParentName(menu.parentId, true)} / ${resolveMenuName(menu.parentId)}`,
    displayName: `${resolveParentName(menu.parentId, true)} / ${resolveMenuName(menu.parentId)} / ${menu.name}`,
  })),
);

function resolveMenuName(id) {
  const secondMatch = menuData.secondMenus.find((menu) => menu.id === id);
  if (secondMatch) return secondMatch.name;
  return menuData.firstMenus.find((menu) => menu.id === id)?.name || '-';
}

function resolveParentName(id, returnSecond = false) {
  const secondMatch = menuData.secondMenus.find((menu) => menu.id === id);
  if (secondMatch) {
    const first = menuData.firstMenus.find((menu) => menu.id === secondMatch.parentId);
    return returnSecond ? (first ? first.name : '-') : `${first ? first.name : '-'} / ${secondMatch.name}`;
  }
  const first = menuData.firstMenus.find((menu) => menu.id === id);
  return first ? first.name : '-';
}

const loadMenus = async () => {
  loading.value = true;
  try {
    const res = await fetchMenus();
    if (res?.code === 0 && res.data) {
      menuData.firstMenus = res.data.firstMenus || [];
      menuData.secondMenus = res.data.secondMenus || [];
      menuData.thirdMenus = res.data.thirdMenus || [];
    } else {
      window.alert(res?.msg || '加载菜单数据失败');
    }
  } catch (error) {
    console.error('加载菜单数据失败', error);
    window.alert('网络错误，加载菜单数据失败');
  } finally {
    loading.value = false;
  }
};

const resetAdd = () => {
  Object.assign(addForm, {
    parentId: '',
    name: '',
    url: '',
    sort: 0,
    icon: 'ri-function-line',
    state: 1,
  });
};

const handleAdd = async () => {
  if (!addForm.parentId) {
    window.alert('请选择按钮所属的二级菜单');
    return;
  }
  if (!addForm.name) {
    window.alert('按钮名称不能为空');
    return;
  }
  submitting.value = true;
  try {
    const payload = {
      parentId: Number(addForm.parentId),
      name: addForm.name,
      url: addForm.url,
      sort: Number(addForm.sort) || 0,
      icon: addForm.icon || 'ri-function-line',
      state: Number(addForm.state) || 1,
    };
    const res = await createMenu(payload);
    if (res?.code === 0) {
      window.alert(res.msg || '按钮创建成功');
      resetAdd();
      await loadMenus();
    } else {
      window.alert(res?.msg || '按钮创建失败');
    }
  } catch (error) {
    console.error('创建按钮失败', error);
    window.alert('网络错误，创建按钮失败');
  } finally {
    submitting.value = false;
  }
};

const populateEdit = () => {
  const target = buttonList.value.find((item) => item.id === editForm.id);
  if (!target) return;
  Object.assign(editForm, {
    name: target.name,
    url: target.url || '',
    sort: target.sort || 0,
    icon: target.icon || '',
    state: target.state || 1,
  });
};

const resetEdit = () => {
  Object.assign(editForm, {
    id: null,
    name: '',
    url: '',
    sort: 0,
    icon: '',
    state: 1,
  });
};

const handleEdit = async () => {
  if (!editForm.id) return;
  if (!editForm.name) {
    window.alert('按钮名称不能为空');
    return;
  }
  submitting.value = true;
  try {
    const res = await updateMenu(editForm.id, {
      name: editForm.name,
      url: editForm.url,
      sort: Number(editForm.sort) || 0,
      icon: editForm.icon,
      state: Number(editForm.state) || 1,
    });
    if (res?.code === 0) {
      window.alert(res.msg || '按钮修改成功');
      await loadMenus();
      resetEdit();
    } else {
      window.alert(res?.msg || '按钮修改失败');
    }
  } catch (error) {
    console.error('按钮修改失败', error);
    window.alert('网络错误，按钮修改失败');
  } finally {
    submitting.value = false;
  }
};

const handleDelete = async () => {
  if (!deleteId.value) return;
  const target = buttonList.value.find((item) => item.id === deleteId.value);
  if (!target) {
    window.alert('请选择有效的按钮');
    return;
  }
  if (!window.confirm(`确定删除按钮「${target.displayName}」吗？`)) return;
  submitting.value = true;
  try {
    const res = await deleteMenu(deleteId.value);
    if (res?.code === 0) {
      window.alert(res.msg || '按钮删除成功');
      deleteId.value = null;
      await loadMenus();
    } else {
      window.alert(res?.msg || '按钮删除失败');
    }
  } catch (error) {
    console.error('按钮删除失败', error);
    window.alert('网络错误，按钮删除失败');
  } finally {
    submitting.value = false;
  }
};

const switchTab = (tab) => {
  activeTab.value = tab;
  router.replace({
    path: '/admin/menu/buttons',
    query: { tab },
  });
};

watch(
  () => route.query.tab,
  (tab) => {
    if (typeof tab === 'string' && tabs.some((item) => item.key === tab)) {
      activeTab.value = tab;
    }
  },
  { immediate: true },
);

onMounted(async () => {
  await loadMenus();
  const initialTab = route.query.tab;
  if (typeof initialTab === 'string' && tabs.some((item) => item.key === initialTab)) {
    activeTab.value = initialTab;
  }
});
</script>

<style scoped>
.menu-buttons {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 26px;
  color: #0f172a;
}

.page-header p {
  margin: 6px 0 0;
  color: #64748b;
}

.tabs {
  display: inline-flex;
  gap: 12px;
  background: rgba(203, 213, 225, 0.4);
  padding: 6px;
  border-radius: 999px;
}

.tab-btn {
  border: none;
  padding: 10px 18px;
  border-radius: 999px;
  font-weight: 600;
  background: transparent;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
  color: #475569;
}

.tab-btn.active {
  background: linear-gradient(135deg, #6366f1, #4338ca);
  color: #fff;
}

.card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.12);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.loading-card {
  text-align: center;
  color: #94a3b8;
}

.form-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

label {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-weight: 600;
  color: #1f2937;
}

input,
select {
  height: 42px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

input:focus,
select:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.18);
}

.form-actions {
  display: flex;
  gap: 12px;
  grid-column: 1 / -1;
}

.btn {
  border: none;
  border-radius: 10px;
  padding: 0 16px;
  height: 40px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn.primary {
  background: linear-gradient(135deg, #6366f1, #4338ca);
  color: #fff;
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 20px rgba(67, 56, 202, 0.3);
}

.btn.ghost {
  background: rgba(148, 163, 184, 0.18);
  color: #1f2937;
}

.btn.ghost:hover {
  background: rgba(148, 163, 184, 0.26);
}

.btn.danger {
  background: rgba(248, 113, 113, 0.18);
  color: #be123c;
}

.btn.danger:hover {
  background: rgba(248, 113, 113, 0.28);
}

.empty-tip {
  text-align: center;
  color: #94a3b8;
}

.button-table table {
  width: 100%;
  border-collapse: collapse;
}

.button-table th,
.button-table td {
  padding: 12px 16px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  text-align: left;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.state-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.state-on {
  background: rgba(16, 185, 129, 0.18);
  color: #047857;
}

.state-off {
  background: rgba(148, 163, 184, 0.24);
  color: #475569;
}

@media (max-width: 820px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .tabs {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>

