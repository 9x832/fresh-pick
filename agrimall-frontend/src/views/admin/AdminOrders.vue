<template>
  <section class="order-manage">
    <header class="page-header">
      <div>
        <h2>订单管理</h2>
        <p>查看并处理商城订单，支持状态更新与删除。</p>
      </div>
      <div class="actions">
        <input
          v-model.trim="searchOrderNo"
          type="text"
          class="search-input"
          placeholder="输入订单号搜索（支持模糊）"
          @keyup.enter="handleSearch"
        />
        <button type="button" class="btn secondary" @click="handleSearch">搜索</button>
        <button type="button" class="btn ghost" @click="resetSearch">清空</button>
      </div>
    </header>

    <section class="card">
      <table class="order-table">
        <thead>
          <tr>
            <th style="width: 120px;">订单号</th>
            <th style="width: 120px;">用户</th>
            <th style="width: 120px;">金额（元）</th>
            <th style="width: 140px;">状态</th>
            <th style="width: 200px;">创建时间</th>
            <th style="width: 200px;">更新时间</th>
            <th style="width: 200px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="loading-cell">订单数据加载中...</td>
          </tr>
          <tr v-else-if="!orders.length">
            <td colspan="7" class="empty-cell">暂无订单记录</td>
          </tr>
          <tr v-for="order in orders" :key="order.id">
            <td class="center">{{ order.orderNo }}</td>
            <td class="center">{{ order.user?.username || order.userId }}</td>
            <td class="center price">￥{{ formatPrice(order.totalPrice) }}</td>
            <td class="center">
              <span class="state-tag" :class="`state-${order.state}`">{{ stateText(order.state) }}</span>
            </td>
            <td class="center">{{ formatDate(order.createTime) }}</td>
            <td class="center">{{ formatDate(order.updateTime) }}</td>
            <td class="center actions-cell">
              <button type="button" class="action-btn" @click="openDetail(order)">详情</button>
              <button type="button" class="action-btn" @click="openStateDialog(order)">修改状态</button>
              <button type="button" class="action-btn danger" @click="handleDelete(order)">
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
        <span>共 {{ pageInfo.total || 0 }} 个订单</span>
      </footer>
    </section>

    <OrderDetailDialog
      v-if="showDetail"
      :order="detailData.order"
      :items="detailData.items"
      @close="showDetail = false"
    />

    <div v-if="showStateDialog" class="dialog-backdrop">
      <div class="dialog small">
        <header>
          <h3>更新订单状态</h3>
        </header>
        <section class="dialog-body">
          <p>订单号：{{ targetOrder?.orderNo }}</p>
          <label class="form-label" for="order-state">请选择新状态</label>
          <select id="order-state" v-model.number="stateForm.state">
            <option
              v-for="option in ADMIN_STATE_OPTIONS"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </option>
          </select>
        </section>
        <footer class="dialog-footer">
          <button type="button" class="btn secondary" @click="closeStateDialog" :disabled="submitting">
            取消
          </button>
          <button type="button" class="btn primary" @click="submitState" :disabled="submitting">
            {{ submitting ? '提交中...' : '确认更新' }}
          </button>
        </footer>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import {
  deleteOrder,
  fetchOrderDetail,
  fetchOrders,
  updateOrderState,
} from '@/apis/admin';
import OrderDetailDialog from '@/views/admin/components/OrderDetailDialog.vue';

const orders = ref([]);
const loading = ref(false);
const submitting = ref(false);
const searchOrderNo = ref('');
const pageInfo = reactive({
  pageNum: 1,
  pageSize: 10,
  pages: 0,
  total: 0,
});

const showDetail = ref(false);
const detailData = reactive({
  order: null,
  items: [],
});

const showStateDialog = ref(false);
const targetOrder = ref(null);
const stateForm = reactive({
  state: 0,
});

const ADMIN_STATE_OPTIONS = [
  { value: 0, label: '未支付', disabled: true },
  { value: 1, label: '已支付，待发货' },
  { value: 5, label: '已发货' },
  { value: 3, label: '已送达，待签收' },
  { value: 4, label: '已签收' },
];

const stateOptions = [
  { value: 0, label: '未支付' },
  { value: 1, label: '已支付，待发货' },
  { value: 5, label: '已发货' },
  { value: 3, label: '已送达，待签收' },
  { value: 4, label: '已签收' },
  { value: 2, label: '已取消' },
];

const stateText = (state) => {
  const found = stateOptions.find((item) => item.value === state);
  return found ? found.label : `状态${state}`;
};

