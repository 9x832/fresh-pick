<template>
  <div class="home-index">
    <section class="hero">
      <div class="hero-content">
        <div class="hero-text">
          <span class="hero-eyebrow">新鲜速递 · Fresh in Season</span>
          <h1>每日甄选，农场直达</h1>
          <p>
            {{ heroDescription }}
          </p>
          <div class="hero-actions">
            <button
              v-for="action in heroCategoryButtons"
              :key="action.key"
              type="button"
              class="btn"
              :class="action.variant"
              @click="goCategory(action.key)"
            >
              {{ action.label }}
            </button>
          </div>
          <div class="hero-stats">
            <div v-for="item in heroStats" :key="item.label">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </div>
        <div class="hero-visual">
          <div class="hero-main-card" v-if="sliderImages.length">
            <img :src="sliderImages[currentSlide]?.image" alt="主推商品" />
            <div class="card-info" v-if="sliderImages[currentSlide]?.name">
              <h3>{{ sliderImages[currentSlide].name }}</h3>
              <p>{{ sliderImages[currentSlide].subtitle }}</p>
              <button type="button" class="btn ghost" @click="goProductDetail(sliderImages[currentSlide].id)">
                查看详情
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="feature-strip">
      <div class="feature-card" v-for="feature in featureList" :key="feature.title">
        <i :class="feature.icon"></i>
        <div>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.desc }}</p>
        </div>
      </div>
    </section>

    <section class="top-picks" v-if="topPickProducts.length">
      <header>
        <div>
          <span class="section-eyebrow">热销推荐</span>
          <h2>大家都在买</h2>
        </div>
        <button type="button" class="btn subtle" @click="goTopPicks">
          更多好物
        </button>
      </header>
      <div class="top-picks-grid">
        <article v-for="item in topPickProducts" :key="item.id" @click="goProductDetail(item.id)">
          <img :src="item.image" :alt="item.name" loading="lazy" />
          <div class="info">
            <h3>{{ item.name }}</h3>
            <p>{{ item.subtitle || '产地直采 · 当日新鲜' }}</p>
            <span class="price">￥{{ item.price }}</span>
          </div>
        </article>
      </div>
    </section>

    <section
      class="personal-recommend"
      v-if="personalRecommendProducts.length"
    >
      <header>
        <div>
          <span class="section-eyebrow">{{ personalRecommendEyebrow }}</span>
          <h2>{{ personalRecommendHeadline }}</h2>
        </div>
        <button
          type="button"
          class="btn subtle"
          @click="refreshRecommendations"
        >
          换一批
        </button>
      </header>
      <p class="section-desc">
        {{ personalRecommendDescription }}
      </p>
      <div class="recommend-grid">
        <article
          v-for="item in personalRecommendProducts"
          :key="item.id"
          class="recommend-card"
          @click="goProductDetail(item.id)"
        >
          <img :src="item.image" :alt="item.name" loading="lazy" />
          <div class="info">
            <h3>{{ item.name }}</h3>
            <p>{{ item.subtitle || '产地直供 · 鲜香四溢' }}</p>
            <span class="price">￥{{ item.price }}</span>
          </div>
        </article>
      </div>
    </section>

    <section class="personal-recommend loading" v-else-if="recommendLoading">
      <div class="loading-placeholder">
        正在为你挑选推荐...
      </div>
    </section>

    <section class="category-panels">
      <article class="category-card" v-for="panel in categoryPanels" :key="panel.key">
        <div class="category-head">
          <div>
            <span class="section-eyebrow">{{ panel.tagline }}</span>
            <h2>{{ panel.title }}</h2>
          </div>
          <button type="button" class="btn subtle" @click="goCategory(panel.key)">
            浏览全部
          </button>
        </div>
        <div class="category-grid">
          <article
            v-for="product in panel.products"
            :key="product.id"
            class="mini-card"
            @click="goProductDetail(product.id)"
          >
            <img :src="product.image" :alt="product.name" loading="lazy" />
            <div>
              <h3>{{ product.name }}</h3>
              <p>{{ product.subtitle || panel.defaultSubtitle }}</p>
              <span class="price">￥{{ product.price }}</span>
            </div>
          </article>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  fetchHomeProductCategories,
  fetchHomeProductList,
  fetchHomeTopProducts,
  fetchPersonalRecommend,
  fetchPopularRecommend,
} from '@/apis/home';

const router = useRouter();

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const sliderImages = ref([]);
const categoriesWithKey = ref([]);
const categoryProductsMap = ref({});
const currentSlide = ref(0);
const personalRecommendProducts = ref([]);
const recommendLoading = ref(false);
const recommendPage = ref(0);

