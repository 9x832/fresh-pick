<template>
  <footer class="site-footer">
    <div class="footer-content">
      <div class="footer-brand">
        <RouterLink to="/home" class="brand-link">
          <img :src="logoImg" alt="鲜采直送自营平台" />
          <div class="brand-text">
            <span class="name">鲜采直送自营平台</span>
            <p>鲜活好物 · 农场直达</p>
          </div>
        </RouterLink>
        <p class="brand-desc">
          我们坚持产地直采，严格管控冷链，让每一份新鲜如期抵达您的餐桌。
        </p>
      </div>

      <div class="footer-columns">
        <div class="footer-column">
          <h3>快捷导航</h3>
          <RouterLink
            v-for="link in quickNavLinks"
            :key="link.key"
            :to="link.to"
          >
            {{ link.label }}
          </RouterLink>
        </div>
        <div class="footer-column">
          <h3>联系客服</h3>
          <p>客服热线：400-000-2025</p>
          <p>服务时间：每日 9:00-21:00</p>
          <p>邮箱：2350064257@qq.com</p>
        </div>
        <div class="footer-column">
          <h3>关注我们</h3>
          <p>微信公众号：鲜采直送自营平台</p>
          <p>社区分享：#鲜采直送自营平台</p>
          <p>地址：仲恺农业工程学院</p>
        </div>
      </div>
    </div>
    <div class="footer-meta">
      <p>© {{ currentYear }} 鲜采直送自营平台. All rights reserved.</p>
      <p class="icp">粤ICP备2025号-1</p>
    </div>
  </footer>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { fetchHomeProductCategories } from '@/apis/home';

const logoImg = new URL('@/assets/home/images/logo.png', import.meta.url).href;

const createCategoryKey = (id) => `category-${id}`;

const categories = ref([]);

const fallbackCategoryLinks = [
  { key: 'fruit', label: '水果专区', to: '/home/product/fruit' },
  { key: 'vegetable', label: '蔬菜专区', to: '/home/product/vegetable' },
];

const quickCategoryLinks = computed(() => {
  if (!categories.value.length) {
    return fallbackCategoryLinks;
  }
  return categories.value.slice(0, 4).map((item) => {
    const rawId = item?.id ?? item?.categoryId;
    const id = Number(rawId);
    const key = createCategoryKey(id);
    return {
      key,
      label: item?.categoryName ?? item?.name ?? `分类${id}`,
      to: `/home/product/${key}`,
    };
  });
});

const quickNavLinks = computed(() => [
  ...quickCategoryLinks.value,
  { key: 'cart', label: '购物车', to: '/home/cart' },
  { key: 'center', label: '用户中心', to: '/home/user/info' },
]);

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

onMounted(() => {
  loadCategories();
});

const currentYear = computed(() => new Date().getFullYear());
</script>

<style scoped>
.site-footer {
  margin-top: clamp(40px, 8vw, 72px);
  background: linear-gradient(180deg, rgba(243, 248, 244, 0.6) 0%, rgba(229, 237, 231, 0.9) 100%);
  border-top: 1px solid rgba(148, 163, 184, 0.2);
  padding: clamp(32px, 8vw, 64px) clamp(16px, 8vw, 40px) 28px;
  color: #1f2937;
  min-height: var(--footer-min-height);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.footer-content {
  max-width: 1280px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(260px, 1.2fr) repeat(auto-fit, minmax(160px, 1fr));
  gap: clamp(24px, 4vw, 48px);
}

.footer-brand .brand-link {
  display: inline-flex;
  align-items: center;
  gap: 16px;
}

.footer-brand img {
  width: 56px;
  height: 56px;
  object-fit: contain;
  border-radius: 16px;
  box-shadow: 0 12px 24px rgba(76, 187, 108, 0.18);
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-text .name {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.brand-text p {
  margin: 0;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.6);
}

.brand-desc {
  margin: 18px 0 0;
  font-size: 14px;
  line-height: 1.7;
  color: rgba(15, 23, 42, 0.7);
}

.footer-columns {
  display: contents;
}

.footer-column {
  display: flex;
  flex-direction: column;
  gap: 10px;
  font-size: 14px;
  color: rgba(15, 23, 42, 0.72);
}

.footer-column h3 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
}

.footer-column a {
  color: inherit;
  transition: color 0.2s ease;
}

.footer-column a:hover {
  color: #2f8f4a;
}

.footer-meta {
  max-width: 1280px;
  margin: 32px auto 0;
  padding-top: 20px;
  border-top: 1px solid rgba(148, 163, 184, 0.25);
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: space-between;
  font-size: 13px;
  color: rgba(71, 85, 105, 0.8);
}

.footer-meta .icp {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

@media (max-width: 640px) {
  .footer-content {
    grid-template-columns: 1fr;
  }

  .footer-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
}
</style>
