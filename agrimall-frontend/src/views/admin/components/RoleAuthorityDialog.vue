<template>
  <div v-if="open" class="dialog-backdrop" @click.self="handleClose">
    <section class="dialog">
      <header class="dialog-header">
        <div>
          <h3>角色权限配置</h3>
          <p class="hint">当前角色：{{ roleName || '未命名角色' }}</p>
        </div>
        <button type="button" class="btn ghost" @click="handleClose" :disabled="saving">
          取消
        </button>
      </header>

      <section class="dialog-content">
        <div v-if="!tree.length" class="empty">
          <p>暂无可配置的菜单，请先在菜单管理中添加后再分配。</p>
        </div>
        <div v-else class="menu-tree">
          <div v-for="firstMenu in tree" :key="firstMenu.id" class="first-block">
            <label class="first-label">
              <input
                type="checkbox"
                :checked="isChecked(firstMenu.id)"
                @change="toggleFirst(firstMenu, $event.target.checked)"
              />
              <span>{{ firstMenu.name }}</span>
            </label>
            <div v-if="firstMenu.children.length" class="second-container">
              <div v-for="secondMenu in firstMenu.children" :key="secondMenu.id" class="second-block">
                <label>
                  <input
                    type="checkbox"
                    :checked="isChecked(secondMenu.id)"
                    @change="toggleSecond(secondMenu, $event.target.checked)"
                  />
                  <span>{{ secondMenu.name }}</span>
                </label>
                <div v-if="secondMenu.children.length" class="third-list">
                  <label v-for="thirdMenu in secondMenu.children" :key="thirdMenu.id">
                    <input
                      type="checkbox"
                      :checked="isChecked(thirdMenu.id)"
                      @change="toggleThird(thirdMenu, $event.target.checked)"
                    />
                    <span>{{ thirdMenu.name }}</span>
                  </label>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <footer class="dialog-footer">
        <button type="button" class="btn ghost" @click="handleClose" :disabled="saving">
          取消
        </button>
        <button type="button" class="btn primary" @click="saveAuthority" :disabled="saving">
          {{ saving ? '保存中...' : '保存权限' }}
        </button>
      </footer>
    </section>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  roleName: {
    type: String,
    default: '',
  },
  menus: {
    type: Object,
    default: () => ({}),
  },
  selected: {
    type: Array,
    default: () => [],
  },
  saving: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'submit']);

const tree = ref([]);
const parentMap = ref(new Map());
const menuMap = ref(new Map());
const selectedIds = ref(new Set());

const rebuildTree = (menusPayload) => {
  const { firstMenus = [], secondMenus = [], thirdMenus = [] } = menusPayload || {};
  const secondByParent = secondMenus.reduce((acc, menu) => {
    acc[menu.parentId] = acc[menu.parentId] || [];
    acc[menu.parentId].push({ ...menu, children: [] });
    return acc;
  }, {});
  const thirdByParent = thirdMenus.reduce((acc, menu) => {
    acc[menu.parentId] = acc[menu.parentId] || [];
    acc[menu.parentId].push({ ...menu, children: [] });
    return acc;
  }, {});

  const newParentMap = new Map();
  const newMenuMap = new Map();

  const buildNode = (menu, parentId = null) => {
    const node = { ...menu, children: [] };
    newParentMap.set(node.id, parentId);
    newMenuMap.set(node.id, node);
    return node;
  };

  const newTree = firstMenus.map((first) => {
    const firstNode = buildNode(first, null);
    const secondChildren = (secondByParent[first.id] || []).map((second) => {
      const secondNode = buildNode(second, firstNode.id);
      const thirdChildren = (thirdByParent[second.id] || []).map((third) => buildNode(third, secondNode.id));
      secondNode.children = thirdChildren;
      return secondNode;
    });
    firstNode.children = secondChildren;
    return firstNode;
  });

  parentMap.value = newParentMap;
  menuMap.value = newMenuMap;
  tree.value = newTree;
};

const modifySelection = (callback) => {
  const next = new Set(selectedIds.value);
  callback(next);
  selectedIds.value = next;
};

const collectDescendantIds = (id) => {
  const node = menuMap.value.get(id);
  if (!node || !node.children || !node.children.length) {
    return [];
  }
  const stack = [...node.children];
  const result = [];
  while (stack.length) {
    const current = stack.pop();
    result.push(current.id);
    if (current.children && current.children.length) {
      stack.push(...current.children);
    }
  }
  return result;
};

