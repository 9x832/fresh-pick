<template>
  <div class="product-page">
    <section class="page-hero">
      <div class="hero-copy">
        <span class="eyebrow">{{ currentCategory.heroTag }}</span>
        <h1>{{ currentCategory.heroTitle }}</h1>
        <p class="hero-note">{{ introduction.title }}</p>
        <p class="hero-desc">
          {{ introduction.description }}
        </p>
        <div class="hero-actions">
          <div class="search-bar">
            <input
              v-model.trim="searchContent"
              type="text"
              :placeholder="placeholderText"
              @keyup.enter="handleSearch"
            />
            <button type="button" class="btn primary" @click="handleSearch">
              搜索
            </button>
          </div>
          <div class="hero-tags">
            <span v-for="tag in currentCategory.tags" :key="tag">{{ tag }}</span>
          </div>
        </div>
      </div>

      <div class="hero-visual">
        <img :src="heroImageSrc" :alt="currentCategory.heroTitle" />
        <div class="visual-badge">
          <h3>{{ currentCategory.badge.title }}</h3>
          <p>{{ currentCategory.badge.desc }}</p>
        </div>
      </div>
    </section>

    <section class="product-section" ref="productSectionRef">
      <header class="section-header">
        <div>
          <span class="section-eyebrow">精选商品</span>
          <h2>{{ currentCategory.name }}系列</h2>
        </div>
        <p class="section-meta">
          共 <strong>{{ totalCount }}</strong> 件商品 · 当前第 {{ pageNum }} / {{ pages }} 页
        </p>
      </header>

      <div v-if="loading" class="loading-state">
        <span class="spinner"></span>
        正在加载商品，请稍候…
      </div>

      <div v-else>
        <div v-if="products.length" class="product-grid">
          <article v-for="product in products" :key="product.id" class="product-card">
            <RouterLink :to="`/home/product/detail/${product.id}`" class="product-image">
              <img :src="product.image" :alt="product.name" loading="lazy" />
            </RouterLink>
            <div class="product-body">
              <h3>{{ product.name }}</h3>
              <p class="product-subtitle">{{ product.subtitle || currentCategory.defaultSubtitle }}</p>
              <div class="product-footer">
                <span class="product-price">￥{{ product.price }}</span>
                <div class="product-actions">
                  <button
                    type="button"
                    class="btn subtle"
                    @click.prevent="handleAddCollect(product.id)"
                  >
                    收藏
                  </button>
                  <button
                    type="button"
                    class="btn primary"
                    @click.prevent="handleAddCart(product.id)"
                  >
                    加入购物车
                  </button>
                </div>
              </div>
            </div>
          </article>
        </div>

        <p v-else class="empty-text">
          暂无相关商品，试试调整搜索关键词或稍后再来。
        </p>
      </div>

      <div class="pagination" v-if="pages > 1">
        <button type="button" :disabled="pageNum === 1" @click="gotoPage(pageNum - 1)">
          上一页
        </button>
        <button
          v-for="page in pageRange"
          :key="page"
          type="button"
          :class="{ active: page === pageNum }"
          @click="gotoPage(page)"
        >
          {{ page }}
        </button>
        <button type="button" :disabled="pageNum === pages" @click="gotoPage(pageNum + 1)">
          下一页
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  addCollect,
  addToCart,
  fetchCart,
  fetchHomeProductCategories,
  fetchHomeProductList,
} from '@/apis/home';
import { calculateCartTotal, notifyCartCleared, setCartTotal } from '@/utils/cart';

const route = useRoute();
const router = useRouter();

const fruitFallback = new URL('@/assets/home/images/placeholder-fruit.png', import.meta.url).href;
const vegetableFallback = new URL('@/assets/home/images/placeholder-vegetable.png', import.meta.url).href;
const fallbackHeroImages = [fruitFallback, vegetableFallback];

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const products = ref([]);
const heroImage = ref('');
const searchContent = ref('');
const pageNum = ref(1);
const pageSize = ref(8);
const pages = ref(1);
const loading = ref(false);
const totalCount = ref(0);

const categoriesWithMeta = ref([]);
const currentCategoryKey = ref('');
const categoriesLoaded = ref(false);
const initialProductsLoaded = ref(false);

const productSectionRef = ref(null);

const LEGACY_CATEGORY_IDS = {
  fruit: 1,
  vegetable: 2,
};