const LEGACY_CATEGORY_IDS = {
  fruit: 1,
  vegetable: 2,
};

const panelThemes = [
  {
    title: '{name}优选',
    tagline: 'FRESH PICKS',
    defaultSubtitle: '产地直供 · 鲜甜多汁',
  },
  {
    title: '{name}精选',
    tagline: 'GREEN DAILY',
    defaultSubtitle: '应季采摘 · 营养丰富',
  },
  {
    title: '{name}好物',
    tagline: 'SEASONAL FAVORITES',
    defaultSubtitle: '严选品质 · 产地直发',
  },
  {
    title: '{name}推荐',
    tagline: 'FARM TO TABLE',
    defaultSubtitle: '田间美味 · 新鲜到家',
  },
];

const categoryPresets = {
  1: {
    title: '鲜果主场',
    tagline: 'FRESH FRUITS',
    defaultSubtitle: '甜润多汁 · 冷链直达',
  },
  2: {
    title: '绿色鲜蔬',
    tagline: 'GREEN VEGGIES',
    defaultSubtitle: '脆嫩爽口 · 当日采摘',
  },
};

const normalizeImage = (path) => {
  if (!path) return '';
  if (/^https?:\//.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const formatProduct = (product) => ({
  id: product.id,
  name: product.productName,
  subtitle: product.info,
  image: normalizeImage(product.productPic),
  price: Number(product.price || 0).toFixed(2),
  sellCount: Number(product.sellNumber ?? product.sellNum ?? 0),
  categoryId:
    product.categoryId ??
    product.productCategoryId ??
    product.productTypeId ??
    product.category_id ??
    null,
});

const createCategoryKey = (id) => `category-${id}`;

const visibleCategories = computed(() => categoriesWithKey.value.slice(0, 4));

const totalShowcaseProducts = computed(() =>
  visibleCategories.value.reduce((sum, category) => {
    const products = categoryProductsMap.value[category.id] || [];
    return sum + products.length;
  }, 0),
);

const heroStats = computed(() => [
  { label: '当日直送订单', value: `${Math.max(totalShowcaseProducts.value * 6, 96)}+` },
  { label: '合作基地', value: '120+' },
  { label: '用户好评率', value: '98%' },
]);

const isLoggedIn = ref(Boolean(localStorage.getItem('token')));

const personalRecommendEyebrow = computed(() =>
  isLoggedIn.value ? 'PERSONAL PICKS' : 'BEST SELLERS',
);

const personalRecommendHeadline = computed(() =>
  isLoggedIn.value ? '为你推荐' : '热销好物推荐',
);

const personalRecommendDescription = computed(() =>
  isLoggedIn.value
    ? '基于你的近期购买及收藏，为你精选同好们都在回购的优质农产品。'
    : '大家近期都在购买的口碑单品，不用挑也不会踩雷。',
);

const featureList = computed(() => [
  { icon: 'ri-seedling-line', title: '原产地直采', desc: '来自合作农场，田间到舌尖不打折' },
  { icon: 'ri-truck-line', title: '冷链速配', desc: '全程2-8℃冷链，守护营养新鲜' },
  { icon: 'ri-shield-check-line', title: '质检把关', desc: '多重质检流程，严控农残指标' },
  { icon: 'ri-service-line', title: '专业服务', desc: '7×12 小时客服在线，售后无忧' },
]);

const topPickProducts = computed(() => sliderImages.value.slice(0, 8));

const heroCategoryButtons = computed(() => {
  if (!visibleCategories.value.length) {
    return [
      { key: 'fruit', label: '选购水果', variant: 'primary' },
      { key: 'vegetable', label: '精选蔬菜', variant: 'outline' },
      { key: 'grain', label: '探索五谷杂粮', variant: 'subtle' },
    ];
  }
  const variants = ['primary', 'outline', 'subtle', 'outline'];
  return visibleCategories.value.slice(0, 4).map((category, index) => ({
    key: category.key,
    label: index === 0 ? `选购${category.name}` : `探索${category.name}`,
    variant: variants[index] || 'subtle',
  }));
});

const categoryPanels = computed(() =>
  visibleCategories.value.map((category, index) => {
    const products = (categoryProductsMap.value[category.id] || []).slice(0, 4);
    const preset =
      categoryPresets[category.id] ?? panelThemes[index % panelThemes.length];
    const titleTemplate = preset.title || '{name}优选';
    return {
      key: category.key,
      title: titleTemplate.replace('{name}', category.name),
      tagline: preset.tagline || 'FARM TO TABLE',
      defaultSubtitle: preset.defaultSubtitle || '产地直供 · 新鲜到家',
      products,
    };
  }),
);