const formatPrice = (value) => {
  if (value === null || value === undefined) return '0.00';
  const num = Number(value);
  return Number.isNaN(num) ? value : num.toFixed(2);
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

const loadOrders = async () => {
  loading.value = true;
  try {
    const res = await fetchOrders({
      pageNum: pageInfo.pageNum,
      pageSize: pageInfo.pageSize,
      orderNo: searchOrderNo.value || undefined,
    });
    if (res?.code === 0 && res.data) {
      const pageData = res.data;
      orders.value = pageData.list || [];
      Object.assign(pageInfo, {
        pageNum: pageData.pageNum || 1,
        pageSize: pageData.pageSize || pageInfo.pageSize,
        pages: pageData.pages || 0,
        total: pageData.total || 0,
      });
    } else {
      orders.value = [];
      window.alert(res?.msg || '加载订单列表失败');
    }
  } catch (error) {
    console.error('加载订单列表失败', error);
    window.alert('网络错误，加载订单列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pageInfo.pageNum = 1;
  loadOrders();
};

const resetSearch = () => {
  searchOrderNo.value = '';
  handleSearch();
};

const gotoPage = (page) => {
  if (page < 1) return;
  if (pageInfo.pages && page > pageInfo.pages) return;
  pageInfo.pageNum = page;
  loadOrders();
};

const openDetail = async (order) => {
  showDetail.value = true;
  detailData.order = null;
  detailData.items = [];
  try {
    const res = await fetchOrderDetail(order.id);
    if (res?.code === 0 && res.data) {
      detailData.order = res.data.order;
      detailData.items = res.data.items || [];
    } else {
      window.alert(res?.msg || '加载订单详情失败');
      showDetail.value = false;
    }
  } catch (error) {
    console.error('加载订单详情失败', error);
    window.alert('网络错误，加载订单详情失败');
    showDetail.value = false;
  }
};

const openStateDialog = (order) => {
  targetOrder.value = order;
  stateForm.state = order.state;
  showStateDialog.value = true;
};

const closeStateDialog = () => {
  if (submitting.value) return;
  showStateDialog.value = false;
};

const submitState = async () => {
  if (!targetOrder.value?.id) return;
  submitting.value = true;
  try {
    const res = await updateOrderState(targetOrder.value.id, stateForm.state);
    if (res?.code === 0) {
      window.alert(res.msg || '订单状态更新成功');
      showStateDialog.value = false;
      await loadOrders();
    } else {
      window.alert(res?.msg || '更新订单状态失败');
    }
  } catch (error) {
    console.error('更新订单状态失败', error);
    window.alert('网络错误，更新订单状态失败');
  } finally {
    submitting.value = false;
  }
};

const handleDelete = async (order) => {
  if (!order?.id) return;
  if (!window.confirm(`确认删除订单「${order.orderNo}」吗？`)) return;
  try {
    const res = await deleteOrder(order.id);
    if (res?.code === 0) {
      window.alert(res.msg || '订单删除成功');
      if (orders.value.length === 1 && pageInfo.pageNum > 1) {
        pageInfo.pageNum -= 1;
      }
      await loadOrders();
    } else {
      window.alert(res?.msg || '删除订单失败');
    }
  } catch (error) {
    console.error('删除订单失败', error);
    window.alert('网络错误，删除订单失败');
  }
};

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.order-manage {
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
  border-color: #22d3ee;
  box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.25);
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
  background: linear-gradient(135deg, #22d3ee, #0ea5e9);
  color: #fff;
}

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
}

.btn.ghost {
  background: rgba(14, 165, 233, 0.15);
  color: #0c4a6e;
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

.order-table {
  width: 100%;
  border-collapse: collapse;
}

.order-table thead {
  background: rgba(226, 232, 240, 0.5);
}

.order-table th,
.order-table td {
  padding: 12px 14px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.order-table tbody tr:hover {
  background: rgba(240, 249, 255, 0.6);
}

.center {
  text-align: center;
}

.price {
  font-weight: 600;
  color: #15803d;
}

.state-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: #155e75;
  background: rgba(14, 165, 233, 0.1);
}

.state-tag.state-0 {
  color: #a855f7;
  background: rgba(168, 85, 247, 0.12);
}

.state-tag.state-1 {
  color: #f97316;
  background: rgba(249, 115, 22, 0.15);
}

.state-tag.state-5,
.state-tag.state-3 {
  color: #0ea5e9;
  background: rgba(14, 165, 233, 0.15);
}

.state-tag.state-4 {
  color: #16a34a;
  background: rgba(22, 163, 74, 0.15);
}

.state-tag.state-2 {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.15);
}

.actions-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.action-btn {
  min-width: 88px;
  height: 34px;
  border-radius: 999px;
  border: none;
  padding: 0 18px;
  background: rgba(59, 130, 246, 0.12);
  color: #1d4ed8;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.1s ease;
}

.action-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  transform: translateY(-1px);
}

.action-btn.danger {
  background: rgba(248, 113, 113, 0.18);
  color: #b91c1c;
}

.action-btn.danger:hover {
  background: rgba(248, 113, 113, 0.3);
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
  width: min(480px, 100%);
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.25);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog.small {
  width: min(420px, 100%);
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
  color: #475569;
}

.dialog select {
  height: 38px;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  padding: 0 12px;
  outline: none;
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