const SCROLL_OFFSET = 120;

const scrollToProducts = (position = 'section') => {
  nextTick(() => {
    if (position === 'top') {
      window.scrollTo({ top: 0, behavior: 'smooth' });
      return;
    }
    const target = productSectionRef.value;
    if (!target) return;
    const rect = target.getBoundingClientRect();
    const offsetTop = rect.top + window.scrollY - SCROLL_OFFSET;
    window.scrollTo({
      top: offsetTop > 0 ? offsetTop : 0,
      behavior: 'smooth',
    });
  });
};

const refreshCartTotal = async () => {
  try {
    const res = await fetchCart();
    if (res?.code === 0 && res.data?.cartProductVoList) {
      setCartTotal(calculateCartTotal(res.data.cartProductVoList));
    } else if (res?.code === -6) {
      notifyCartCleared();
    }
  } catch (error) {
    console.error('刷新购物车数量失败', error);
  }
};

const defaultCategoryMeta = {
  id: null,
  key: '',
  name: '精选品类',
  heroTitle: '精选好物',
  heroTag: 'Fresh Picks',
  tags: ['品质甄选', '新鲜直达', '营养搭配'],
  badge: { title: '精选', desc: '严选食材 · 新鲜到家' },
  heroImage: fruitFallback,
  introduction: {
    title: '健康饮食小贴士',
    description: '多样搭配、均衡营养，享受来自农场的自然好味道。',
  },
  placeholder: '搜索商品名称',
  defaultSubtitle: '新鲜严选 · 馆主推荐',
};

const categoryPresets = {
  1: {
    heroTitle: '甜润多汁的当季甄选水果',
    heroTag: 'Just Picked',
    tags: ['维C加油站', '冷链直达', '甜蜜多汁'],
    badge: {
      title: 'NATURAL',
      desc: '原产地认证 · 口感丰富',
    },
    heroImage: fruitFallback,
    introduction: {
      title: '健康水果小知识',
      description:
        '水果富含纤维、维生素与抗氧化物，有助于保持肌肤活力与增强抵抗力。记得多样搭配，补充全面营养。',
    },
    placeholder: '搜索喜爱的水果名称',
    defaultSubtitle: '产地直供 · 每日现摘',
  },
  2: {
    heroTitle: '鲜嫩爽脆的绿色能量餐',
    heroTag: 'Green & Fresh',
    tags: ['膳食纤维', '绿色无公害', '营养均衡'],
    badge: {
      title: 'ORGANIC',
      desc: '绿色认证 · 当日采摘',
    },
    heroImage: vegetableFallback,
    introduction: {
      title: '健康蔬菜小知识',
      description:
        '深色蔬菜富含铁与维生素A，低脂且高纤维。合理搭配蔬菜能让膳食更加均衡健康。',
    },
    placeholder: '搜索蔬菜名称',
    defaultSubtitle: '清甜爽脆 · 低脂轻负担',
  },
};

const fallbackThemes = [
  {
    heroTitle: '{name}每日鲜选',
    heroTag: 'Daily Fresh',
    tags: ['产地直发', '当季鲜味', '严选品质'],
    badge: { title: 'FRESH', desc: '{name}严选' },
    introduction: {
      title: '{name}小贴士',
      description: '我们为你挑选高人气的{name}，营养与美味一步到位。',
    },
    placeholder: '搜索{name}关键词',
    defaultSubtitle: '新鲜严选 · 馆主推荐',
  },
  {
    heroTitle: '{name}灵感厨房',
    heroTag: 'Kitchen Ideas',
    tags: ['营养均衡', '灵感菜谱', '健康搭配'],
    badge: { title: 'QUALITY', desc: '安心之选' },
    introduction: {
      title: '{name}搭配建议',
      description: '用{name}搭配你喜爱的佐料，轻松做出家庭限定好味道。',
    },
    placeholder: '搜索{name}美味',
    defaultSubtitle: '营养升级 · 轻松烹饪',
  },
  {
    heroTitle: '{name}季节好物',
    heroTag: 'Seasonal Taste',
    tags: ['季节风味', '口碑热卖', '新鲜到家'],
    badge: { title: 'SEASON', desc: '{name}当季推荐' },
    introduction: {
      title: '{name}营养亮点',
      description: '{name}富含丰富营养，是家庭餐桌上的热门选择，快来尝鲜吧！',
    },
    placeholder: '搜索{name}相关商品',
    defaultSubtitle: '当季好味 · 不容错过',
  },
];

