<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
      <RouterLink to="/admin" class="sidebar-logo">鲜采直送自营平台后台</RouterLink>
      </div>
      <div class="sidebar-scroll">
        <nav v-if="menuTree.length">
          <div v-for="firstMenu in menuTree" :key="firstMenu.id" class="menu-group">
            <button
              type="button"
              class="first-item"
              :class="{ opened: isFirstExpanded(firstMenu.id) }"
              @click="toggleFirst(firstMenu.id)"
            >
              <span class="item-icon" aria-hidden="true">
                {{ renderMenuIcon(firstMenu.icon, '▸') }}
              </span>
              <span class="item-label">{{ firstMenu.name }}</span>
              <span class="item-arrow" aria-hidden="true">▼</span>
            </button>
            <ul v-show="isFirstExpanded(firstMenu.id)" class="second-list">
              <li v-for="secondMenu in firstMenu.children" :key="secondMenu.id" class="second-node">
                <button
                  type="button"
                  class="second-item leaf"
                  :class="{ active: isActive(secondMenu) }"
                  @click="navigateToMenu(secondMenu)"
                >
                  <span class="item-icon leaf-icon" aria-hidden="true">
                    {{ renderMenuIcon(secondMenu.icon, '•') }}
                  </span>
                  <span class="item-label">{{ secondMenu.name }}</span>
                </button>
              </li>
            </ul>
          </div>
        </nav>
        <div v-else-if="loadingMenus" class="menu-loading">
          <p>后台菜单加载中...</p>
        </div>
        <div v-else class="menu-empty">
          <p>暂无可用菜单</p>
          <button type="button" @click="loadMenus">重新加载</button>
        </div>
      </div>
    </aside>
    <section class="admin-content">
      <header class="admin-header">
        <div class="header-left">
          <h1 class="header-title">{{ currentMenuTitle }}</h1>
          <p class="header-subtitle">欢迎使用鲜采直送自营平台后台管理系统</p>
        </div>
        <div class="header-right">
          <span class="admin-badge">当前用户：{{ adminName || '管理员' }}</span>
          <button type="button" class="logout-button" @click="handleLogout">
            退出登录
          </button>
        </div>
      </header>
      <main class="admin-main">
        <RouterView />
      </main>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { adminLogout, fetchAdminMenus, fetchAdminProfile } from '@/apis/admin';

const router = useRouter();
const route = useRoute();

const menuTree = ref([]);
const adminName = ref(localStorage.getItem('adminName') || '');
const expandedState = reactive({
  first: [],
});
const activeRoutePath = ref('');
const menuLookup = reactive(new Map());
const menuIdLookup = reactive(new Map());

const LEGACY_ROUTE_MAP = {
  'admin/menu/index': { path: 'menus' },
  'admin/menu/add': { path: 'menu/buttons', query: { tab: 'add' } },
  'admin/menu/edit': { path: 'menu/buttons', query: { tab: 'edit' } },
  'admin/menu/delete': { path: 'menu/buttons', query: { tab: 'delete' } },
  'admin/admin/index': { path: 'admins' },
  'admin/admin/add': { path: 'admins', query: { action: 'add' } },
  'admin/admin/edit': { path: 'admins', query: { action: 'edit' } },
  'admin/admin/delete': { path: 'admins', query: { action: 'delete' } },
  'admin/role/index': { path: 'roles' },
  'admin/role/add': { path: 'roles', query: { action: 'add' } },
  'admin/role/edit': { path: 'roles', query: { action: 'edit' } },
  'admin/role/delete': { path: 'roles', query: { action: 'delete' } },
  'admin/mail/receive': { path: 'mails', label: '邮件列表' },
  'admin/announcement/index': { path: 'announcements' },
  'admin/announcement/add': { path: 'announcements', query: { action: 'add' } },
  'admin/announcement/delete': { path: 'announcements', query: { action: 'delete' } },
  'admin/product_category/index': { path: 'product/categories' },
  'admin/product_category/add': { path: 'product/categories', query: { action: 'add' } },
  'admin/product_category/edit': { path: 'product/categories', query: { action: 'edit' } },
  'admin/product_category/delete': { path: 'product/categories', query: { action: 'delete' } },
  'admin/product/index': { path: 'products' },
  'admin/product/add': { path: 'products', query: { action: 'add' } },
  'admin/product/edit': { path: 'products', query: { action: 'edit' } },
  'admin/product/delete': { path: 'products', query: { action: 'delete' } },
  'admin/user/index': { path: 'users' },
  'admin/user/comment': { path: 'comments' },
  'admin/order/index': { path: 'orders' },
};
const loadingMenus = ref(false);