const ensureParentsChecked = (set, id) => {
  let parentId = parentMap.value.get(id);
  while (parentId !== undefined && parentId !== null) {
    set.add(parentId);
    parentId = parentMap.value.get(parentId);
  }
};

const hasSelectedDescendant = (set, node) => {
  if (!node || !node.children || !node.children.length) {
    return false;
  }
  return node.children.some(
    (child) => set.has(child.id) || hasSelectedDescendant(set, menuMap.value.get(child.id) || child),
  );
};

const cleanupParents = (set, id) => {
  let parentId = parentMap.value.get(id);
  while (parentId !== undefined && parentId !== null) {
    const parentNode = menuMap.value.get(parentId);
    if (!parentNode) break;
    const keep = hasSelectedDescendant(set, parentNode);
    if (!keep) {
      set.delete(parentId);
      parentId = parentMap.value.get(parentId);
    } else {
      break;
    }
  }
};

const isChecked = (id) => selectedIds.value.has(id);

const toggleFirst = (node, checked) => {
  modifySelection((set) => {
    if (checked) {
      set.add(node.id);
      collectDescendantIds(node.id).forEach((descId) => set.add(descId));
    } else {
      set.delete(node.id);
      collectDescendantIds(node.id).forEach((descId) => set.delete(descId));
    }
  });
};

const toggleSecond = (node, checked) => {
  modifySelection((set) => {
    if (checked) {
      set.add(node.id);
      collectDescendantIds(node.id).forEach((descId) => set.add(descId));
      ensureParentsChecked(set, node.id);
    } else {
      set.delete(node.id);
      collectDescendantIds(node.id).forEach((descId) => set.delete(descId));
      cleanupParents(set, node.id);
    }
  });
};

const toggleThird = (node, checked) => {
  modifySelection((set) => {
    if (checked) {
      set.add(node.id);
      ensureParentsChecked(set, node.id);
    } else {
      set.delete(node.id);
      cleanupParents(set, node.id);
    }
  });
};

const handleClose = () => {
  if (props.saving) return;
  emit('close');
};

const saveAuthority = () => {
  if (props.saving) return;
  const result = new Set(selectedIds.value);
  result.forEach((id) => {
    ensureParentsChecked(result, id);
  });
  emit('submit', Array.from(result));
};

watch(
  () => props.menus,
  (menusPayload) => {
    rebuildTree(menusPayload);
  },
  { immediate: true, deep: true },
);

watch(
  () => props.selected,
  (value) => {
    selectedIds.value = new Set(value || []);
  },
  { immediate: true, deep: true },
);

watch(
  () => props.open,
  (visible) => {
    if (!visible) {
      selectedIds.value = new Set(props.selected || []);
    }
  },
);
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
  width: min(860px, 100%);
  max-height: 92vh;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 32px 60px rgba(15, 23, 42, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.9);
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
  padding: 24px;
  overflow-y: auto;
}

.empty {
  padding: 40px;
  border: 1px dashed rgba(148, 163, 184, 0.4);
  border-radius: 16px;
  text-align: center;
  color: #94a3b8;
}

.menu-tree {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.first-block {
  border: 1px solid rgba(203, 213, 225, 0.6);
  border-radius: 16px;
  padding: 16px 18px;
  background: rgba(248, 250, 252, 0.9);
}

.first-label {
  font-weight: 700;
  font-size: 16px;
  color: #0f172a;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.second-container {
  margin-top: 14px;
  padding-left: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  border-left: 2px solid rgba(148, 163, 184, 0.25);
}

.second-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-left: 16px;
  color: #1f2937;
  font-weight: 600;
}

.second-block label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.third-list {
  margin-left: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  padding-left: 12px;
}

.third-list label {
  font-weight: 500;
  color: #475569;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

input[type='checkbox'] {
  width: 16px;
  height: 16px;
  accent-color: #6366f1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 14px;
  padding: 20px 24px;
  border-top: 1px solid rgba(226, 232, 240, 0.9);
}

.btn {
  border: none;
  border-radius: 10px;
  height: 38px;
  padding: 0 18px;
  font-weight: 600;
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
  box-shadow: 0 10px 20px rgba(99, 102, 241, 0.32);
}

.btn.ghost {
  background: rgba(148, 163, 184, 0.18);
  color: #1f2937;
}

.btn.ghost:hover {
  background: rgba(148, 163, 184, 0.28);
}

@media (max-width: 720px) {
  .dialog {
    border-radius: 16px;
  }

  .third-list {
    flex-direction: column;
  }
}
</style>

