<template>
  <section class="product-manage">
    <header class="page-header">
      <div>
        <h2>商品管理</h2>
        <p>维护商城商品信息，支持搜索、上下架与库存调整。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchContent"
          type="text"
          class="search-input"
          placeholder="按商品名称关键字搜索"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn ghost" @click="resetSearch">清空</button>
        <button type="button" class="btn primary" @click="openCreateDialog">
          <i class="ri-add-line"></i> 新增商品
        </button>
      </div>
    </header>

    <section class="card">
      <table class="product-table">
        <thead>
          <tr>
            <th style="width: 80px;">商品ID</th>
            <th style="width: 80px;">图片</th>
            <th>名称</th>
            <th style="width: 240px;">简介</th>
            <th style="width: 100px;">分类</th>
            <th style="width: 100px;">价格</th>
            <th style="width: 80px;">库存</th>
            <th style="width: 80px;">销量</th>
            <th style="width: 80px;">评论</th>
            <th style="width: 160px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="10" class="loading-cell">商品数据加载中...</td>
          </tr>
          <tr v-else-if="!products.length">
            <td colspan="10" class="empty-cell">暂无商品，请先创建。</td>
          </tr>
          <tr v-for="product in products" :key="product.id">
            <td class="center">{{ product.id }}</td>
            <td class="center">
              <img :src="resolveImage(product.productPic)" alt="商品图片" class="thumbnail" />
            </td>
            <td>{{ product.productName }}</td>
            <td class="ellipsis" :title="product.info">{{ product.info }}</td>
            <td class="center">{{ categoryName(product.categoryId) }}</td>
            <td class="center price">￥{{ formatPrice(product.price) }}</td>
            <td class="center">{{ product.stock }}</td>
            <td class="center">{{ product.sellNum ?? 0 }}</td>
            <td class="center">{{ product.commentNum ?? 0 }}</td>
            <td class="center">
              <div class="row-actions">
                <button type="button" class="btn ghost" @click="openEditDialog(product)">编辑</button>
                <button type="button" class="btn danger ghost" @click="handleDelete(product)">
                  删除
                </button>
              </div>
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
        <span>共 {{ pageInfo.total || 0 }} 件商品</span>
      </footer>
    </section>

    <ProductDialog
      v-if="showDialog"
      :loading="submitting"
      :product="currentProduct"
      :categories="categories"
      @close="closeDialog"
      @submit="handleSubmit"
    />
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import {
  createProduct,
  deleteProduct,
  fetchProductCategories,
  fetchProducts,
  updateProduct,
} from '@/apis/admin';
import ProductDialog from '@/views/admin/components/ProductDialog.vue';

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const products = ref([]);
const categories = ref([]);
const loading = ref(false);
const submitting = ref(false);
const searchContent = ref('');
const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  pages: 0,
  total: 0,
});

const showDialog = ref(false);
const currentProduct = ref(null);

const resolveImage = (path) => {
  if (!path) {
    return 'https://via.placeholder.com/80x80.png?text=Product';
  }
  if (/^https?:\/\//i.test(path)) {
    return path;
  }
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const formatPrice = (value) => {
  if (value === null || value === undefined) return '0.00';
  const num = Number(value);
  return Number.isNaN(num) ? value : num.toFixed(2);
};

const categoryName = (categoryId) => {
  const target = categories.value.find((item) => item.id === categoryId);
  return target?.categoryName || '-';
};

const loadCategories = async () => {
  try {
    const res = await fetchProductCategories();
    if (res?.code === 0) {
      categories.value = res.data || [];
    }
  } catch (error) {
    console.warn('加载商品分类失败', error);
  }
};

const loadProducts = async () => {
  loading.value = true;
  try {
    const res = await fetchProducts({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      content: searchContent.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      const { pageInfo: pageData, categories: categoryList } = res.data;
      products.value = pageData?.list || [];
      Object.assign(pageInfo, {
        pageNum: pageData?.pageNum || 1,
        pageSize: pageData?.pageSize || pageInfo.pageSize,
        pages: pageData?.pages || 0,
        total: pageData?.total || 0,
      });
      if (Array.isArray(categoryList) && categoryList.length) {
        categories.value = categoryList;
      }
    } else {
      products.value = [];
      window.alert(res?.msg || '加载商品列表失败');
    }
  } catch (error) {
    console.error('加载商品列表失败', error);
    window.alert('网络错误，加载商品列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadProducts();
};

const resetSearch = () => {
  searchContent.value = '';
  handleSearch();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadProducts();
};

const openCreateDialog = () => {
  currentProduct.value = null;
  showDialog.value = true;
};

const openEditDialog = (product) => {
  currentProduct.value = { ...product };
  showDialog.value = true;
};

const closeDialog = () => {
  if (submitting.value) return;
  showDialog.value = false;
};

const handleSubmit = async (payload) => {
  submitting.value = true;
  try {
    let res;
    if (payload.id) {
      res = await updateProduct(payload.id, payload);
    } else {
      res = await createProduct(payload);
    }
    if (res?.code === 0) {
      window.alert(res.msg || (payload.id ? '商品更新成功' : '商品创建成功'));
      showDialog.value = false;
      await loadProducts();
    } else {
      window.alert(res?.msg || '操作失败，请检查字段或稍后重试');
    }
  } catch (error) {
    console.error('提交商品失败', error);
    window.alert('网络错误，提交商品失败');
  } finally {
    submitting.value = false;
  }
};

const handleDelete = async (product) => {
  if (!product?.id) return;
  if (!window.confirm(`确认删除商品「${product.productName || product.id}」？`)) {
    return;
  }
  try {
    const res = await deleteProduct(product.id);
    if (res?.code === 0) {
      window.alert(res.msg || '删除成功');
      if (products.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadProducts();
    } else {
      window.alert(res?.msg || '删除商品失败');
    }
  } catch (error) {
    console.error('删除商品失败', error);
    window.alert('网络错误，删除商品失败');
  }
};

onMounted(async () => {
  await loadCategories();
  loadProducts();
});
</script>

<style scoped>
.product-manage {
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
  width: 240px;
  height: 38px;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  padding: 0 14px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-input:focus {
  border-color: #38bdf8;
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.25);
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

.btn.primary {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: #fff;
}

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
}

.btn.ghost {
  background: rgba(59, 130, 246, 0.12);
  color: #1d4ed8;
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
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.12);
  padding: 24px;
}

.product-table {
  width: 100%;
  border-collapse: collapse;
}

.product-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.product-table th,
.product-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.product-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.6);
}

.center {
  text-align: center;
}

.price {
  font-weight: 600;
  color: #047857;
}

.thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 12px;
  border: 2px solid rgba(129, 140, 248, 0.35);
}

.ellipsis {
  max-width: 240px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.row-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
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

@media (max-width: 1080px) {
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