const totalSlides = computed(() => sliderImages.value.length);

const heroDescription = computed(() => {
  const names = visibleCategories.value.map((category) => category.name);
  if (names.length >= 3) {
    return `时令${names.slice(0, 3).join('、')}等${names.length}大品类同仓发货，新鲜直抵你的餐桌。`;
  }
  if (names.length === 2) {
    return `${names[0]}的甜润与${names[1]}的清新同行，新鲜好味每日直送。`;
  }
  if (names.length === 1) {
    return `${names[0]}每日产地直采，冷链护送，守住一口鲜甜。`;
  }
  return '精选果蔬从农田到餐桌，全程冷链保障新鲜，满足一家人的每日所需。';
});

const getCategoryByKey = (key) =>
  categoriesWithKey.value.find((category) => category.key === key);

const getCategoryById = (id) => {
  const idNumber = Number(id);
  return categoriesWithKey.value.find((category) => Number(category.id) === idNumber);
};

let slideTimer = null;

const goToSlide = (index) => {
  currentSlide.value = index;
};

const nextSlide = () => {
  if (!totalSlides.value) return;
  currentSlide.value = (currentSlide.value + 1) % totalSlides.value;
};

const startSlideTimer = () => {
  stopSlideTimer();
  slideTimer = window.setInterval(nextSlide, 5000);
};

const stopSlideTimer = () => {
  if (slideTimer) {
    window.clearInterval(slideTimer);
    slideTimer = null;
  }
};

const goProductDetail = (id) => {
  if (!id) return;
  router.push({ name: 'HomeProductDetail', params: { id } });
};

const goCategory = (key) => {
  const target = getCategoryByKey(key);
  if (target) {
    router.push(`/home/product/${target.key}`);
    return;
  }
  const legacyId = LEGACY_CATEGORY_IDS[key];
  if (legacyId) {
    const legacyTarget = getCategoryById(legacyId);
    if (legacyTarget) {
      router.push(`/home/product/${legacyTarget.key}`);
      return;
    }
  }
  if (categoriesWithKey.value.length) {
    router.push(`/home/product/${categoriesWithKey.value[0].key}`);
  }
};

const resolveCategoryKey = (product) => {
  if (!product || product.categoryId == null) return null;
  const target = getCategoryById(product.categoryId);
  return target?.key ?? null;
};

const goTopPicks = () => {
  router.push({ name: 'HomeHotRank' });
};

const syncLoginState = () => {
  isLoggedIn.value = Boolean(localStorage.getItem('token'));
};

const loadRecommendations = async (forcePopular = false) => {
  recommendLoading.value = true;
  try {
    syncLoginState();
    if (forcePopular) {
      recommendPage.value = Math.max(recommendPage.value, 0);
    }
    if (!forcePopular && isLoggedIn.value) {
      const res = await fetchPersonalRecommend({
        size: 8,
        page: recommendPage.value,
        _: Date.now(),
      });
      if (res?.code === 0 && Array.isArray(res.data) && res.data.length) {
        personalRecommendProducts.value = res.data.map(formatProduct);
        return;
      }
    }
    const fallbackRes = await fetchPopularRecommend({
      size: 8,
      page: recommendPage.value,
      _: Date.now(),
    });
    if (fallbackRes?.code === 0 && Array.isArray(fallbackRes.data)) {
      personalRecommendProducts.value = fallbackRes.data.map(formatProduct);
    } else {
      personalRecommendProducts.value = [];
    }
  } catch (error) {
    console.error('加载推荐数据失败', error);
    personalRecommendProducts.value = [];
  } finally {
    recommendLoading.value = false;
  }
};

const refreshRecommendations = async () => {
  recommendPage.value += 1;
  await loadRecommendations(false);
};

