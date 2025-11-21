<template>
  <div v-if="!loading" class="user-center-layout">
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
            <span class="name">我的订单</span>
            <p class="desc">可查看订单详情、支付状态与常规操作</p>
          </div>

          <div class="orders-summary">
            <article class="summary-card overview">
              <header>
                <span class="eyebrow">今日是</span>
                <strong>{{ weekDay }}</strong>
                <span class="date">{{ dateStr }} · 第 {{ day }} 日</span>
              </header>
              <div class="metric">
                <span class="label">全部订单</span>
                <span class="value">{{ summaryCards.find((c) => c.code === null)?.count || 0 }}</span>
              </div>
              <p class="tips">未支付订单请及时完成付款，系统将定期自动清理过期订单。</p>
            </article>

            <div class="summary-buttons">
              <button
                v-for="card in summaryCards"
                :key="card.label"
                type="button"
                class="summary-chip"
                :data-variant="card.variant"
                :class="{ active: card.code === activeState || (card.code === null && activeState === null) }"
                @click="setActiveState(card.code)"
              >
                <span class="chip-label">{{ card.label }}</span>
                <span class="chip-count">{{ card.count || 0 }}</span>
              </button>
            </div>
          </div>

          <div class="card tips-card">
            <h3>使用提示</h3>
            <ul>
              <li>可筛选订单状态，快速定位需要处理的订单。</li>
              <li>支付成功后可在订单详情中查看配送进度。</li>
            </ul>
          </div>
          <div v-if="orders.length" class="order_list">
            <article class="order_entry" v-for="order in orders" :key="order.id">
              <header class="order_entry__header">
                <div>
                  <span class="order_no">订单号：{{ order.orderNo }}</span>
                  <span class="order_time">{{ order.createTime }}</span>
                  <p class="order_remark">留言：{{ order.remark || '无' }}</p>
                </div>
                <div class="order_state">
                  <span class="order_amount">￥{{ formatPrice(order.totalPrice) }}</span>
                  <span class="status_tag" :data-type="stateVariant(order.state)">
                    {{ stateText(order.state) }}
                  </span>
                </div>
              </header>

              <div class="order_entry__items">
                <RouterLink
                  v-for="item in order.orderItemList"
                  :key="item.productId"
                  :to="`/home/product/detail/${item.productId}`"
                  class="order_item"
                >
                  <img :src="normalizeImage(item.productPic)" :alt="item.productName" loading="lazy" />
                  <div>
                    <h3>{{ item.productName }}</h3>
                    <p>￥{{ formatPrice(item.productPrice) }} × {{ item.quantity }}</p>
                  </div>
                </RouterLink>
              </div>

              <footer class="order_entry__actions">
                <button
                  v-if="order.state === 0"
                  type="button"
                  class="btn primary"
                  @click="startPayment(order)"
                >
                  前往支付
                </button>
                <button
                  v-if="order.state === 0"
                  type="button"
                  class="btn outline"
                  @click="cancelUnpaidOrder(order)"
                >
                  取消订单
                </button>
                <button type="button" class="btn subtle" @click="openDetail(order.id)">
                  查看详情
                </button>
              </footer>
            </article>
          </div>
          <div v-else class="empty-text">暂无订单记录。</div>

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
        </div>
      </section>
    </div>
  </div>
  <div v-else class="loading-state">正在加载订单信息...</div>

  <transition name="fade">
    <div v-if="detailVisible" class="order-modal">
      <div class="modal-mask" @click="closeDetail"></div>
      <div class="modal-card large">
        <header>
          <h2>订单详情</h2>
          <button type="button" class="close" @click="closeDetail">×</button>
        </header>
        <div v-if="detailLoading" class="modal-loading">正在加载详情...</div>
        <div v-else-if="detailOrder" class="modal-content">
          <section class="order-info">
            <div>
              <span class="label">订单号</span>
              <span class="value">{{ detailOrder.orderNo }}</span>
            </div>
            <div>
              <span class="label">下单时间</span>
              <span class="value">{{ detailOrder.createTime }}</span>
            </div>
            <div>
              <span class="label">订单状态</span>
              <span class="value">{{ stateText(detailOrder.state) }}</span>
            </div>
          </section>

          <section v-if="detailOrder.address" class="order-address">
            <h3>收货信息</h3>
            <ul>
              <li>收货人：{{ detailOrder.address.receiverName }}</li>
              <li>联系电话：{{ detailOrder.address.receiverPhone }}</li>
              <li>收货地址：{{ detailOrder.address.receiverAddress }}</li>
            </ul>
          </section>

          <section class="order-items">
            <h3>商品清单</h3>
            <ul>
              <li v-for="item in detailOrder.orderItemList" :key="item.productId">
                <img :src="normalizeImage(item.productPic)" :alt="item.productName" />
                <div>
                  <p>{{ item.productName }}</p>
                  <span>￥{{ formatPrice(item.productPrice) }} × {{ item.quantity }}</span>
                </div>
                <strong>￥{{ formatPrice(item.productPrice * (item.quantity || 1)) }}</strong>
              </li>
            </ul>
          </section>

          <section v-if="[5, 3, 4].includes(detailOrder.state)" class="order-delivery">
            <h3>配送进度</h3>
            <ul>
              <li v-for="(event, idx) in deliveryTimeline()" :key="idx">
                <span class="dot"></span>
                <div>
                  <strong>{{ event.title }}</strong>
                  <p>{{ event.desc }}</p>
                  <small>{{ event.time }}</small>
                </div>
              </li>
            </ul>
          </section>
        </div>
      </div>
    </div>
  </transition>

  <transition name="fade">
    <div v-if="paymentVisible" class="payment-modal">
      <div class="modal-mask" @click="closePayment"></div>
      <div class="modal-card">
        <header>
          <h2>订单支付</h2>
          <button type="button" class="close" @click="closePayment">×</button>
        </header>
        <section class="modal-body">
          <p class="amount">￥{{ formatPrice(payingOrder?.totalPrice || 0) }}</p>
          <p class="order-info">订单号：{{ payingOrder?.orderNo }}</p>
          <div class="channel-selector">
            <button
              v-for="channel in PAYMENT_CHANNELS"
              :key="channel.code"
              type="button"
              :class="['channel', { active: channel.code === selectedChannel }]"
              @click="selectedChannel = channel.code"
            >
              <span class="name">{{ channel.name }}</span>
              <span class="description">{{ channel.desc }}</span>
            </button>
          </div>
          <p class="desc">支付剩余时间：<strong>{{ Math.floor(paymentCountdown / 60) }}分 {{ paymentCountdown % 60 }}秒</strong></p>
        </section>
        <footer class="modal-actions">
          <button type="button" class="ghost" :disabled="processingPay" @click="closePayment">
            取消
          </button>
          <button type="button" class="primary" :disabled="processingPay" @click="confirmPayment">
            {{ processingPay ? '支付处理中…' : '确认支付' }}
          </button>
        </footer>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { computed, onMounted, onBeforeUnmount, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchUserOrders, fetchOrderDetail, updateOrderState } from '@/apis/home';