const createCategoryKey = (id) => `category-${id}`;

const applyTemplate = (template, name) =>
  template ? template.replaceAll('{name}', name) : '';

const buildCategoryMeta = (category, index) => {
  const id = Number(category.id ?? category.categoryId);
  const name = category.categoryName ?? category.name ?? `分类${id}`;
  const base =
    categoryPresets[id] ?? fallbackThemes[index % fallbackThemes.length];
  return {
    id,
    key: createCategoryKey(id),
    name,
    heroTitle: applyTemplate(base.heroTitle, name) || `${name}甄选`,
    heroTag: base.heroTag || 'Fresh Picks',
    tags: base.tags ?? defaultCategoryMeta.tags,
    badge: {
      title: base.badge?.title || defaultCategoryMeta.badge.title,
      desc: applyTemplate(base.badge?.desc, name) || defaultCategoryMeta.badge.desc,
    },
    heroImage:
      base.heroImage ??
      fallbackHeroImages[index % fallbackHeroImages.length] ??
      defaultCategoryMeta.heroImage,
    introduction: {
      title:
        applyTemplate(base.introduction?.title, name) ||
        defaultCategoryMeta.introduction.title,
      description:
        applyTemplate(base.introduction?.description, name) ||
        defaultCategoryMeta.introduction.description,
    },
    placeholder: applyTemplate(base.placeholder, name) || `搜索${name}名称`,
    defaultSubtitle: base.defaultSubtitle || defaultCategoryMeta.defaultSubtitle,
  };
};

const normalizeImage = (path) => {
  if (!path) return '';
  if (/^https?:\/\//.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const ensureCategoryKey = (rawKey) => {
  const matched = categoriesWithMeta.value.find((item) => item.key === rawKey);
  if (matched) return matched.key;
  const legacyId = LEGACY_CATEGORY_IDS[rawKey];
  if (legacyId) {
    const legacyTarget = categoriesWithMeta.value.find(
      (item) => Number(item.id) === Number(legacyId),
    );
    if (legacyTarget) return legacyTarget.key;
  }
  return categoriesWithMeta.value[0]?.key ?? null;
};

const fetchProducts = async ({ scroll = false, position = 'section' } = {}) => {
  const category = currentCategory.value;
  if (!category?.id) {
    products.value = [];
    pages.value = 1;
    totalCount.value = 0;
    heroImage.value = category?.heroImage || '';
    return;
  }
  loading.value = true;
  try {
    const res = await fetchHomeProductList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: category.id,
      content: searchContent.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      const {
        list = [],
        pages: totalPages = 1,
        pageNum: currentPage = 1,
        total = list.length,
      } = res.data;
      products.value = list.map((item) => ({
        id: item.id,
        name: item.productName,
        subtitle: item.info,
        image: normalizeImage(item.productPic),
        price: Number(item.price || 0).toFixed(2),
      }));
      if (!searchContent.value) {
        heroImage.value = products.value[0]?.image || category.heroImage || '';
      } else {
        heroImage.value = category.heroImage || '';
      }
      pages.value = totalPages || 1;
      pageNum.value = currentPage || 1;
      totalCount.value = Number(total) || list.length;
    } else {
      products.value = [];
      pages.value = 1;
      totalCount.value = 0;
      heroImage.value = category.heroImage || '';
    }
  } catch (error) {
    console.error('获取商品列表失败', error);
    products.value = [];
    pages.value = 1;
    totalCount.value = 0;
    heroImage.value = category.heroImage || '';
  } finally {
    loading.value = false;
    initialProductsLoaded.value = true;
    if (scroll) {
      scrollToProducts(position);
    }
  }
};

const syncRouteWithCategories = () => {
  if (!categoriesLoaded.value || !categoriesWithMeta.value.length) return;
  const normalizedKey = ensureCategoryKey(route.params.category);
  if (!normalizedKey) return;
  if (normalizedKey !== route.params.category) {
    router.replace({
      name: 'HomeProductList',
      params: { category: normalizedKey },
      query: route.query,
    });
    return;
  }
  if (normalizedKey !== currentCategoryKey.value) {
    const targetCategory = categoriesWithMeta.value.find(
      (item) => item.key === normalizedKey,
    );
    currentCategoryKey.value = normalizedKey;
    heroImage.value = targetCategory?.heroImage || '';
    searchContent.value = '';
    pageNum.value = 1;
    fetchProducts({ scroll: true, position: 'top' });
  }
};