const currentMenuTitle = computed(() => {
  if (!activeRoutePath.value) {
    return '仪表盘';
  }
  const info = menuLookup.get(activeRoutePath.value);
  return info?.menu?.name || '';
});

const parseQueryString = (queryString) => {
  const query = {};
  if (!queryString) {
    return query;
  }
  const searchParams = new URLSearchParams(queryString);
  searchParams.forEach((value, key) => {
    query[key] = value;
  });
  return query;
};

const normalizeRoutePath = (path) => {
  if (!path || !path.startsWith('/admin')) {
    return '';
  }
  return path.replace(/^\/admin\/?/, '');
};

const renderMenuIcon = (icon, fallback) => {
  if (!icon) return fallback;
  const value = String(icon).trim();
  if (!value || value.startsWith('&#')) {
    return fallback;
  }
  return value;
};

const buildRouteInfoFromPath = (rawPath, incomingQuery = {}) => {
  if (!rawPath) {
    return {
      hasRoute: false,
      routePath: '',
      query: {},
      isExternal: false,
    };
  }
  const normalizedPath = rawPath.replace(/^\/+/, '');

  const legacy = LEGACY_ROUTE_MAP[normalizedPath];
  if (legacy) {
    return {
      hasRoute: true,
      isExternal: false,
      routePath: legacy.path,
      query: { ...(legacy.query || {}), ...incomingQuery },
      displayName: legacy.label,
    };
  }

  if (/^https?:\/\//i.test(normalizedPath)) {
    return {
      hasRoute: true,
      isExternal: true,
      externalUrl: normalizedPath,
      routePath: '',
      query: incomingQuery,
    };
  }

  let relativePath = normalizedPath;
  if (relativePath.startsWith('admin/')) {
    relativePath = relativePath.slice('admin/'.length);
  }
  return {
    hasRoute: true,
    isExternal: false,
    routePath: relativePath,
    query: incomingQuery,
  };
};

