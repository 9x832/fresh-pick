<template>
  <div class="user-center-layout">
    <div class="user-center-inner">
      <aside class="user-center-sidebar">
        <ul>
          <li :class="{ active: isActive('/home/user/info') }">
            <RouterLink to="/home/user/info">个人信息</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/user/order') }">
            <RouterLink to="/home/user/order">我的订单</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/user/comment') }">
            <RouterLink to="/home/user/comment">我的评价</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/collect') }">
            <RouterLink to="/home/collect">我的收藏</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/address') }">
            <RouterLink to="/home/address">地址管理</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/user/password') }">
            <RouterLink to="/home/user/password">修改密码</RouterLink>
          </li>
        </ul>
      </aside>

      <section class="user-center-main">
        <div class="user_Borders">
          <div class="title_name">
            <div class="heading">
              <span class="name">我的收藏</span>
              <p class="desc">收藏的商品会保存在这里，方便快速回购</p>
            </div>
          </div>
          <div v-if="collects.length" class="collect_grid">
            <article class="collect_card" v-for="item in collects" :key="item.id">
              <RouterLink :to="`/home/product/detail/${item.productId}`" class="collect_thumb">
                <img :src="item.productImage" :alt="item.productName" loading="lazy" />
              </RouterLink>
              <div class="collect_body">
                <RouterLink :to="`/home/product/detail/${item.productId}`" class="collect_name">
                  {{ item.productName }}
                </RouterLink>
                <span class="collect_price">￥{{ item.price }}</span>
                <div class="collect_actions">
                  <button type="button" class="btn outline" @click="goDetail(item.productId)">
                    查看详情
                  </button>
                  <button type="button" class="btn subtle" @click="handleDelete(item.id)">
                    移除收藏
                  </button>
                </div>
              </div>
            </article>
          </div>
          <div v-else class="empty-text">还没有收藏任何商品。</div>
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
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchCollects, removeCollect } from '@/apis/home';

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const router = useRouter();
const route = useRoute();

const state = reactive({
  collects: [],
  pageNum: 1,
  pageSize: 8,
  pages: 1,
  loading: false,
});

const collects = computed(() => state.collects);
const pageNum = computed(() => state.pageNum);
const pages = computed(() => state.pages);
const isActive = (path) => route.path === path;

const photoUrl = (filename) => {
  if (!filename) return '';
  if (/^https?:/.test(filename)) return filename;
  const prefix = filename.startsWith('/') ? filename.slice(1) : filename;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${prefix}`;
};

const loadCollects = async () => {
  state.loading = true;
  try {
    const res = await fetchCollects({
      pageNum: state.pageNum,
      pageSize: state.pageSize,
    });
    if (res?.code === 0 && res.data) {
      const { list = [], pages: totalPages = 1, pageNum: currentPage = 1 } = res.data;
      state.collects = list.map((item) => ({
        id: item.id,
        productId: item.product?.id,
        productName: item.product?.productName || '未知商品',
        productImage: photoUrl(item.product?.productPic),
        price: Number(item.product?.price || 0).toFixed(2),
      }));
      state.pages = totalPages || 1;
      state.pageNum = currentPage || 1;
    } else if (res?.code === -6) {
      window.alert('请先登录后查看收藏');
      router.push('/login');
    } else {
      state.collects = [];
      state.pages = 1;
    }
  } catch (error) {
    console.error('加载收藏列表失败', error);
    state.collects = [];
    state.pages = 1;
  } finally {
    state.loading = false;
  }
};

const gotoPage = (page) => {
  if (page < 1 || page > state.pages || page === state.pageNum) return;
  state.pageNum = page;
  loadCollects();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const pageRange = computed(() => {
  const range = [];
  const start = Math.max(1, state.pageNum - 2);
  const end = Math.min(state.pages, state.pageNum + 2);
  for (let i = start; i <= end; i += 1) {
    range.push(i);
  }
  return range;
});

const handleDelete = async (id) => {
  if (!window.confirm('确定要移除该收藏吗？')) {
    return;
  }
  try {
    const res = await removeCollect(id);
    if (res?.code === 0) {
      window.alert(res.msg || '移除收藏成功');
      loadCollects();
    } else {
      window.alert(res?.msg || '移除收藏失败');
    }
  } catch (error) {
    console.error('移除收藏失败', error);
    window.alert('网络错误，移除收藏失败');
  }
};

const goDetail = (productId) => {
  if (!productId) return;
  router.push(`/home/product/detail/${productId}`);
};

onMounted(() => {
  loadCollects();
});
</script>

<style scoped>

.user-center-layout {
  display: flex;
  flex-direction: column;
  gap: clamp(24px, 4vw, 40px);
}

.user-center-inner {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  gap: clamp(20px, 3.5vw, 32px);
  padding: 0;
}

.user-center-sidebar {
  width: 240px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.95);
  padding: clamp(18px, 3vw, 28px);
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.12);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-center-sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-center-sidebar li {
  margin: 0;
}

.user-center-sidebar li a {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 18px;
  border-radius: 16px;
  color: rgba(15, 23, 42, 0.68);
  font-weight: 600;
  transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
  text-decoration: none;
}

.user-center-sidebar li.active a,
.user-center-sidebar li a:hover {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.16), rgba(99, 209, 130, 0.16));
  color: #2f8f4a;
  transform: translateX(4px);
}

.user-center-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3vw, 30px);
}

.user_Borders {
  border-radius: 28px;
  padding: clamp(26px, 4vw, 38px);
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 22px 44px rgba(15, 23, 42, 0.14);
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3vw, 28px);
}

.title_name {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
  padding-bottom: 18px;
}

.title_name .heading {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title_name .name {
  font-size: clamp(22px, 3.5vw, 26px);
  color: #0f172a;
  font-weight: 700;
}

.title_name .desc {
  margin: 0;
  font-size: 14px;
  color: rgba(71, 85, 105, 0.8);
}

.collect_grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: clamp(16px, 3vw, 24px);
}

.collect_card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 16px;
  border-radius: 20px;
  background: rgba(248, 250, 252, 0.92);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.18);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.collect_card:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 32px rgba(15, 23, 42, 0.12);
}

.collect_thumb {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
  display: block;
}

.collect_thumb img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.collect_body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.collect_name {
  font-weight: 600;
  color: #0f172a;
  text-decoration: none;
}

.collect_price {
  color: #f05a24;
  font-weight: 700;
}

.collect_actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn {
  border: none;
  border-radius: 999px;
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.btn.outline {
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
}

.btn.subtle {
  background: rgba(148, 163, 184, 0.16);
  color: rgba(15, 23, 42, 0.72);
}

.btn:hover {
  transform: translateY(-1px);
}

.empty-text {
  text-align: center;
  padding: 60px 0;
  color: rgba(100, 116, 139, 0.74);
}

.pagination {
  margin-top: clamp(16px, 4vw, 24px);
  display: flex;
  justify-content: center;
  gap: 10px;
}

.pagination button {
  border: none;
  border-radius: 999px;
  padding: 8px 16px;
  font-size: 13px;
  cursor: pointer;
  background: rgba(148, 163, 184, 0.16);
  color: rgba(15, 23, 42, 0.7);
  transition: background 0.18s ease, color 0.18s ease;
}

.pagination button.active,
.pagination button:hover:not(:disabled) {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.92), rgba(99, 209, 130, 0.92));
  color: #fff;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 960px) {
  .user-center-inner {
    flex-direction: column;
  }
}

@media (max-width: 640px) {
  .collect_thumb img {
    height: 180px;
  }
}
</style>