const fetchHomeData = async () => {
  try {
    const [categoryRes, topRes] = await Promise.all([
      fetchHomeProductCategories(),
      fetchHomeTopProducts(),
    ]);

    const rawCategories = Array.isArray(categoryRes?.data) ? categoryRes.data : [];
    const normalizedCategories = rawCategories.map((item, index) => {
      const id = Number(item.id ?? item.categoryId);
      const name = item.categoryName ?? item.name ?? `分类${id}`;
      const key = createCategoryKey(id);
      const preset =
        categoryPresets[id] ?? panelThemes[index % panelThemes.length];
      return {
        id,
        key,
        name,
        title: (preset.title || '{name}优选').replace('{name}', name),
        tagline: preset.tagline || 'FARM TO TABLE',
        defaultSubtitle: preset.defaultSubtitle || '产地直供 · 新鲜到家',
      };
    });

    categoriesWithKey.value = normalizedCategories;

    const primaryCategories = normalizedCategories.slice(0, 4);
    const productsEntries = await Promise.all(
      primaryCategories.map(async (category) => {
        try {
          const res = await fetchHomeProductList({
            pageNum: 1,
            pageSize: 8,
            categoryId: category.id,
          });
          if (res?.code === 0 && res.data?.list) {
            return [category.id, res.data.list.map(formatProduct)];
          }
        } catch (error) {
          console.error(`加载分类 ${category.name} 商品失败`, error);
        }
        return [category.id, []];
      }),
    );

    categoryProductsMap.value = productsEntries.reduce((acc, [id, list]) => {
      acc[id] = list;
      return acc;
    }, {});

    if (topRes?.code === 0 && Array.isArray(topRes.data)) {
      sliderImages.value = topRes.data.map((item) => ({
        ...formatProduct(item),
      }));
    } else {
      sliderImages.value = [];
    }
  } catch (error) {
    console.error('加载首页数据失败', error);
    categoriesWithKey.value = [];
    categoryProductsMap.value = {};
    sliderImages.value = [];
  }
};

const handleStorageChange = ({ key }) => {
  if (key && key !== 'token' && key !== 'username') {
    return;
  }
  const previousState = isLoggedIn.value;
  syncLoginState();
  if (previousState !== isLoggedIn.value) {
    recommendPage.value = 0;
    loadRecommendations(false);
  }
};

onMounted(async () => {
  window.addEventListener('storage', handleStorageChange);
  recommendPage.value = 0;
  await fetchHomeData();
  await loadRecommendations(false);
  startSlideTimer();
});

onBeforeUnmount(() => {
  stopSlideTimer();
  window.removeEventListener('storage', handleStorageChange);
});
</script>

<style scoped>
.home-index {
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3.5vw, 40px);
  margin-top: calc(-1 * (var(--header-height) + clamp(4px, 2vw, 12px)));
  padding-bottom: clamp(24px, 5.5vw, 56px);
}

.hero {
  background: radial-gradient(circle at left top, rgba(133, 193, 46, 0.12), transparent 52%),
    radial-gradient(circle at right bottom, rgba(241, 133, 0, 0.1), transparent 58%),
    #ffffff;
  border-radius: 22px;
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
  padding: clamp(18px, 3.5vw, 32px);
}

.hero-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: clamp(24px, 4vw, 40px);
  align-items: center;
}

.hero-text {
  display: flex;
  flex-direction: column;
  gap: 16px;
  color: #0f172a;
}

.hero-eyebrow {
  font-size: 13px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #2f8f4a;
  font-weight: 600;
}

.hero-text h1 {
  margin: 0;
  font-size: clamp(32px, 5vw, 44px);
  line-height: 1.1;
}

.hero-text p {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: rgba(15, 23, 42, 0.72);
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn {
  padding: 10px 22px;
  border-radius: 999px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.2s ease;
}

.btn.primary {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.96), rgba(99, 209, 130, 0.92));
  color: #ffffff;
  box-shadow: 0 16px 28px rgba(76, 187, 108, 0.25);
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 22px 36px rgba(76, 187, 108, 0.3);
}

.btn.outline {
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
}

.btn.outline:hover {
  background: rgba(99, 102, 241, 0.2);
}

.btn.subtle {
  background: rgba(148, 163, 184, 0.16);
  color: #0f172a;
}

.btn.subtle:hover {
  background: rgba(148, 163, 184, 0.26);
}

.btn.ghost {
  background: rgba(255, 255, 255, 0.9);
  color: #2f8f4a;
  border: 1px solid rgba(99, 209, 130, 0.35);
}

.btn.ghost:hover {
  background: rgba(255, 255, 255, 1);
}

.btn:hover {
  transform: translateY(-1px);
}

.hero-stats {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
  gap: 12px;
}

.hero-stats div {
  background: rgba(255, 255, 255, 0.82);
  border-radius: 16px;
  padding: 12px 16px;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.18);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.hero-stats strong {
  font-size: 20px;
  color: #2f8f4a;
}

.hero-stats span {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.6);
}

.hero-visual {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero-main-card {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  height: clamp(280px, 42vw, 360px);
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(12px, 2vw, 20px);
  box-shadow: 0 20px 36px rgba(15, 23, 42, 0.12);
}

.hero-main-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 18px;
}

.hero-main-card .card-info {
  position: absolute;
  inset: auto 0 0 0;
  padding: 16px 22px;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0) 0%, rgba(15, 23, 42, 0.65) 100%);
  color: #ffffff;
}