const parseMenuRoute = (menu) => {
  const originalUrl = (menu?.url || '').trim();
  let url = originalUrl;

  if (!url) {
    return {
      hasRoute: false,
      routePath: '',
      query: {},
      isExternal: false,
    };
  }

  if (/openAddButton/i.test(url) || /openAddBtn/i.test(url)) {
    return {
      hasRoute: true,
      isExternal: false,
      routePath: 'menu/buttons',
      query: { tab: 'add' },
    };
  }
  if (/openEditButton/i.test(url) || /openEditBtn/i.test(url)) {
    return {
      hasRoute: true,
      isExternal: false,
      routePath: 'menu/buttons',
      query: { tab: 'edit' },
    };
  }
  if (/openDelButton|openDeleteButton/i.test(url)) {
    return {
      hasRoute: true,
      isExternal: false,
      routePath: 'menu/buttons',
      query: { tab: 'delete' },
    };
  }

  if (/xadmin\.open/i.test(url)) {
    const match = url.match(/xadmin\.open\([^,]+,\s*['"]([^'"]+)['"]/i);
    if (match && match[1]) {
      url = match[1];
    }
  }

  if (/^https?:\/\//i.test(url)) {
    return {
      hasRoute: true,
      isExternal: true,
      externalUrl: url,
      routePath: '',
      query: {},
    };
  }

  if (/^javascript:/i.test(url)) {
    return {
      hasRoute: false,
      routePath: '',
      query: {},
      isExternal: false,
    };
  }

  const [pathPartRaw, queryString] = url.split('?');
  const pathPart = (pathPartRaw || '').replace(/^\/+/, '');
  if (!pathPart) {
    return {
      hasRoute: false,
      routePath: '',
      query: {},
      isExternal: false,
    };
  }

  if (/[()]/.test(pathPart)) {
    return {
      hasRoute: false,
      routePath: '',
      query: {},
      isExternal: false,
    };
  }

  const routeInfo = buildRouteInfoFromPath(pathPartRaw || pathPart, parseQueryString(queryString));
  routeInfo.originalUrl = originalUrl;
  return routeInfo;
};

const indexMenuNode = (menu, hierarchy) => {
  const info = {
    menu,
    ...hierarchy,
  };
  if (menu.routePath) {
    menuLookup.set(menu.routePath, info);
  }
  if (menu.id) {
    menuIdLookup.set(menu.id, info);
  }
  if (menu.children && menu.children.length) {
    menu.children.forEach((child) => {
      indexMenuNode(child, {
        firstId: hierarchy.firstId,
        secondId: child.parentLevel === 2 ? child.id : hierarchy.secondId,
        thirdId: child.parentLevel === 3 ? child.id : hierarchy.thirdId,
      });
    });
  }
};

const buildMenuTree = (menusPayload) => {
  const firstMenus = menusPayload.firstMenus || [];
  const secondMenus = menusPayload.secondMenus || [];
  const thirdMenus = menusPayload.thirdMenus || [];

  const secondByParent = secondMenus.reduce((acc, item) => {
    const parentId = item.parentId;
    if (!acc[parentId]) {
      acc[parentId] = [];
    }
    acc[parentId].push(item);
    return acc;
  }, {});
  const thirdByParent = thirdMenus.reduce((acc, item) => {
    const parentId = item.parentId;
    if (!acc[parentId]) {
      acc[parentId] = [];
    }
    acc[parentId].push(item);
    return acc;
  }, {});

  return firstMenus.map((first) => {
    const firstRouteInfo = parseMenuRoute(first);
    const firstNode = {
      ...first,
      ...firstRouteInfo,
      children: [],
      parentLevel: 1,
    };
    const secondChildrenRaw = (secondByParent[first.id] || []).map((second) => {
      const secondRouteInfo = parseMenuRoute(second);
      if (secondRouteInfo.hidden) {
        return null;
      }
      return {
        ...second,
        ...secondRouteInfo,
        name: secondRouteInfo.displayName || second.name,
        children: (thirdByParent[second.id] || []).map((third) => ({
          ...third,
          ...parseMenuRoute(third),
          parentLevel: 3,
          children: [],
        })),
        parentLevel: 2,
      };
    });

    const dedupedSecond = [];
    const seenKeys = new Map();
    secondChildrenRaw.forEach((item) => {
      if (!item) return;
      const key = item.routePath || item.url || item.name;
      if (seenKeys.has(key)) {
        const existingIndex = seenKeys.get(key);
        const existing = dedupedSecond[existingIndex];
        if (!existing.query && item.query) {
          existing.query = item.query;
        }
        if (!existing.name && item.name) {
          existing.name = item.name;
        }
        return;
      }
      seenKeys.set(key, dedupedSecond.length);
      dedupedSecond.push(item);
    });

    firstNode.children = dedupedSecond;
    return firstNode;
  });
};

const rebuildMenuIndex = (tree) => {
  menuLookup.clear();
  menuIdLookup.clear();
  tree.forEach((first) => {
    indexMenuNode(first, { firstId: first.id });
    first.children.forEach((second) => {
      indexMenuNode(second, { firstId: first.id, secondId: second.id });
      second.children.forEach((third) => {
        indexMenuNode(third, { firstId: first.id, secondId: second.id, thirdId: third.id });
      });
    });
  });
};

const isFirstExpanded = (id) => expandedState.first.includes(id);
const toggleFirst = (id) => {
  if (isFirstExpanded(id)) {
    expandedState.first = expandedState.first.filter((item) => item !== id);
  } else {
    expandedState.first = [...expandedState.first, id];
  }
};

const isActive = (menu) => menu.routePath && menu.routePath === activeRoutePath.value;

const ensureParentExpanded = (routePath) => {
  if (!routePath) {
    return;
  }
  const info = menuLookup.get(routePath);
  if (!info) {
    return;
  }
  if (info.firstId && !isFirstExpanded(info.firstId)) {
    expandedState.first = [...expandedState.first, info.firstId];
  }
};

const navigateToMenu = (menu) => {
  if (!menu) {
    return;
  }

  if (!menu.hasRoute) {
    const parentInfo = menuIdLookup.get(menu.parentId);
    if (parentInfo?.menu?.routePath) {
      const query = parentInfo.menu.query ? { ...parentInfo.menu.query } : {};
      if (/添加按钮/.test(menu.name || '')) {
        query.tab = 'add';
      } else if (/按钮/.test(menu.name || '') && !query.tab) {
        query.tab = 'edit';
      }
      router.push({
        path: `/admin/${parentInfo.menu.routePath}`,
        query,
      });
      return;
    }

    console.warn('[AdminMenu] Menu without route, ignore.', menu?.url || menu);
    return;
  }

  if (menu.isExternal && menu.externalUrl) {
    window.open(menu.externalUrl, '_blank');
    return;
  }

  const targetPath = menu.routePath ? `/admin/${menu.routePath}` : '/admin';
  const query = menu.query || {};
  const resolved = router.resolve({ path: targetPath, query });
  if (!resolved.matched.length) {
    const parentInfo = menuIdLookup.get(menu.parentId);
    if (parentInfo?.menu?.routePath) {
      const parentQuery = parentInfo.menu.query ? { ...parentInfo.menu.query } : {};
      if (/添加/.test(menu.name || '')) {
        parentQuery.action = 'add';
      } else if (/删除/.test(menu.name || '')) {
        parentQuery.action = 'delete';
      } else if (/修改|编辑/.test(menu.name || '')) {
        parentQuery.action = 'edit';
      } else if (/查看/.test(menu.name || '')) {
        parentQuery.action = 'view';
      }
      router.push({
        path: `/admin/${parentInfo.menu.routePath}`,
        query: parentQuery,
      });
      return;
    }
  }

  router.push({
    path: targetPath,
    query,
  });
};

const loadMenus = async () => {
  loadingMenus.value = true;
  try {
    const res = await fetchAdminMenus();
    if (res?.code === 0 && res.data) {
      const tree = buildMenuTree(res.data);
      menuTree.value = tree;
      rebuildMenuIndex(tree);
      if (tree.length && expandedState.first.length === 0) {
        expandedState.first = [tree[0].id];
      }
      ensureParentExpanded(activeRoutePath.value);
    } else if (res) {
      window.alert(res.msg || '加载后台菜单失败');
    }
  } catch (error) {
    console.error('加载后台菜单失败', error);
    window.alert('加载后台菜单失败，请稍后重试');
  } finally {
    loadingMenus.value = false;
  }
};

const loadProfile = async () => {
  try {
    const res = await fetchAdminProfile();
    if (res?.code === 0 && res.data) {
      adminName.value = res.data.name || res.data.username || '';
      if (adminName.value) {
        localStorage.setItem('adminName', adminName.value);
      }
    }
  } catch (error) {
    console.error('获取管理员信息失败', error);
  }
};

const handleLogout = async () => {
  try {
    await adminLogout();
  } catch (error) {
    console.error('后台退出登录失败', error);
  } finally {
    localStorage.removeItem('adminLoggedIn');
    localStorage.removeItem('adminName');
    router.push('/admin/login');
  }
};

watch(
  () => route.path,
  (newPath) => {
    const normalized = normalizeRoutePath(newPath);
    activeRoutePath.value = normalized;
    ensureParentExpanded(normalized);
  },
  { immediate: true },
);

onMounted(async () => {
  if (!menuTree.value.length) {
    await loadMenus();
  }
  await loadProfile();
});
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f3f6fb;
  color: #1f2933;
}