import '@/views/home/userCenter.css';

const router = useRouter();
const route = useRoute();
const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const state = reactive({
  orders: [],
  pageNum: 1,
  pageSize: 5,
  pages: 1,
  loading: false,
  totalCount: 0,
});

const stateCounts = reactive({});
const activeState = ref(null);

const detailVisible = ref(false);
const detailLoading = ref(false);
const detailOrder = ref(null);

const paymentVisible = ref(false);
const processingPay = ref(false);
const paymentCountdown = ref(600);
const paymentTimer = ref(null);
const payingOrder = ref(null);
const selectedChannel = ref('wechat');

const STATE_DEFS = [
  { code: null, label: '全部订单', variant: 'all' },
  { code: 0, label: '待支付', variant: 'pending' },
  { code: 1, label: '待发货', variant: 'paid' },
  { code: 5, label: '配送中', variant: 'shipping' },
  { code: 3, label: '待签收', variant: 'shipping' },
  { code: 4, label: '已签收', variant: 'done' },
  { code: 2, label: '已取消', variant: 'cancel' },
];

const orders = computed(() => state.orders);
const pageNum = computed(() => state.pageNum);
const pages = computed(() => state.pages);
const loading = computed(() => state.loading);
const isActive = (path) => route.path === path;

const weekMap = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
const now = new Date();
const day = now.getDate();
const weekDay = weekMap[now.getDay()];
const dateStr = `${now.getFullYear()}年${now.getMonth() + 1}月`;

