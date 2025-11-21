<template>
  <header class="home-header">
    <div class="header-inner">
      <RouterLink to="/home" class="brand">
        <img :src="logoImg" alt="鲜采直送自营平台" />
        <div class="brand-text">
          <span class="brand-name">鲜采直送自营平台</span>
          <span class="brand-tag">品质自营 · 鲜到家</span>
        </div>
      </RouterLink>

      <nav class="main-nav">
        <button
          v-for="item in navMenus"
          :key="item.key"
          type="button"
          class="nav-btn"
          :class="{ active: activeMenu === item.key }"
          @click="navigate(item.key)"
        >
          {{ item.label }}
        </button>
      </nav>

      <div class="header-actions">
        <div class="user-info">
          <span v-if="isLogin">Hi，{{ username }}！</span>
          <div v-else class="auth-links">
            <RouterLink to="/login">登录</RouterLink>
            <span class="divider">|</span>
            <RouterLink to="/register">注册</RouterLink>
          </div>
        </div>
        <RouterLink to="/home/cart" class="cart-link">
          <span>购物车</span>
          <span class="cart-count">{{ cartTotalDisplay }}</span>
        </RouterLink>
        <button v-if="isLogin" type="button" class="logout-btn" @click="requestLogout">
          退出
        </button>
        <RouterLink to="/admin/login" class="admin-link" title="进入后台管理">
          后台入口
        </RouterLink>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchHomeProductCategories } from '@/apis/home';