.admin-sidebar {
  width: 260px;
  background: linear-gradient(180deg, #111827 0%, #1f2937 100%);
  color: #e5e7eb;
  display: flex;
  flex-direction: column;
  box-shadow: inset -1px 0 0 rgba(255, 255, 255, 0.05);
}

.sidebar-header {
  padding: 24px 20px 16px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.sidebar-logo {
  display: inline-block;
  color: inherit;
  text-decoration: none;
  transition: opacity 0.2s ease;
}

.sidebar-logo:hover {
  opacity: 0.85;
}

.sidebar-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 8px 12px 24px;
  position: sticky;
  top: 0;
  max-height: 100vh;
}

.menu-group + .menu-group {
  margin-top: 8px;
}

.first-item,
.second-item,
.third-item {
  width: 100%;
  display: flex;
  align-items: center;
  border: none;
  background: transparent;
  color: inherit;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
  font-size: 14px;
}

.first-item {
  font-size: 15px;
  font-weight: 600;
  justify-content: space-between;
}

.first-item.opened {
  background: rgba(55, 65, 81, 0.8);
}

.second-item {
  margin-top: 6px;
  justify-content: space-between;
  background: rgba(55, 65, 81, 0.32);
}

.second-item.leaf {
  justify-content: flex-start;
}

.third-item {
  margin-top: 6px;
  justify-content: flex-start;
  padding-left: 32px;
  background: rgba(75, 85, 99, 0.25);
}

.second-item:hover,
.third-item:hover {
  background: rgba(129, 140, 248, 0.32);
  color: #e0e7ff;
}

.second-item.active,
.third-item.active {
  background: rgba(99, 102, 241, 0.55);
  color: #fff;
  transform: translateX(2px);
}

.item-icon {
  margin-right: 12px;
  min-width: 16px;
  text-align: center;
}

.leaf-icon {
  margin-right: 10px;
}

.item-arrow {
  font-size: 12px;
  transform: rotate(-90deg);
  transition: transform 0.2s ease;
}

.first-item.opened .item-arrow,
.second-item.opened .item-arrow {
  transform: rotate(0);
}

.item-dot {
  margin-right: 12px;
  font-size: 18px;
  line-height: 1;
}

.second-list,
.third-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.second-list {
  margin-top: 6px;
}

.third-list {
  margin-left: 8px;
  border-left: 1px solid rgba(255, 255, 255, 0.08);
  padding-left: 12px;
}

.menu-empty {
  padding: 40px 20px;
  text-align: center;
  color: rgba(229, 231, 235, 0.75);
}

.menu-empty button {
  margin-top: 16px;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: rgba(99, 102, 241, 0.8);
  color: #fff;
  cursor: pointer;
  transition: background 0.2s ease;
}

.menu-empty button:hover {
  background: rgba(99, 102, 241, 1);
}

.menu-loading {
  padding: 40px 20px;
  text-align: center;
  color: rgba(229, 231, 235, 0.85);
}

.admin-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.admin-header {
  background: #ffffff;
  padding: 20px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.08);
}

.header-title {
  margin: 0;
  font-size: 22px;
  color: #1f2933;
}

.header-subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
}

.admin-badge {
  background: rgba(99, 102, 241, 0.12);
  color: #4f46e5;
  padding: 6px 12px;
  border-radius: 999px;
}

.logout-button {
  border: none;
  background: linear-gradient(135deg, #ef4444 0%, #f97316 100%);
  color: #fff;
  padding: 10px 18px;
  border-radius: 999px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.logout-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px rgba(239, 68, 68, 0.35);
}

.admin-main {
  flex: 1;
  padding: 24px 28px;
  overflow-y: auto;
}

@media (max-width: 960px) {
  .admin-layout {
    flex-direction: column;
  }

  .admin-sidebar {
    width: 100%;
    max-height: 320px;
  }

  .admin-content {
    flex: none;
  }
}
</style>