const summaryCards = computed(() =>
  STATE_DEFS.map((def) => {
    const key = def.code === null ? 'all' : String(def.code);
    const count = def.code === null ? state.totalCount : stateCounts[key] ?? 0;
    return { ...def, count };
  }),
);

const formatPrice = (value) => Number(value || 0).toFixed(2);

const PAYMENT_CHANNELS = [
  {
    code: 'wechat',
    name: '微信支付',
    desc: '扫码支付，支持微信钱包余额',
  },
  {
    code: 'alipay',
    name: '支付宝',
    desc: '推荐支付宝用户，支持花呗',
  },
  {
    code: 'card',
    name: '银行卡快捷支付',
    desc: '支持主流借记卡、信用卡',
  },
];

const resetPaymentState = () => {
  selectedChannel.value = 'wechat';
  processingPay.value = false;
  paymentCountdown.value = 600;
  payingOrder.value = null;
};

const normalizeImage = (path) => {
  if (!path) return '';
  if (/^https?:\/\//.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const deliveryTimeline = () => {
  if (!detailOrder.value) return [];
  const base = detailOrder.value.createTime || '';
  const events = [
    { title: '订单创建', time: base, desc: '系统已生成订单，等待商家处理' },
    { title: '仓库备货', time: '预计 30 分钟内完成', desc: '仓库正在为您挑选新鲜食材' },
    { title: '配送中', time: '预计 2 小时内送达', desc: '骑手已取货，正前往您的收货地址' },
    { title: '送达待签收', time: '配送员即将抵达', desc: '请保持电话畅通，方便联系' },
  ];
  return events;
};

const openDetail = async (orderId) => {
  detailVisible.value = true;
  detailLoading.value = true;
  try {
    const res = await fetchOrderDetail(orderId);
    if (res?.code === 0 && res.data?.order) {
      detailOrder.value = res.data.order;
      if (res.data.address) {
        detailOrder.value.address = res.data.address;
      }
      detailOrder.value.orderItemList = detailOrder.value.orderItemList || [];
    } else {
      window.alert(res?.msg || '订单不存在或已失效');
      detailVisible.value = false;
    }
  } catch (error) {
    console.error('加载订单详情失败', error);
    window.alert('加载订单详情失败，请稍后再试');
    detailVisible.value = false;
  } finally {
    detailLoading.value = false;
  }
};

const closeDetail = () => {
  if (detailLoading.value) return;
  detailVisible.value = false;
  detailOrder.value = null;
};

const stateText = (val) => {
  switch (val) {
    case 0:
      return '待支付';
    case 1:
      return '待发货';
    case 2:
      return '已取消';
    case 3:
      return '待签收';
    case 4:
      return '已签收';
    case 5:
      return '配送中';
    default:
      return '未知状态';
  }
};

const stateVariant = (val) => {
  if (val === null || val === undefined) {
    return 'all';
  }
  switch (val) {
    case 0:
      return 'pending';
    case 1:
      return 'paid';
    case 3:
      return 'shipping';
    case 4:
      return 'done';
    case 5:
      return 'shipping';
    case 2:
      return 'cancel';
    default:
      return 'normal';
  }
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

const loadOrders = async () => {
  state.loading = true;
  try {
    const res = await fetchUserOrders({
      pageNum: state.pageNum,
      pageSize: state.pageSize,
      state: activeState.value ?? undefined,
    });
    if (res?.code === 0 && res.data) {
      const { pageInfo } = res.data;
      state.orders = (pageInfo?.list || []).map((order) => ({
        ...order,
        orderItemList: order.orderItemList || [],
      }));
      state.pages = pageInfo?.pages || 1;
      state.pageNum = pageInfo?.pageNum || 1;
      state.totalCount = res.data.totalCount ?? state.orders.length;
      Object.keys(stateCounts).forEach((key) => delete stateCounts[key]);
      Object.entries(res.data.stateCount || {}).forEach(([key, value]) => {
        stateCounts[key] = value;
      });
      if (res.data.activeState !== undefined) {
        activeState.value = res.data.activeState;
      }
    } else if (res?.code === -6) {
      window.alert('请先登录后查看订单');
      router.push('/login');
    } else {
      state.orders = [];
      state.pages = 1;
    }
  } catch (error) {
    console.error('加载订单列表失败', error);
    state.orders = [];
    state.pages = 1;
  } finally {
    state.loading = false;
  }
};

const gotoPage = (page) => {
  if (page < 1 || page > state.pages || page === state.pageNum) return;
  state.pageNum = page;
  loadOrders();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const setActiveState = (code) => {
  const next = code === null ? null : Number(code);
  if (activeState.value === next) return;
  activeState.value = next;
  state.pageNum = 1;
  loadOrders();
};

const startPayment = (order) => {
  payingOrder.value = order;
  paymentVisible.value = true;
  selectedChannel.value = 'wechat';
  paymentCountdown.value = 600;
  processingPay.value = false;
  stopPaymentTimer();
  paymentTimer.value = setInterval(() => {
    if (paymentCountdown.value <= 0) {
      stopPaymentTimer();
      window.alert('支付超时，订单仍未支付');
      paymentVisible.value = false;
      payingOrder.value = null;
    } else {
      paymentCountdown.value -= 1;
    }
  }, 1000);
};

const stopPaymentTimer = () => {
  if (paymentTimer.value) {
    clearInterval(paymentTimer.value);
    paymentTimer.value = null;
  }
};

const closePayment = () => {
  if (processingPay.value) return;
  paymentVisible.value = false;
  stopPaymentTimer();
  resetPaymentState();
};

const confirmPayment = async () => {
  if (!payingOrder.value) return;
  processingPay.value = true;
  try {
    const res = await updateOrderState(payingOrder.value.id, 1);
    if (res?.code === 0) {
      window.alert('支付成功，订单状态已更新');
      paymentVisible.value = false;
      await loadOrders();
    } else {
      window.alert(res?.msg || '支付失败，请稍后再试');
    }
  } catch (error) {
    console.error('支付失败', error);
    window.alert('网络错误，支付失败');
  } finally {
    processingPay.value = false;
    stopPaymentTimer();
    resetPaymentState();
  }
};

const cancelUnpaidOrder = async (order) => {
  if (!window.confirm('确定取消该订单吗？')) return;
  try {
    const res = await updateOrderState(order.id, 2);
    if (res?.code === 0) {
      window.alert('订单已取消');
      await loadOrders();
    } else {
      window.alert(res?.msg || '取消失败，请稍后再试');
    }
  } catch (error) {
    console.error('取消订单失败', error);
    window.alert('网络错误，取消失败');
  }
};


onMounted(() => {
  loadOrders();
});

onBeforeUnmount(() => {
  stopPaymentTimer();
});
</script>

<style scoped>
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

.order_list {
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vw, 24px);
}

.order_entry {
  border-radius: 22px;
  background: rgba(248, 250, 252, 0.6);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.18);
  padding: clamp(18px, 4vw, 26px);
  display: flex;
  flex-direction: column;
  gap: clamp(14px, 2.5vw, 22px);
}

.order_entry__header {
  display: flex;
  justify-content: space-between;
  gap: clamp(12px, 3vw, 20px);
  flex-wrap: wrap;
}

.order_no {
  font-weight: 600;
  color: #0f172a;
}

.order_time {
  margin-left: 16px;
  color: rgba(15, 23, 42, 0.55);
  font-size: 13px;
}

.order_remark {
  margin: 8px 0 0;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.6);
}