const loadCategories = async () => {
  try {
    const res = await fetchHomeProductCategories();
    if (res?.code === 0 && Array.isArray(res.data)) {
      categoriesWithMeta.value = res.data.map((item, index) =>
        buildCategoryMeta(item, index),
      );
    } else {
      categoriesWithMeta.value = [];
    }
  } catch (error) {
    console.error('获取商品分类失败', error);
    categoriesWithMeta.value = [];
  } finally {
    categoriesLoaded.value = true;
    syncRouteWithCategories();
  }
};

const currentCategory = computed(
  () =>
    categoriesWithMeta.value.find((item) => item.key === currentCategoryKey.value) ??
    defaultCategoryMeta,
);

const introduction = computed(
  () => currentCategory.value.introduction ?? defaultCategoryMeta.introduction,
);
const placeholderText = computed(
  () => currentCategory.value.placeholder ?? defaultCategoryMeta.placeholder,
);

const heroImageSrc = computed(
  () => heroImage.value || currentCategory.value.heroImage || fallbackHeroImages[0],
);

const pageRange = computed(() => {
  const range = [];
  const start = Math.max(1, pageNum.value - 2);
  const end = Math.min(pages.value, pageNum.value + 2);
  for (let i = start; i <= end; i += 1) {
    range.push(i);
  }
  return range;
});

const handleSearch = () => {
  pageNum.value = 1;
  fetchProducts({ scroll: true, position: 'section' });
};

const gotoPage = (page) => {
  if (page < 1 || page > pages.value || page === pageNum.value) return;
  pageNum.value = page;
  fetchProducts({ scroll: true, position: 'section' });
};

const handleAddCart = async (productId) => {
  try {
    const res = await addToCart({ productId, quantity: 1 });
    if (res?.code === 0) {
      window.alert(res.msg || '加入购物车成功');
      await refreshCartTotal();
    } else {
      window.alert(res?.msg || '加入购物车失败，请先登录');
      if (res?.code === -6) {
        router.push('/login');
        notifyCartCleared();
      }
    }
  } catch (error) {
    console.error('加入购物车失败', error);
    window.alert('网络错误，加入购物车失败');
  }
};

const handleAddCollect = async (productId) => {
  try {
    const res = await addCollect(productId);
    if (res?.code === 0) {
      window.alert(res.msg || '收藏成功');
    } else {
      window.alert(res?.msg || '收藏失败，请先登录');
      if (res?.code === -6) {
        router.push('/login');
      }
    }
  } catch (error) {
    console.error('收藏失败', error);
    window.alert('网络错误，收藏失败');
  }
};

watch(
  () => route.params.category,
  () => {
    if (categoriesLoaded.value) {
      syncRouteWithCategories();
    }
  },
);

onMounted(() => {
  loadCategories();
});
</script>

<style scoped>
.product-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f8f0 0%, #fdfdfc 50%, #f7f8fb 100%);
  margin-top: calc(-1 * (var(--header-height) + clamp(4px, 2vw, 12px)));
  padding: clamp(20px, 5vw, 40px) clamp(16px, 6vw, 72px) clamp(32px, 7vw, 72px);
  display: flex;
  flex-direction: column;
  gap: clamp(28px, 5.5vw, 56px);
}

.page-hero {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: clamp(24px, 4.5vw, 52px);
  padding: clamp(24px, 4.5vw, 48px);
  background: radial-gradient(circle at top left, rgba(76, 187, 108, 0.18), transparent 52%),
    radial-gradient(circle at bottom right, rgba(249, 115, 22, 0.12), transparent 55%), #ffffff;
  border-radius: 28px;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.12);
  position: relative;
  overflow: hidden;
}

.page-hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(76, 187, 108, 0.12), transparent 55%);
  pointer-events: none;
}

.hero-copy {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.eyebrow {
  font-size: 13px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--home-primary-dark, #2f8f4a);
  font-weight: 600;
}

.hero-copy h1 {
  margin: 0;
  font-size: clamp(32px, 5vw, 46px);
  line-height: 1.15;
  color: #0f172a;
}

.hero-note {
  margin: 0;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.58);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.hero-desc {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: rgba(15, 23, 42, 0.7);
}

.hero-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 6px;
}

