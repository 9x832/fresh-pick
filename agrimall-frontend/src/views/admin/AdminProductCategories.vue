<template>
  <section class="category-manage">
    <header class="page-header">
      <div>
        <h2>商品分类管理</h2>
        <p>维护商城商品分类，便于前台展示与检索。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchContent"
          type="text"
          class="search-input"
          placeholder="按分类名称搜索"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn ghost" @click="resetSearch">清空</button>
        <button type="button" class="btn primary" @click="openCreateDialog">
          <i class="ri-add-line"></i> 新增分类
        </button>
      </div>
    </header>

    <section class="card">
      <table class="category-table">
        <thead>
          <tr>
            <th style="width: 100px;">分类ID</th>
            <th>分类名称</th>
            <th style="width: 200px;">创建时间</th>
            <th style="width: 200px;">更新时间</th>
            <th style="width: 140px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="loading-cell">分类数据加载中...</td>
          </tr>
          <tr v-else-if="!categories.length">
            <td colspan="5" class="empty-cell">暂无分类，点击右上角新增。</td>
          </tr>
          <tr v-for="category in categories" :key="category.id">
            <td class="center">{{ category.id }}</td>
            <td class="center">{{ category.categoryName }}</td>
            <td class="center">{{ formatDate(category.createTime) }}</td>
            <td class="center">{{ formatDate(category.updateTime) }}</td>
            <td class="center">
              <div class="row-actions">
                <button type="button" class="btn ghost" @click="openEditDialog(category)">编辑</button>
                <button type="button" class="btn danger ghost" @click="handleDelete(category)">
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
        <span>共 {{ pageInfo.total || 0 }} 个分类</span>
      </footer>
    </section>

    <div v-if="showDialog" class="dialog-backdrop">
      <div class="dialog">
        <header>
          <h3>{{ currentCategory?.id ? '编辑分类' : '新增分类' }}</h3>
        </header>
        <section class="dialog-body">
          <label class="form-label" for="category-name">分类名称</label>
          <input
            id="category-name"
            v-model.trim="form.categoryName"
            type="text"
            maxlength="16"
            placeholder="请输入分类名称（1-16 字）"
          />
        </section>
        <footer class="dialog-footer">
          <button type="button" class="btn secondary" @click="closeDialog" :disabled="submitting">
            取消
          </button>
          <button type="button" class="btn primary" @click="submitCategory" :disabled="submitting">
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </footer>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import {
  createProductCategory,
  deleteProductCategory,
  fetchProductCategoryList,
  updateProductCategory,
} from '@/apis/admin';

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
const currentCategory = ref(null);
const form = reactive({
  categoryName: '',
});

const formatDate = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const mi = String(date.getMinutes()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
};

const loadCategories = async () => {
  loading.value = true;
  try {
    const res = await fetchProductCategoryList({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      content: searchContent.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      categories.value = res.data.list || [];
      Object.assign(pageInfo, {
        pageNum: res.data.pageNum || 1,
        pageSize: res.data.pageSize || pageInfo.pageSize,
        pages: res.data.pages || 0,
        total: res.data.total || 0,
      });
    } else {
      categories.value = [];
      window.alert(res?.msg || '加载分类列表失败');
    }
  } catch (error) {
    console.error('加载分类列表失败', error);
    window.alert('网络错误，加载分类列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadCategories();
};

const resetSearch = () => {
  searchContent.value = '';
  handleSearch();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadCategories();
};

const openCreateDialog = () => {
  currentCategory.value = null;
  form.categoryName = '';
  showDialog.value = true;
};

const openEditDialog = (category) => {
  currentCategory.value = category;
  form.categoryName = category?.categoryName || '';
  showDialog.value = true;
};

const closeDialog = () => {
  if (submitting.value) return;
  showDialog.value = false;
};

const submitCategory = async () => {
  if (!form.categoryName) {
    window.alert('分类名称不能为空');
    return;
  }
  submitting.value = true;
  try {
    let res;
    if (currentCategory.value?.id) {
      res = await updateProductCategory(currentCategory.value.id, { categoryName: form.categoryName });
    } else {
      res = await createProductCategory({ categoryName: form.categoryName });
    }
    if (res?.code === 0) {
      window.alert(res.msg || '操作成功');
      showDialog.value = false;
      await loadCategories();
    } else {
      window.alert(res?.msg || '操作失败，请稍后重试');
    }
  } catch (error) {
    console.error('提交分类失败', error);
    window.alert('网络错误，提交分类失败');
  } finally {
    submitting.value = false;
  }
};

const handleDelete = async (category) => {
  if (!category?.id) return;
  if (!window.confirm(`确认删除分类「${category.categoryName}」吗？`)) return;
  try {
    const res = await deleteProductCategory(category.id);
    if (res?.code === 0) {
      window.alert(res.msg || '删除成功');
      if (categories.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadCategories();
    } else {
      window.alert(res?.msg || '删除分类失败');
    }
  } catch (error) {
    console.error('删除分类失败', error);
    window.alert('网络错误，删除分类失败');
  }
};

onMounted(() => {
  loadCategories();
});
</script>

<style scoped>
.category-manage {
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
  border-color: #60a5fa;
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.25);
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
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
}

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
}

.btn.ghost {
  background: rgba(251, 146, 60, 0.15);
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

.category-table {
  width: 100%;
  border-collapse: collapse;
}

.category-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.category-table th,
.category-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.category-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.6);
}

.center {
  text-align: center;
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

.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  padding: 24px;
}

.dialog {
  width: min(420px, 100%);
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.25);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog header h3 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.dialog input {
  height: 38px;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  padding: 0 12px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.dialog input:focus {
  border-color: #f97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.25);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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