.order_state {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order_amount {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.status_tag {
  padding: 6px 16px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.03em;
  text-transform: uppercase;
  background: rgba(148, 163, 184, 0.16);
  color: rgba(15, 23, 42, 0.7);
}

.status_tag[data-type='pending'] {
  background: rgba(250, 204, 21, 0.18);
  color: #9a6400;
}

.status_tag[data-type='paid'],
.status_tag[data-type='shipping'] {
  background: rgba(59, 130, 246, 0.16);
  color: #1d4ed8;
}

.status_tag[data-type='done'] {
  background: rgba(34, 197, 94, 0.16);
  color: #15803d;
}

.status_tag[data-type='cancel'] {
  background: rgba(248, 113, 113, 0.16);
  color: #b91c1c;
}

.order_entry__summary {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  margin-bottom: clamp(12px, 2vw, 20px);
}

.order_entry__items {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: clamp(12px, 3vw, 20px);
}

.order_item {
  display: flex;
  gap: 14px;
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.16);
  text-decoration: none;
  color: inherit;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.order_item:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
}

.order_item img {
  width: 72px;
  height: 72px;
  border-radius: 14px;
  object-fit: cover;
}

.order_item h3 {
  margin: 0;
  font-size: 15px;
  color: #0f172a;
}

.order_item p {
  margin: 4px 0 0;
  font-size: 13px;
  color: rgba(71, 85, 105, 0.78);
}

.order_entry__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn {
  border: none;
  border-radius: 999px;
  padding: 9px 22px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.btn.primary {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.92), rgba(37, 99, 235, 0.92));
  color: #fff;
  box-shadow: 0 14px 26px rgba(59, 130, 246, 0.28);
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

.pagination {
  margin-top: clamp(16px, 4vw, 28px);
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
  cursor: not-allowed;
  opacity: 0.5;
}

.empty-text {
  text-align: center;
  padding: 80px 0;
  color: rgba(100, 116, 139, 0.7);
  font-size: 14px;
}

.loading-state {
  text-align: center;
  padding: 80px 0;
  color: rgba(71, 85, 105, 0.8);
}

.summary-strip {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  margin-bottom: clamp(12px, 2vw, 20px);
}

.summary-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  border-radius: 999px;
  padding: 6px 14px;
  background: rgba(248, 250, 252, 0.92);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.18);
  color: rgba(15, 23, 42, 0.72);
  font-size: 13px;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.summary-chip span {
  font-size: 12px;
  letter-spacing: 0.02em;
}

