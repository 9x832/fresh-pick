<template>
  <section class="comment-manage">
    <header class="page-header">
      <div>
        <h2>评论管理</h2>
        <p>查看用户评论内容，及时删除不合规评论。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchContent"
          type="text"
          class="search-input"
          placeholder="按评论内容搜索"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn ghost" @click="resetSearch">清空</button>
      </div>
    </header>

    <section class="card">
      <table class="comment-table">
        <thead>
          <tr>
            <th style="width: 80px;">评论ID</th>
            <th style="width: 80px;">商品图片</th>
            <th>商品名称</th>
            <th style="width: 140px;">用户</th>
            <th>评论内容</th>
            <th style="width: 180px;">评论时间</th>
            <th style="width: 120px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="loading-cell">评论数据加载中...</td>
          </tr>
          <tr v-else-if="!comments.length">
            <td colspan="7" class="empty-cell">暂无评论记录</td>
          </tr>
          <tr v-for="comment in comments" :key="comment.id">
            <td class="center">{{ comment.id }}</td>
            <td class="center">
              <img :src="resolveImage(comment.product?.productPic)" alt="商品图片" class="thumbnail" />
            </td>
            <td class="center">{{ comment.product?.productName || '-' }}</td>
            <td class="center">{{ comment.user?.username || comment.userId }}</td>
            <td class="content-cell">{{ comment.content }}</td>
            <td class="center">{{ formatDate(comment.createTime) }}</td>
            <td class="center">
              <button type="button" class="btn danger ghost" @click="handleDelete(comment)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <footer class="pagination">
        <button type="button" :disabled="pageInfo.pageNum <= 1" @click="gotoPage(pageInfo.pageNum - 1)">
          上一页
        </button>
        <span>第 {{ pageInfo.pageNum }} / {{ pageInfo.pages || 1 }} 页</span>
        <button
          type="button"
          :disabled="pageInfo.pageNum >= (pageInfo.pages || 1)"
          @click="gotoPage(pageInfo.pageNum + 1)"
        >
          下一页
        </button>
        <span>共 {{ pageInfo.total || 0 }} 条评论</span>
      </footer>
    </section>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { deleteComment, fetchComments } from '@/apis/admin';

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const comments = ref([]);
const loading = ref(false);
const searchContent = ref('');
const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  pages: 0,
  total: 0,
});

const resolveImage = (path) => {
  if (!path) return 'https://via.placeholder.com/60.png?text=Product';
  if (/^https?:\/\//i.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const formatDate = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const mi = String(date.getMinutes()).padStart(2, '0');
  const ss = String(date.getSeconds()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
};

const loadComments = async () => {
  loading.value = true;
  try {
    const res = await fetchComments({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      content: searchContent.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      comments.value = res.data.list || [];
      Object.assign(pageInfo, {
        pageNum: res.data.pageNum || 1,
        pageSize: res.data.pageSize || pageInfo.pageSize,
        pages: res.data.pages || 0,
        total: res.data.total || 0,
      });
    } else {
      comments.value = [];
      window.alert(res?.msg || '加载评论列表失败');
    }
  } catch (error) {
    console.error('加载评论列表失败', error);
    window.alert('网络错误，加载评论列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadComments();
};

const resetSearch = () => {
  searchContent.value = '';
  handleSearch();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadComments();
};

const handleDelete = async (comment) => {
  if (!comment?.id) return;
  if (!window.confirm(`确认删除该评论（ID: ${comment.id}）吗？`)) return;
  try {
    const res = await deleteComment(comment.id);
    if (res?.code === 0) {
      window.alert(res.msg || '删除成功');
      if (comments.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadComments();
    } else {
      window.alert(res?.msg || '删除评论失败');
    }
  } catch (error) {
    console.error('删除评论失败', error);
    window.alert('网络错误，删除评论失败');
  }
};

onMounted(() => {
  loadComments();
});
</script>

<style scoped>
.comment-manage {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #0f172a;
}

.page-header p {
  margin: 4px 0 0;
  color: #64748b;
}

.actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 260px;
  height: 38px;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  padding: 0 14px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-input:focus {
  border-color: #f97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.25);
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 0 16px;
  height: 38px;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
}

.btn.ghost {
  background: rgba(249, 115, 22, 0.12);
  color: #c2410c;
}

.btn.danger {
  background: rgba(248, 113, 113, 0.15);
  color: #b91c1c;
}

.btn.danger:hover {
  background: rgba(248, 113, 113, 0.25);
}

.card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.1);
  padding: 24px;
}

.comment-table {
  width: 100%;
  border-collapse: collapse;
}

.comment-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.comment-table th,
.comment-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.comment-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.6);
}

.center {
  text-align: center;
}

.content-cell {
  white-space: pre-wrap;
  line-height: 1.6;
}

.thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 12px;
  border: 2px solid rgba(251, 146, 60, 0.3);
}

.loading-cell,
.empty-cell {
  text-align: center;
  padding: 40px 0;
  color: #94a3b8;
}

.pagination {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
  font-size: 14px;
}

.pagination button {
  height: 32px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid #cbd5f5;
  background: #f1f5f9;
  cursor: pointer;
  transition: background 0.2s ease;
}

.pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pagination button:not(:disabled):hover {
  background: #e2e8f0;
}

@media (max-width: 960px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .search-input {
    flex: 1;
    min-width: 200px;
  }
}
</style>

