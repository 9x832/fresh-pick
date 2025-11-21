<template>
  <section class="menu-manage">
    <header class="page-header">
      <div>
        <h2>菜单管理</h2>
        <p>维护后台功能菜单结构，支持三级菜单。</p>
      </div>
      <div>
        <button type="button" class="btn primary" @click="openCreateDialog">
          <i class="ri-add-line"></i> 新增菜单
        </button>
      </div>
    </header>

    <section class="card">
      <div class="menu-tree" v-if="!loading">
        <MenuTree
          :tree="menuTree"
          :expanded.sync="expanded"
          @edit="openEditDialog"
          @toggle-state="handleToggleState"
          @delete="handleDelete"
        />
      </div>
      <div v-else class="loading-card">菜单数据加载中...</div>
    </section>

    <MenuDialog
      v-if="showDialog"
      :loading="submitting"
      :menu="currentMenu"
      :parent-options="parentOptions"
      @close="closeDialog"
      @submit="handleSubmit"
    />
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import {
  createMenu,
  deleteMenu,
  fetchMenus,
  fetchMenuLevel,
  toggleMenuState,
  updateMenu,
} from '@/apis/admin';
import MenuTree from '@/views/admin/components/MenuTree.vue';
import MenuDialog from '@/views/admin/components/MenuDialog.vue';

const loading = ref(false);
const submitting = ref(false);
const rawMenuData = reactive({
  firstMenus: [],
  secondMenus: [],
  thirdMenus: [],
});

const expanded = reactive({
  first: [],
  second: [],
});

const showDialog = ref(false);
const currentMenu = ref(null);

const menuTree = computed(() => {
  const secondByParent = rawMenuData.secondMenus.reduce((map, menu) => {
    if (!map[menu.parentId]) map[menu.parentId] = [];
    map[menu.parentId].push(menu);
    return map;
  }, {});
  const thirdByParent = rawMenuData.thirdMenus.reduce((map, menu) => {
    if (!map[menu.parentId]) map[menu.parentId] = [];
    map[menu.parentId].push(menu);
    return map;
  }, {});
  return rawMenuData.firstMenus.map((first) => {
    const children = (secondByParent[first.id] || []).map((second) => ({
      ...second,
      level: 2,
      children: (thirdByParent[second.id] || []).map((third) => ({
        ...third,
        level: 3,
        children: [],
      })),
    }));
    return {
      ...first,
      level: 1,
      children,
    };
  });
});

const parentOptions = computed(() => {
  const options = [{ label: '顶级菜单', value: 0 }];
  rawMenuData.firstMenus.forEach((first) => {
    options.push({ label: `一级：${first.name}`, value: first.id });
    const secondMenus = rawMenuData.secondMenus.filter((menu) => menu.parentId === first.id);
    secondMenus.forEach((second) => {
      options.push({ label: `二级：${second.name}`, value: second.id });
    });
  });
  return options;
});

const loadMenus = async () => {
  loading.value = true;
  try {
    const res = await fetchMenus();
    if (res?.code === 0 && res.data) {
      rawMenuData.firstMenus = res.data.firstMenus || [];
      rawMenuData.secondMenus = res.data.secondMenus || [];
      rawMenuData.thirdMenus = res.data.thirdMenus || [];
      ensureExpandedState();
    } else {
      window.alert(res?.msg || '加载菜单列表失败');
    }
  } catch (error) {
    console.error('加载菜单失败', error);
    window.alert('网络错误，加载菜单失败');
  } finally {
    loading.value = false;
  }
};

const ensureExpandedState = () => {
  const firstIds = menuTree.value.map((menu) => menu.id);
  expanded.first = firstIds.slice(0, 2);
  expanded.second = menuTree.value
    .flatMap((menu) => menu.children?.map((child) => child.id) || [])
    .slice(0, 3);
};

const openCreateDialog = () => {
  currentMenu.value = null;
  showDialog.value = true;
};

const openEditDialog = async (menu) => {
  if (!menu?.id) return;
  let level = menu.level;
  if (!level) {
    try {
      const res = await fetchMenuLevel(menu.id);
      if (res?.code === 0) {
        level = res.data;
      }
    } catch (error) {
      console.warn('获取菜单层级失败', error);
    }
  }
  currentMenu.value = {
    ...menu,
    level,
  };
  showDialog.value = true;
};

const closeDialog = () => {
  if (submitting.value) return;
  showDialog.value = false;
};

const handleSubmit = async (payload) => {
  submitting.value = true;
  try {
    let res;
    if (payload.id) {
      res = await updateMenu(payload.id, payload);
    } else {
      res = await createMenu(payload);
    }
    if (res?.code === 0) {
      window.alert(res.msg || (payload.id ? '菜单修改成功' : '菜单创建成功'));
      showDialog.value = false;
      await loadMenus();
    } else {
      window.alert(res?.msg || '操作失败，请检查表单或稍后重试');
    }
  } catch (error) {
    console.error('提交菜单失败', error);
    window.alert('网络错误，操作失败');
  } finally {
    submitting.value = false;
  }
};

const handleToggleState = async (menu) => {
  if (!menu?.id) return;
  try {
    const res = await toggleMenuState(menu.id);
    if (res?.code === 0) {
      window.alert(res.msg || '菜单状态已切换');
      await loadMenus();
    } else {
      window.alert(res?.msg || '切换菜单状态失败');
    }
  } catch (error) {
    console.error('切换菜单状态失败', error);
    window.alert('网络错误，切换菜单状态失败');
  }
};

const handleDelete = async (menu) => {
  if (!menu?.id) return;
  if (!window.confirm(`确定要删除菜单「${menu.name}」么？`)) return;
  try {
    const res = await deleteMenu(menu.id);
    if (res?.code === 0) {
      window.alert(res.msg || '菜单删除成功');
      await loadMenus();
    } else {
      window.alert(res?.msg || '删除菜单失败');
    }
  } catch (error) {
    console.error('删除菜单失败', error);
    window.alert('网络错误，删除菜单失败');
  }
};

onMounted(() => {
  loadMenus();
});
</script>

<style scoped>
.menu-manage {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.btn {
  display: inline-flex;
  align-items: center;
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

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(99, 102, 241, 0.35);
}

.card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
  padding: 24px;
}

.loading-card {
  text-align: center;
  color: #94a3b8;
  padding: 40px 0;
}
</style>