.search-bar {
  display: flex;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.24);
}

.search-bar input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  color: #1f2937;
  outline: none;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  border: none;
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.btn.primary {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.96), rgba(99, 209, 130, 0.92));
  color: #ffffff;
  box-shadow: 0 14px 24px rgba(76, 187, 108, 0.3);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 18px 32px rgba(76, 187, 108, 0.32);
}

.btn.subtle {
  padding: 8px 14px;
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
  transition: background 0.18s ease;
}

.btn.subtle:hover {
  background: rgba(99, 102, 241, 0.2);
}

.hero-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  color: rgba(15, 23, 42, 0.64);
  font-size: 12px;
}

.hero-tags span {
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(76, 187, 108, 0.15);
}

.hero-visual {
  position: relative;
  border-radius: 28px;
  overflow: hidden;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.98), rgba(239, 248, 242, 0.9));
  box-shadow: 0 20px 42px rgba(15, 23, 42, 0.16);
  min-height: clamp(260px, 45vw, 360px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(12px, 3vw, 24px);
}

.hero-visual img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 22px;
  display: block;
}

.visual-badge {
  position: absolute;
  bottom: 20px;
  left: 20px;
  padding: 12px 16px;
  border-radius: 18px;
  background: rgba(15, 23, 42, 0.82);
  color: #ffffff;
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.25);
}

.visual-badge h3 {
  margin: 0;
  font-size: 16px;
}

.visual-badge p {
  margin: 4px 0 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
}

.product-section {
  display: flex;
  flex-direction: column;
  gap: clamp(24px, 4vw, 36px);
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}

.section-eyebrow {
  display: inline-block;
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--home-primary-dark, #2f8f4a);
  font-weight: 600;
}

.section-header h2 {
  margin: 6px 0 0;
  font-size: clamp(24px, 4vw, 30px);
  color: #0f172a;
}

.section-meta {
  margin: 0;
  font-size: 14px;
  color: rgba(71, 85, 105, 0.85);
}

.section-meta strong {
  color: var(--home-primary-dark, #2f8f4a);
}

.loading-state {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: rgba(15, 23, 42, 0.65);
  padding: 32px 0;
}

.spinner {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 3px solid rgba(76, 187, 108, 0.2);
  border-top-color: rgba(76, 187, 108, 0.9);
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: clamp(18px, 3vw, 28px);
}

.product-card {
  display: flex;
  flex-direction: column;
  border-radius: 24px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 20px 36px rgba(15, 23, 42, 0.12);
  transition: transform 0.22s ease, box-shadow 0.22s ease;
  justify-self: center;
  width: clamp(240px, 90%, 360px);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 26px 48px rgba(15, 23, 42, 0.18);
}

.product-image {
  position: relative;
  padding-top: 70%;
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.12), rgba(249, 115, 22, 0.08));
}

.product-image img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-body {
  padding: 20px 22px 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-body h3 {
  margin: 0;
  font-size: 18px;
  color: #111827;
}

.product-subtitle {
  margin: 0;
  font-size: 13px;
  color: rgba(71, 85, 105, 0.78);
  min-height: 36px;
}

.product-footer {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--home-primary-dark, #2f8f4a);
}

.product-actions {
  display: flex;
  gap: 10px;
}

.empty-text {
  text-align: center;
  padding: 48px 0;
  color: rgba(100, 116, 139, 0.76);
  font-size: 14px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.pagination button {
  border: none;
  border-radius: 999px;
  padding: 8px 14px;
  font-size: 14px;
  background: rgba(148, 163, 184, 0.18);
  color: rgba(15, 23, 42, 0.72);
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.pagination button:hover:not(:disabled) {
  background: rgba(76, 187, 108, 0.2);
  color: var(--home-primary-dark, #2f8f4a);
}

.pagination button.active {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.92), rgba(99, 209, 130, 0.9));
  color: #ffffff;
  box-shadow: 0 12px 20px rgba(76, 187, 108, 0.25);
}

.pagination button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

@media (max-width: 960px) {
  .product-page {
    padding: 28px clamp(16px, 5vw, 32px) 56px;
  }

  .page-hero {
    border-radius: 26px;
  }
}

@media (max-width: 640px) {
  .product-page {
    padding: 24px 16px 48px;
  }

  .hero-actions {
    gap: 12px;
  }

  .product-actions {
    flex-direction: column;
  }

  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>

