<template>
  <div class="hot-rank-page">
    <section class="hot-rank-hero">
      <div class="hero-text">
        <span class="eyebrow">HOT SELLER BOARD</span>
        <h1>热销榜单</h1>
        <p>
          统计商城实时销量，精选当下最受欢迎的果蔬好物。排行榜每次进入自动更新，助你快速锁定爆款。
        </p>
      </div>
      <button type="button" class="btn ghost" @click="refresh">
        刷新榜单
      </button>
    </section>

    <section class="hot-rank-content" v-if="!loading && products.length">
      <article
        v-for="(item, index) in products"
        :key="item.id"
        class="rank-card"
        @click="goProductDetail(item.id)"
      >
        <div class="rank-badge" :class="badgeClass(index)">
          <span>{{ index + 1 }}</span>
        </div>
        <img :src="item.image" :alt="item.name" loading="lazy" />
        <div class="info">
          <header>
            <h3>{{ item.name }}</h3>
            <span class="price">￥{{ item.price }}</span>
          </header>
          <p class="subtitle">{{ item.subtitle || '热销爆款 · 新鲜直达' }}</p>
          <footer>
            <span class="sell-count">月售 {{ item.sellCount }}</span>
            <button type="button" class="btn subtle">查看详情</button>
          </footer>
        </div>
      </article>
    </section>

    <section class="state-block" v-else-if="loading">
      <div class="loading">正在获取热销榜单...</div>
    </section>

    <section class="state-block empty" v-else>
      <p>暂时没有热销数据，稍后再来看看吧。</p>
      <button type="button" class="btn" @click="goHome">
        返回首页
      </button>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { fetchHotRankProducts } from '@/apis/home';

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const router = useRouter();
const loading = ref(true);
const products = ref([]);

const normalizeImage = (path) => {
  if (!path) return '';
  if (/^https?:\/\//.test(path)) {
    return path;
  }
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const formatProduct = (product) => ({
  id: product.id,
  name: product.productName,
  subtitle: product.info,
  image: normalizeImage(product.productPic),
  price: Number(product.price || 0).toFixed(2),
  sellCount: Number(product.sellNum ?? product.sellNumber ?? 0),
});

const load = async () => {
  loading.value = true;
  try {
    const res = await fetchHotRankProducts({ size: 30, _: Date.now() });
    if (res?.code === 0 && Array.isArray(res.data)) {
      products.value = res.data.map(formatProduct);
    } else {
      products.value = [];
    }
  } catch (error) {
    console.error('获取热销榜失败', error);
    products.value = [];
  } finally {
    loading.value = false;
  }
};

const refresh = async () => {
  await load();
};

const goHome = () => {
  router.push('/home');
};

const goProductDetail = (id) => {
  if (!id) return;
  router.push({ name: 'HomeProductDetail', params: { id } });
};

const badgeClass = (index) => {
  if (index === 0) return 'gold';
  if (index === 1) return 'silver';
  if (index === 2) return 'bronze';
  return '';
};

onMounted(async () => {
  window.scrollTo({ top: 0, behavior: 'auto' });
  await load();
});
</script>

<style scoped>
.hot-rank-page {
  display: flex;
  flex-direction: column;
  gap: clamp(18px, 3.5vw, 32px);
  margin-top: calc(-1 * clamp(32px, 6vw, 72px));
  padding: clamp(16px, 2vw, 20px) clamp(12px, 4vw, 24px) clamp(32px, 6vw, 48px);
}

.hot-rank-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: clamp(16px, 3vw, 32px);
  padding: clamp(16px, 3.5vw, 28px);
  border-radius: 26px;
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.12), rgba(99, 209, 130, 0.08));
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.08);
}

.hero-text {
  max-width: 640px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  color: #0f172a;
}

.hero-text .eyebrow {
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: rgba(15, 23, 42, 0.6);
  font-weight: 600;
}

.hero-text h1 {
  margin: 0;
  font-size: clamp(28px, 4vw, 40px);
  font-weight: 700;
}

.hero-text p {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: rgba(15, 23, 42, 0.72);
}

.btn {
  padding: 10px 22px;
  border: none;
  border-radius: 999px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.btn:hover {
  transform: translateY(-1px);
}

.btn.ghost {
  background: #ffffff;
  color: #256c3c;
  border: 1px solid rgba(37, 108, 60, 0.22);
  box-shadow: 0 12px 20px rgba(37, 108, 60, 0.12);
}

.btn.subtle {
  background: rgba(148, 163, 184, 0.16);
  color: #0f172a;
}

.hot-rank-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: clamp(18px, 3vw, 28px);
}

.rank-card {
  display: flex;
  gap: 16px;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
  padding: clamp(16px, 2.5vw, 22px);
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
  position: relative;
}

.rank-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.12);
}

.rank-card img {
  width: clamp(88px, 14vw, 112px);
  height: clamp(88px, 14vw, 112px);
  border-radius: 18px;
  object-fit: cover;
}

.rank-badge {
  position: absolute;
  inset: clamp(12px, 2vw, 16px) auto auto clamp(12px, 2vw, 16px);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(15, 23, 42, 0.08);
  color: #0f172a;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  box-shadow: inset 0 0 0 2px rgba(15, 23, 42, 0.08);
}

.rank-badge.gold {
  background: linear-gradient(135deg, #f9d57a, #f3b547);
  color: #4a2b00;
}

.rank-badge.silver {
  background: linear-gradient(135deg, #d7e1ec, #a8b2c9);
  color: #1f2c3a;
}

.rank-badge.bronze {
  background: linear-gradient(135deg, #e4c3aa, #ba8f6b);
  color: #3b2414;
}

.rank-card .info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.rank-card .info header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.rank-card .info h3 {
  margin: 0;
  font-size: 18px;
  color: #0f172a;
}

.rank-card .subtitle {
  margin: 0;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.62);
  line-height: 1.6;
}

.rank-card footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.price {
  font-weight: 700;
  color: #2f8f4a;
  font-size: 17px;
}

.sell-count {
  font-size: 13px;
  color: rgba(15, 23, 42, 0.6);
}

.state-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  min-height: 200px;
  background: #ffffff;
  border-radius: 22px;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.12);
  color: rgba(15, 23, 42, 0.64);
}

.state-block.empty p {
  margin: 0;
}

.loading {
  font-size: 14px;
}

@media (max-width: 960px) {
  .hot-rank-hero {
    flex-direction: column;
    align-items: stretch;
  }

  .rank-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .rank-card img {
    width: clamp(120px, 40vw, 160px);
    height: clamp(120px, 40vw, 160px);
  }

  .rank-card .info header {
    flex-direction: column;
    gap: 6px;
  }

  .rank-card footer {
    flex-direction: column;
  }
}

@media (max-width: 640px) {
  .hot-rank-content {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>