const props = defineProps({
  username: {
    type: String,
    default: '',
  },
  cartTotal: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(['logout']);

const router = useRouter();
const route = useRoute();

const logoImg = new URL('@/assets/home/images/logo.png', import.meta.url).href;

const isLogin = computed(() => !!props.username);
const cartTotalDisplay = computed(() => props.cartTotal ?? 0);

const createCategoryKey = (id) => `category-${id}`;
const LEGACY_CATEGORY_IDS = {
  fruit: 1,
  vegetable: 2,
};

const categories = ref([]);

const categoryLookup = computed(() => {
  const map = new Map();
  categories.value.forEach((item) => {
    const rawId = item?.id ?? item?.categoryId;
    if (rawId == null) return;
    const id = Number(rawId);
    map.set(createCategoryKey(id), {
      id,
      name: item?.categoryName ?? item?.name ?? `分类${id}`,
    });
  });
  return map;
});

/**
 * 导航栏显示的最大分类数量。
 * 可根据实际需求调整（建议 6-8 个）。
 */
const MAX_DISPLAYED_CATEGORIES = 6;

const displayedCategories = computed(() => {
  if (!categories.value.length) {
    return [
      { key: 'fruit', label: '水果专区' },
      { key: 'vegetable', label: '蔬菜专区' },
    ];
  }
  // 显示前 N 个分类（根据 MAX_DISPLAYED_CATEGORIES 配置）
  return categories.value.slice(0, MAX_DISPLAYED_CATEGORIES).map((item) => {
    const rawId = item?.id ?? item?.categoryId;
    const id = Number(rawId);
    return {
      key: createCategoryKey(id),
      label: item?.categoryName ?? item?.name ?? `分类${id}`,
    };
  });
});

const navMenus = computed(() => [
  { key: 'home', label: '首页' },
  ...displayedCategories.value,
  { key: 'center', label: '用户中心' },
]);

const normalizeCategoryKey = (key) => {
  if (!key) return null;
  if (categoryLookup.value.has(key)) return key;
  const legacyId = LEGACY_CATEGORY_IDS[key];
  if (legacyId != null) {
    const candidate = createCategoryKey(legacyId);
    if (categoryLookup.value.has(candidate)) {
      return candidate;
    }
  }
  return key;
};

const isUserCenterPath = (path) =>
  path.startsWith('/home/user') ||
  path.includes('/home/collect') ||
  path.includes('/home/address') ||
  path.includes('/home/cart');

const activeMenu = computed(() => {
  const path = route.path;
  if (path === '/home' || path === '/home/') {
    return 'home';
  }
  if (path.startsWith('/home/product/')) {
    const categoryParam = route.params?.category;
    return normalizeCategoryKey(categoryParam);
  }
  if (isUserCenterPath(path)) {
    return 'center';
  }
  return '';
});

const ensureNavigateKey = (key) => {
  if (categoryLookup.value.has(key)) {
    return key;
  }
  const legacyId = LEGACY_CATEGORY_IDS[key];
  if (legacyId != null) {
    const candidate = createCategoryKey(legacyId);
    if (categoryLookup.value.has(candidate)) {
      return candidate;
    }
  }
  return key;
};

/**
 * 加载商品分类列表。
 * 从后端获取最新的分类数据并更新本地状态。
 */
const loadCategories = async () => {
  try {
    const res = await fetchHomeProductCategories();
    if (res?.code === 0 && Array.isArray(res.data)) {
      categories.value = res.data;
    } else {
      categories.value = [];
    }
  } catch (error) {
    console.error('加载商品分类失败', error);
    categories.value = [];
  }
};

/**
 * 分类自动刷新间隔（毫秒）。
 * 设置为 0 表示禁用自动刷新。
 * 建议值：30000（30秒）或 60000（1分钟）。
 */
const CATEGORY_REFRESH_INTERVAL = 30000; // 30秒

let categoryRefreshTimer = null;

/**
 * 启动分类自动刷新定时器。
 */
const startCategoryRefresh = () => {
  if (CATEGORY_REFRESH_INTERVAL <= 0) return;
  stopCategoryRefresh();
  categoryRefreshTimer = window.setInterval(() => {
    loadCategories();
  }, CATEGORY_REFRESH_INTERVAL);
};

/**
 * 停止分类自动刷新定时器。
 */
const stopCategoryRefresh = () => {
  if (categoryRefreshTimer) {
    window.clearInterval(categoryRefreshTimer);
    categoryRefreshTimer = null;
  }
};

onMounted(() => {
  // 初始加载分类
  loadCategories();
  // 启动自动刷新
  startCategoryRefresh();
});

onBeforeUnmount(() => {
  // 组件卸载时清理定时器
  stopCategoryRefresh();
});

// 监听路由变化，在路由切换时刷新分类列表（确保获取最新数据）
watch(
  () => route.path,
  (newPath, oldPath) => {
    // 只在路由真正变化时刷新（避免重复刷新）
    if (newPath !== oldPath && newPath.startsWith('/home')) {
      loadCategories();
    }
  },
  { immediate: false },
);

const navigate = (menu) => {
  switch (menu) {
    case 'home':
      router.push('/home');
      break;
    case 'center':
      router.push('/home/user/info');
      break;
    default:
      router.push(`/home/product/${ensureNavigateKey(menu)}`);
      break;
  }
};

const requestLogout = () => {
  emit('logout');
};
</script>

<style scoped>
.home-header {
  position: sticky;
  top: 0;
  z-index: 20;
  backdrop-filter: blur(24px);
  background: rgba(255, 255, 255, 0.86);
  border-bottom: 1px solid rgba(148, 163, 184, 0.24);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
  min-height: var(--header-height);
  display: flex;
  align-items: center;
  margin-bottom: 0;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 16px clamp(16px, 4vw, 32px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 16px;
  color: #0f172a;
}

.brand img {
  width: 52px;
  height: 52px;
  object-fit: contain;
  border-radius: 14px;
  box-shadow: 0 10px 20px rgba(76, 187, 108, 0.2);
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.brand-tag {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.6);
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  justify-content: center;
}

.nav-btn {
  padding: 10px 18px;
  border-radius: 999px;
  border: none;
  background: transparent;
  font-weight: 600;
  font-size: 14px;
  color: rgba(15, 23, 42, 0.68);
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-btn:hover {
  color: var(--home-primary-dark, #2f8f4a);
  background: rgba(76, 187, 108, 0.12);
}

.nav-btn.active {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.92), rgba(99, 209, 130, 0.9));
  color: #ffffff;
  box-shadow: 0 12px 20px rgba(76, 187, 108, 0.25);
}

.header-actions {
  display: inline-flex;
  align-items: center;
  gap: 14px;
}

.user-info {
  font-size: 14px;
  color: rgba(15, 23, 42, 0.7);
}

.auth-links {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: rgba(15, 23, 42, 0.7);
}

.auth-links a:hover {
  color: var(--home-primary, #4cbb6c);
}

.divider {
  color: rgba(156, 163, 175, 0.7);
}

.cart-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(76, 187, 108, 0.14);
  color: var(--home-primary-dark, #2f8f4a);
  font-weight: 600;
}

.cart-link:hover {
  background: rgba(76, 187, 108, 0.2);
}

.cart-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  padding: 2px 6px;
  border-radius: 999px;
  background: #ffffff;
  color: var(--home-primary-dark, #2f8f4a);
  font-size: 13px;
}

.logout-btn,
.admin-link {
  border: none;
  background: rgba(15, 23, 42, 0.06);
  color: rgba(15, 23, 42, 0.7);
  padding: 8px 14px;
  border-radius: 999px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  color: #b91c1c;
}

.admin-link {
  border: 1px solid rgba(15, 23, 42, 0.12);
  text-decoration: none;
}

.admin-link:hover {
  background: rgba(15, 23, 42, 0.12);
  color: rgba(15, 23, 42, 0.9);
}

@media (max-width: 1024px) {
  .header-inner {
    flex-direction: column;
    gap: 16px;
  }

  .main-nav {
    flex-wrap: wrap;
    justify-content: center;
  }

  .header-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media (max-width: 640px) {
  .brand {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-inner {
    align-items: stretch;
  }
}
</style>

