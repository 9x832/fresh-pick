<template>
  <ul class="menu-tree">
    <li v-for="node in tree" :key="node.id" class="menu-node">
      <div class="node-header">
        <button
          v-if="node.children?.length"
          type="button"
          class="collapse-btn"
          @click="toggle(node.id)"
        >
          <i :class="isExpanded(node.id) ? 'ri-arrow-down-s-line' : 'ri-arrow-right-s-line'"></i>
        </button>
        <span class="node-name">{{ node.name }}</span>
        <span class="node-info">URL: {{ node.url || '-' }}</span>
        <span class="node-state" :class="{ enabled: node.state === 1 }">
          {{ node.state === 1 ? '启用' : '停用' }}
        </span>
        <div class="node-actions">
          <button type="button" class="btn ghost" @click="$emit('edit', node)">编辑</button>
          <button type="button" class="btn ghost" @click="$emit('toggle-state', node)">
            {{ node.state === 1 ? '停用' : '启用' }}
          </button>
          <button type="button" class="btn danger ghost" @click="$emit('delete', node)">删除</button>
        </div>
      </div>
      <MenuTree
        v-if="node.children?.length && isExpanded(node.id)"
        :tree="node.children"
        :expanded="expanded"
        :level="level + 1"
        @edit="$emit('edit', $event)"
        @toggle-state="$emit('toggle-state', $event)"
        @delete="$emit('delete', $event)"
      />
    </li>
  </ul>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  tree: {
    type: Array,
    default: () => [],
  },
  expanded: {
    type: Object,
    required: true,
  },
  level: {
    type: Number,
    default: 1,
  },
});

const storageKey = computed(() => `admin-menu-expanded-level-${props.level}`);

const ensureList = () => {
  const key = props.level === 1 ? 'first' : 'second';
  if (!props.expanded[key]) {
    const cached = sessionStorage?.getItem?.(storageKey.value);
    props.expanded[key] = cached ? JSON.parse(cached) : [];
  }
};

ensureList();

const getList = () => props.expanded[props.level === 1 ? 'first' : 'second'] || [];

const isExpanded = (id) => getList().includes(id);

const toggle = (id) => {
  const key = props.level === 1 ? 'first' : 'second';
  const collection = props.expanded[key];
  const index = collection.indexOf(id);
  if (index >= 0) {
    collection.splice(index, 1);
  } else {
    collection.push(id);
  }
  sessionStorage?.setItem?.(storageKey.value, JSON.stringify(collection));
};
</script>

<style scoped>
.menu-tree {
  list-style: none;
  margin: 0;
  padding: 0;
}

.menu-node + .menu-node {
  margin-top: 12px;
}

.node-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  background: rgba(226, 232, 240, 0.4);
}

.node-name {
  font-weight: 600;
  color: #1f2937;
}

.node-info {
  color: #64748b;
  font-size: 13px;
}

.node-state {
  margin-left: auto;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(209, 213, 219, 0.7);
  color: #4b5563;
}

.node-state.enabled {
  background: rgba(16, 185, 129, 0.18);
  color: #047857;
}

.node-actions {
  display: flex;
  gap: 8px;
}

.btn {
  border: none;
  border-radius: 8px;
  padding: 6px 10px;
  background: rgba(79, 70, 229, 0.1);
  color: #312e81;
  cursor: pointer;
  font-size: 13px;
  transition: background 0.2s ease;
}

.btn.ghost {
  background: rgba(79, 70, 229, 0.15);
}

.btn.ghost:hover {
  background: rgba(79, 70, 229, 0.25);
}

.btn.danger {
  background: rgba(248, 113, 113, 0.15);
  color: #b91c1c;
}

.btn.danger:hover {
  background: rgba(248, 113, 113, 0.25);
}

.collapse-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 20px;
  display: inline-flex;
  align-items: center;
  color: #4b5563;
}

.collapse-btn:hover {
  color: #1f2937;
}

.menu-tree > .menu-node > .menu-tree {
  margin-left: 20px;
  margin-top: 8px;
  border-left: 2px solid rgba(148, 163, 184, 0.26);
  padding-left: 16px;
}
</style>