.feature-strip {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  background: #ffffff;
  border-radius: 22px;
  box-shadow: 0 18px 34px rgba(15, 23, 42, 0.1);
  padding: 18px clamp(18px, 4vw, 28px);
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 12px;
  color: rgba(15, 23, 42, 0.76);
}

.feature-card i {
  font-size: 24px;
  color: #2f8f4a;
  background: rgba(99, 209, 130, 0.18);
  padding: 10px;
  border-radius: 14px;
}

.feature-card h3 {
  margin: 0;
  font-size: 15px;
  color: #0f172a;
}

.feature-card p {
  margin: 4px 0 0;
  font-size: 13px;
}

.top-picks {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.88) 0%, rgba(245, 248, 244, 0.96) 100%);
  border-radius: 26px;
  box-shadow: 0 18px 34px rgba(15, 23, 42, 0.1);
  padding: clamp(24px, 4vw, 36px);
  display: flex;
  flex-direction: column;
  gap: clamp(18px, 3vw, 26px);
}

.top-picks header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.section-eyebrow {
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(15, 23, 42, 0.6);
  font-weight: 600;
}

.top-picks h2 {
  margin: 6px 0 0;
  font-size: clamp(22px, 3vw, 28px);
  color: #0f172a;
}

.top-picks-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: clamp(16px, 3vw, 24px);
}

@media (max-width: 1280px) {
  .top-picks-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .top-picks-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

.top-picks-grid article {
  background: #ffffff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.12);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.top-picks-grid article img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.top-picks-grid article .info {
  padding: 16px 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.top-picks-grid article h3 {
  margin: 0;
  font-size: 16px;
  color: #0f172a;
}

.top-picks-grid article p {
  margin: 0;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.65);
}

.price {
  font-size: 16px;
  font-weight: 700;
  color: #2f8f4a;
}

.top-picks-grid article:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 28px rgba(15, 23, 42, 0.14);
}

.personal-recommend {
  background: #ffffff;
  border-radius: 26px;
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.1);
  padding: clamp(24px, 4vw, 36px);
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vw, 24px);
}

.personal-recommend header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.personal-recommend h2 {
  margin: 6px 0 0;
  font-size: clamp(22px, 3vw, 28px);
  color: #0f172a;
}

.section-desc {
  margin: 0;
  font-size: 14px;
  color: rgba(15, 23, 42, 0.62);
  line-height: 1.7;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: clamp(16px, 3vw, 24px);
}

.recommend-card {
  background: rgba(246, 249, 244, 0.92);
  border-radius: 22px;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.12);
}

.recommend-card img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.recommend-card .info {
  padding: 16px 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recommend-card h3 {
  margin: 0;
  font-size: 16px;
  color: #0f172a;
}

.recommend-card p {
  margin: 0;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.62);
}

.recommend-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 28px rgba(15, 23, 42, 0.12);
}

.personal-recommend.loading {
  align-items: center;
  justify-content: center;
  min-height: 160px;
}

.loading-placeholder {
  font-size: 14px;
  color: rgba(15, 23, 42, 0.6);
}

.category-panels {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(20px, 3.5vw, 32px);
}

.category-card {
  background: #ffffff;
  border-radius: 26px;
  box-shadow: 0 18px 34px rgba(15, 23, 42, 0.1);
  padding: clamp(22px, 3.5vw, 32px);
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vw, 24px);
}

.category-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.category-head h2 {
  margin: 6px 0 0;
  font-size: clamp(20px, 2.6vw, 26px);
  color: #0f172a;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(14px, 2.5vw, 20px);
}

.mini-card {
  display: flex;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 18px;
  background: rgba(248, 250, 252, 0.92);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.12);
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.mini-card img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 14px;
}

.mini-card h3 {
  margin: 0;
  font-size: 15px;
  color: #0f172a;
}

.mini-card p {
  margin: 4px 0 8px;
  font-size: 12px;
  color: rgba(15, 23, 42, 0.62);
}

.mini-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
}

@media (max-width: 960px) {
  .hero {
    padding: clamp(24px, 6vw, 32px);
  }

  .hero-main-card {
    min-height: 220px;
  }

  .feature-strip {
    padding: 16px;
  }

  .top-picks-grid article img {
    height: 140px;
  }

  .recommend-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .category-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .hero-actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .category-panels {
    grid-template-columns: minmax(0, 1fr);
  }

  .category-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 1280px) and (min-width: 961px) {
  .recommend-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .category-panels {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 480px) {
  .recommend-grid {
    grid-template-columns: 1fr;
  }
}
</style>