.summary-chip strong {
  font-weight: 700;
  color: #0f172a;
}

.summary-chip.active,
.summary-chip:hover {
  background: rgba(220, 252, 231, 0.92);
  box-shadow: 0 10px 18px rgba(34, 197, 94, 0.18);
  transform: translateY(-1px);
  color: #1f6b3a;
}

.summary-chip[data-variant='pending'].active strong {
  color: #d97706;
}

.summary-chip[data-variant='paid'].active strong,
.summary-chip[data-variant='shipping'].active strong {
  color: #2563eb;
}

.summary-chip[data-variant='done'].active strong {
  color: #15803d;
}

.summary-chip[data-variant='cancel'].active strong {
  color: #b91c1c;
}

.summary-chip--date {
  background: rgba(248, 248, 255, 0.72);
  cursor: default;
}

.summary-chip--date strong {
  font-size: 16px;
  color: #0f172a;
}

.summary-chip--date small {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.55);
}

@media (max-width: 960px) {
  .order_entry__header {
    flex-direction: column;
    align-items: flex-start;
  }

  .order_state {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 640px) {
  .order_entry__items {
    grid-template-columns: 1fr;
  }

  .order_item {
    flex-direction: column;
    align-items: flex-start;
  }

  .order_item img {
    width: 100%;
    height: 160px;
  }
}
</style>

