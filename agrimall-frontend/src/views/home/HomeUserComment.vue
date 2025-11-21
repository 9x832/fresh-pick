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
              <span class="name">我的评价</span>
              <p class="desc">记录您对商品的真实体验，可点击商品查看详情</p>
            </div>
          </div>
          <div v-if="comments.length" class="comment_list">
            <article class="comment_card" v-for="comment in comments" :key="comment.id">
              <RouterLink :to="`/home/product/detail/${comment.productId}`" class="comment_thumb">
                <img :src="comment.productImage" :alt="comment.productName" loading="lazy" />
              </RouterLink>
              <div class="comment_body">
                <header class="comment_header">
                  <RouterLink :to="`/home/product/detail/${comment.productId}`" class="product_name">
                    {{ comment.productName }}
                  </RouterLink>
                  <time>{{ comment.createTime }}</time>
                </header>
                <p class="comment_content">{{ comment.content }}</p>
              </div>
            </article>
          </div>
          <div v-else class="empty-text">暂无评论记录。</div>
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
import { fetchUserComments } from '@/apis/home';
import '@/views/home/userCenter.css';

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const router = useRouter();
const route = useRoute();

const state = reactive({
  comments: [],
  pageNum: 1,
  pageSize: 2,
  pages: 1,
  loading: false,
});

const comments = computed(() => state.comments);
const pageNum = computed(() => state.pageNum);
const pages = computed(() => state.pages);

const photoUrl = (filename) => {
  if (!filename) return '';
  if (/^https?:/.test(filename)) return filename;
  const prefix = filename.startsWith('/') ? filename.slice(1) : filename;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${prefix}`;
};

const loadComments = async () => {
  state.loading = true;
  try {
    const res = await fetchUserComments({
      pageNum: state.pageNum,
      pageSize: state.pageSize,
    });
    if (res?.code === 0 && res.data) {
      const { list = [], pages: totalPages = 1, pageNum: currentPage = 1 } = res.data;
      state.comments = list.map((comment) => {
        const linkedProductId = comment.productId ?? comment.product?.id;
        return {
          id: comment.id,
          content: comment.content,
          createTime: comment.createTime,
          productId: linkedProductId,
          productName: comment.product?.productName || '未知商品',
          productImage: photoUrl(comment.product?.productPic),
        };
      });
      state.pages = totalPages || 1;
      state.pageNum = currentPage || 1;
    } else if (res?.code === -6) {
      window.alert('请先登录后查看评论');
      router.push('/login');
    } else {
      state.comments = [];
      state.pages = 1;
    }
  } catch (error) {
    console.error('加载用户评论失败', error);
    state.comments = [];
    state.pages = 1;
  } finally {
    state.loading = false;
  }
};

onMounted(() => {
  loadComments();
});

const isActive = (path) => route.path === path;

const pageRange = computed(() => {
  const range = [];
  const start = Math.max(1, state.pageNum - 2);
  const end = Math.min(state.pages, state.pageNum + 2);
  for (let i = start; i <= end; i += 1) {
    range.push(i);
  }
  return range;
});

const gotoPage = (page) => {
  if (page < 1 || page > state.pages || page === state.pageNum) return;
  state.pageNum = page;
  loadComments();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};
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

.comment_list {
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vw, 24px);
}

.comment_card {
  display: flex;
  gap: 18px;
  padding: 16px;
  border-radius: 20px;
  background: rgba(248, 250, 252, 0.9);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.16);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.comment_card:hover {
  transform: translateY(-2px);
  box-shadow: 0 18px 32px rgba(15, 23, 42, 0.12);
}

.comment_thumb {
  width: 100px;
  height: 100px;
  border-radius: 18px;
  overflow: hidden;
  flex-shrink: 0;
}

.comment_thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment_body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: rgba(15, 23, 42, 0.75);
}

.comment_header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.comment_header .product_name {
  font-weight: 600;
  color: #0f172a;
  text-decoration: none;
}

.comment_header time {
  font-size: 13px;
  color: rgba(71, 85, 105, 0.75);
}

.comment_content {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: rgba(15, 23, 42, 0.78);
}

.empty-text {
  text-align: center;
  padding: 60px 0;
  color: rgba(100, 116, 139, 0.75);
  font-size: 14px;
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
  .comment_card {
    flex-direction: column;
  }

  .comment_thumb {
    width: 100%;
    height: 180px;
  }
}
</style>

