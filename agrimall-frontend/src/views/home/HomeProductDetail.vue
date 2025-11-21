<template>
  <div class="product-detail-page">
    <div v-if="product" class="detail-container">
      <section class="detail-hero">
        <div class="media-panel">
          <div class="media-card">
            <img :src="product.image" alt="product cover" />
          </div>
          <div class="meta-strip">
            <p class="meta-item">
              <span class="label">商品编码</span>
              <span class="value">{{ product.id }}</span>
            </p>
            <p class="meta-item">
              <span class="label">销量</span>
              <span class="value">{{ product.sellNum }} 箱</span>
            </p>
            <p class="meta-item">
              <span class="label">库存</span>
              <span class="value">{{ product.stock }}</span>
            </p>
          </div>
        </div>
        <div class="info-panel">
          <h1 class="title">{{ product.name }}</h1>
          <p v-if="product.subtitle" class="subtitle">{{ product.subtitle }}</p>

          <div class="stat-grid">
            <div class="stat-card price-card">
              <span class="stat-label">商城价</span>
              <span class="stat-value">￥{{ product.price }}</span>
              <span class="stat-unit">元/箱</span>
            </div>
            <div class="stat-card">
              <span class="stat-label">累计销量</span>
              <span class="stat-value">{{ product.sellNum }}</span>
              <span class="stat-unit">箱</span>
            </div>
            <div class="stat-card">
              <span class="stat-label">累计评论</span>
              <span class="stat-value">
                <strong>{{ totalComment }}</strong>
              </span>
              <span class="stat-unit">条</span>
            </div>
          </div>

          <div class="quantity-block">
            <div class="quantity-label">数量</div>
            <div class="quantity-control">
              <button type="button" @click="decrease" aria-label="减少数量">-</button>
              <input
                v-model.number="quantity"
                type="text"
                inputmode="numeric"
                @input="fixQuantity"
              />
              <button type="button" @click="increase" aria-label="增加数量">+</button>
            </div>
            <div class="quantity-stock">库存 {{ product.stock }}</div>
          </div>

          <div class="action-buttons">
            <button class="btn primary" type="button" @click.prevent="handleAddCart(product.id)">
              加入购物车
            </button>
          <button class="btn ghost" type="button" @click.prevent="handleBuyNow(product.id)">
            立即支付
          </button>
            <button class="btn outline" type="button" @click.prevent="handleAddCollect(product.id)">
              收藏商品
            </button>
          </div>
        </div>
      </section>

      <section v-if="sellRank.length" class="recommend-section">
        <header class="section-header">
          <h2>热销推荐</h2>
          <p>同类热卖商品，看看大家都在买什么</p>
        </header>
        <div class="recommend-grid">
          <RouterLink
            v-for="item in sellRank"
            :key="item.id"
            :to="`/home/product/detail/${item.id}`"
            class="recommend-card"
          >
            <div class="thumb">
              <img :src="item.image" :alt="item.name" />
            </div>
            <div class="info">
              <p class="name">{{ item.name }}</p>
              <p class="price">￥{{ item.price }}</p>
            </div>
          </RouterLink>
        </div>
      </section>

      <section class="comment-section">
        <header class="section-header">
          <h2>用户评价</h2>
          <p>共 {{ totalComment }} 条真实评价</p>
        </header>

        <div class="comment-editor">
          <textarea
            v-model.trim="commentContent"
            :maxlength="100"
            placeholder="请输入评论内容（不得超过100字）"
          ></textarea>
          <button
            class="btn primary"
            type="button"
            @click="handleSubmitComment"
          >
            发表评论
          </button>
        </div>

        <div v-if="comments.length" class="comment-list">
          <article
            v-for="(comment, index) in comments"
            :key="comment.id"
            class="comment-item"
          >
            <header class="comment-meta">
              <span class="user">{{ comment.username }}</span>
              <span class="time">{{ comment.createTime }}</span>
              <span class="index">#{{ commentStartIndex + index }}</span>
            </header>
            <p class="content">{{ comment.content }}</p>
          </article>
        </div>

        <div v-else class="empty-comment">
          暂无评论，快来发表第一条评价吧～
        </div>

        <div class="pagination" v-if="commentPages > 1">
          <button
            type="button"
            :disabled="commentPage === 1"
            @click="gotoCommentPage(commentPage - 1)"
          >
            上一页
          </button>
          <button
            v-for="page in commentPageRange"
            :key="page"
            type="button"
            :class="{ active: page === commentPage }"
            @click="gotoCommentPage(page)"
          >
            {{ page }}
          </button>
          <button
            type="button"
            :disabled="commentPage === commentPages"
            @click="gotoCommentPage(commentPage + 1)"
          >
            下一页
          </button>
        </div>
      </section>
    </div>

    <div v-else class="loading-state">正在加载商品信息...</div>
  </div>

  <transition name="fade">
    <div v-if="paymentVisible" class="payment-modal">
      <div class="modal-mask" @click="closePayment"></div>
      <div class="modal-card large">
        <header>
          <h2>订单支付</h2>
          <button type="button" class="close" @click="closePayment">×</button>
        </header>
        <section v-if="payingOrder" class="modal-summary">
          <p class="amount">￥{{ formatPrice(payingOrder.totalPrice) }}</p>
          <p class="order-info">订单号：{{ payingOrder.orderNo }}</p>
          <ul class="item-list">
            <li v-for="item in payingOrder.orderItemList" :key="item.productId">
              <span>{{ item.productName }}</span>
              <span>￥{{ formatPrice(item.productPrice) }} × {{ item.quantity }}</span>
            </li>
          </ul>
        </section>
        <section class="modal-body">
          <div class="channel-selector">
            <button
              v-for="channel in paymentChannels"
              :key="channel.code"
              type="button"
              :class="['channel', { active: channel.code === selectedChannel }]"
              @click="selectedChannel = channel.code"
            >
              <span class="name">{{ channel.name }}</span>
              <span class="description">{{ channel.desc }}</span>
            </button>
          </div>
          <p class="desc">
            支付剩余时间：
            <strong>{{ Math.floor(paymentCountdown / 60) }}分 {{ paymentCountdown % 60 }}秒</strong>
          </p>
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
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  addCollect,
  addToCart,
  deleteCartItem,
  fetchCart,
  fetchProductDetail,
  submitComment,
  generateOrder,
  submitOrder,
  fetchOrderDetail,
  fetchAddresses,
} from '@/apis/home';
import {
  calculateCartTotal,
  notifyCartCleared,
  setCartTotal,
} from '@/utils/cart';

const route = useRoute();
const router = useRouter();

const IMAGE_BASE_URL = 'http://localhost:8080';

const state = reactive({
  product: null,
  sellRank: [],
  totalComment: 0,
  comments: [],
  commentPages: 1,
  commentPage: 1,
  commentPageSize: 5,
});

const quantity = ref(1);
const commentContent = ref('');

const paymentVisible = ref(false);
const processingPay = ref(false);
const paymentCountdown = ref(600);
const paymentTimer = ref(null);
const payingOrder = ref(null);
const selectedChannel = ref('wechat');

const product = computed(() => state.product);
const sellRank = computed(() => state.sellRank);
const totalComment = computed(() => state.totalComment);
const comments = computed(() => state.comments);
const commentPages = computed(() => state.commentPages);
const commentPage = computed(() => state.commentPage);
const commentStartIndex = computed(
  () => (state.commentPage - 1) * state.commentPageSize + 1,
);

const commentPageRange = computed(() => {
  const range = [];
  const start = Math.max(1, state.commentPage - 2);
  const end = Math.min(state.commentPages, state.commentPage + 2);
  for (let i = start; i <= end; i += 1) {
    range.push(i);
  }
  return range;
});

const paymentChannels = [
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

const formatPrice = (value) => Number(value || 0).toFixed(2);

const normalizeImage = (path) => {
  if (!path) return '';
  if (/^https?:\/\//.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const mapProduct = (item) => ({
  id: item.id,
  name: item.productName,
  subtitle: item.info,
  image: normalizeImage(item.productPic),
  price: Number(item.price || 0).toFixed(2),
  stock: item.stock || 0,
  sellNum: item.sellNum || 0,
  commentNum: item.commentNum || 0,
});

const mapComments = (pageInfo) => {
  if (!pageInfo) {
    return {
      list: [],
      pages: 1,
      pageNum: 1,
      pageSize: 5,
    };
  }
  const { list = [], pages = 1, pageNum = 1, pageSize = 5 } = pageInfo;
  return {
    list: list.map((item) => ({
      id: item.id,
      username: item.user?.username || '匿名用户',
      content: item.content,
      createTime: item.createTime,
    })),
    pages,
    pageNum,
    pageSize,
  };
};

const scrollToTop = (behavior = 'auto') => {
  window.requestAnimationFrame(() => {
    window.scrollTo({ top: 0, behavior });
  });
};

const loadDetail = async (options = { scroll: true }) => {
  const productId = Number(route.params.id);
  if (!productId) {
    router.replace('/home');
    return;
  }
  try {
    const res = await fetchProductDetail(productId, {
      pageNum: state.commentPage,
      pageSize: state.commentPageSize,
    });
    if (res?.code === 0 && res.data) {
      const { product: prod, sellRank: rank, totalComment: total, pageInfo } = res.data;
      state.product = mapProduct(prod);
      state.sellRank = Array.isArray(rank)
        ? rank.slice(0, 8).map(mapProduct)
        : [];
      state.totalComment = total || 0;
      const commentInfo = mapComments(pageInfo);
      state.comments = commentInfo.list;
      state.commentPages = commentInfo.pages || 1;
      state.commentPage = commentInfo.pageNum || 1;
      state.commentPageSize = commentInfo.pageSize || 5;
      if (options.scroll) {
        scrollToTop();
      }
    } else {
      window.alert(res?.msg || '商品信息获取失败');
      router.replace('/home');
    }
  } catch (error) {
    console.error('加载商品详情失败', error);
    window.alert('网络错误，商品信息获取失败');
    router.replace('/home');
  }
};

const increase = () => {
  quantity.value += 1;
};

const decrease = () => {
  if (quantity.value > 1) {
    quantity.value -= 1;
  }
};

const fixQuantity = () => {
  if (!Number.isInteger(quantity.value) || quantity.value < 1) {
    quantity.value = 1;
  }
};

const refreshCartTotal = async () => {
  try {
    const res = await fetchCart();
    if (res?.code === 0 && res.data?.cartProductVoList) {
      setCartTotal(calculateCartTotal(res.data.cartProductVoList));
    } else if (res?.code === -6) {
      notifyCartCleared();
    }
  } catch (error) {
    console.error('刷新购物车数量失败', error);
  }
};

const createOrderFromCart = async (ids) => {
  try {
    const res = await generateOrder({ ids });
    if (res?.code === 0 && res.data) {
      return String(res.data);
    }
    if (res?.code === -6) {
      window.alert('请先登录后再下单');
      router.push('/login');
      return null;
    }
    window.alert(res?.msg || '生成订单失败，请稍后重试');
  } catch (error) {
    console.error('生成订单失败', error);
    window.alert('网络错误，生成订单失败');
  }
  return null;
};

const stopPaymentTimer = () => {
  if (paymentTimer.value) {
    clearInterval(paymentTimer.value);
    paymentTimer.value = null;
  }
};

const startPaymentTimer = () => {
  stopPaymentTimer();
  paymentTimer.value = setInterval(() => {
    if (paymentCountdown.value <= 0) {
      stopPaymentTimer();
      paymentVisible.value = false;
      resetPaymentState();
      window.alert('支付超时，订单仍为待支付状态，请在“我的订单”中继续完成支付。');
      router.push('/home/user/order');
    } else {
      paymentCountdown.value -= 1;
    }
  }, 1000);
};

const resetPaymentState = () => {
  stopPaymentTimer();
  paymentCountdown.value = 600;
  selectedChannel.value = 'wechat';
  payingOrder.value = null;
  processingPay.value = false;
};

const closePayment = async () => {
  if (processingPay.value) return;
  await clearOrderCartItems();
  paymentVisible.value = false;
  resetPaymentState();
};

const confirmPayment = async () => {
  if (!payingOrder.value) return;
  processingPay.value = true;
  try {
    const res = await submitOrder({
      remark: '',
      orderId: payingOrder.value.id,
    });
    if (res?.code === 0) {
      window.alert(res.msg || '支付成功，订单已提交');
      paymentVisible.value = false;
      resetPaymentState();
      await refreshCartTotal();
      await clearOrderCartItems();
      router.push('/home/user/order');
      return;
    }
    if (res?.code === -6) {
      window.alert('请先登录后提交订单');
      paymentVisible.value = false;
      resetPaymentState();
      router.push('/login');
      return;
    }
    window.alert(res?.msg || '支付失败，请稍后再试');
  } catch (error) {
    console.error('支付失败', error);
    window.alert('网络错误，支付失败');
  } finally {
    processingPay.value = false;
    if (!paymentVisible.value) {
      resetPaymentState();
    }
  }
};

const prepareCartForInstantPay = async (productId, desiredQuantity) => {
  let previousEntry = null;
  try {
    const cartRes = await fetchCart();
    if (cartRes?.code === 0 && cartRes.data?.cartProductVoList) {
      previousEntry = cartRes.data.cartProductVoList.find(
        (item) => Number(item.productId) === Number(productId),
      );
    }
  } catch (error) {
    console.error('获取购物车信息失败', error);
  }

  if (previousEntry) {
    try {
      await deleteCartItem(productId);
    } catch (error) {
      console.error('移除原购物车数据失败', error);
    }
  }

  try {
    const res = await addToCart({
      productId,
      quantity: desiredQuantity,
    });
    if (res?.code === 0) {
      await refreshCartTotal();
      return { success: true, previousEntry };
    }
    if (res?.code === -6) {
      window.alert('请先登录后再下单');
      router.push('/login');
      notifyCartCleared();
    } else {
      window.alert(res?.msg || '生成订单失败，请稍后再试');
    }
  } catch (error) {
    console.error('加入购物车失败', error);
    window.alert('网络错误，生成订单失败');
  }

  if (previousEntry) {
    try {
      await addToCart({
        productId,
        quantity: previousEntry.quantity,
      });
    } catch (error) {
      console.error('恢复购物车数据失败', error);
    } finally {
      await refreshCartTotal();
    }
  }

  return { success: false, previousEntry: null };
};

const restoreCartAfterFailure = async (productId, previousEntry) => {
  try {
    await deleteCartItem(productId);
  } catch (error) {
    // 如果条目已不存在，不做额外处理
  }
  if (previousEntry) {
    try {
      await addToCart({
        productId,
        quantity: previousEntry.quantity,
      });
    } catch (error) {
      console.error('恢复原购物车商品失败', error);
    }
  }
  await refreshCartTotal();
};

const ensureHasAddress = async () => {
  try {
    const res = await fetchAddresses();
    if (res?.code === 0 && Array.isArray(res.data) && res.data.length > 0) {
      return true;
    }
  } catch (error) {
    console.error('检测地址信息失败', error);
  }
  window.alert('尚未配置收货地址，请先在“地址管理”中新增地址后再下单。');
  return false;
};

const clearOrderCartItems = async () => {
  if (!payingOrder.value?.orderItemList?.length) {
    return;
  }
  for (const item of payingOrder.value.orderItemList) {
    try {
      await deleteCartItem(item.productId);
    } catch (error) {
      // ignore
    }
  }
  await refreshCartTotal();
};

const handleBuyNow = async (productId) => {
  if (!productId) return;
  const hasAddress = await ensureHasAddress();
  if (!hasAddress) return;
  const { success, previousEntry } = await prepareCartForInstantPay(productId, quantity.value);
  if (!success) return;
  let orderId;
  try {
    orderId = await createOrderFromCart(String(productId));
  } catch (error) {
    console.error('生成订单失败', error);
    window.alert('生成订单失败，请稍后重试');
    await restoreCartAfterFailure(productId, previousEntry);
    return;
  }
  if (!orderId) {
    await restoreCartAfterFailure(productId, previousEntry);
    return;
  }

  try {
    await deleteCartItem(productId);
  } catch (error) {
    console.error('移除购物车商品失败', error);
  }
  await refreshCartTotal();

  try {
    const detailRes = await fetchOrderDetail(orderId);
    if (detailRes?.code === 0 && detailRes.data?.order) {
      payingOrder.value = detailRes.data.order;
      paymentCountdown.value = 600;
      selectedChannel.value = 'wechat';
      paymentVisible.value = true;
      startPaymentTimer();
    } else {
      window.alert(detailRes?.msg || '订单信息获取失败');
      router.push('/home/user/order');
    }
  } catch (error) {
    console.error('加载订单信息失败', error);
    window.alert('网络错误，订单信息获取失败');
    router.push('/home/user/order');
  }
};

const handleAddCart = async (productId) => {
  try {
    const res = await addToCart({
      productId,
      quantity: quantity.value,
    });
    if (res?.code === 0) {
      window.alert(res.msg || '加入购物车成功');
      await refreshCartTotal();
    } else {
      window.alert(res?.msg || '加入购物车失败，请先登录');
      if (res?.code === -6) {
        router.push('/login');
        notifyCartCleared();
      }
    }
  } catch (error) {
    console.error('加入购物车失败', error);
    window.alert('网络错误，加入购物车失败');
  }
};

const handleAddCollect = async (productId) => {
  try {
    const res = await addCollect(productId);
    if (res?.code === 0) {
      window.alert(res.msg || '收藏成功');
    } else {
      window.alert(res?.msg || '收藏失败，请先登录');
      if (res?.code === -6) {
        router.push('/login');
      }
    }
  } catch (error) {
    console.error('收藏失败', error);
    window.alert('网络错误，收藏失败');
  }
};

const handleSubmitComment = async () => {
  if (!commentContent.value) {
    window.alert('评论内容不能为空');
    return;
  }
  try {
    const res = await submitComment({
      productId: product.value.id,
      content: commentContent.value,
    });
    if (res?.code === 0) {
      window.alert(res.msg || '评论成功');
      commentContent.value = '';
      state.commentPage = 1;
      await loadDetail();
    } else {
      window.alert(res?.msg || '评论失败，请先登录');
      if (res?.code === -6) {
        router.push('/login');
      }
    }
  } catch (error) {
    console.error('评论失败', error);
    window.alert('网络错误，评论失败');
  }
};

const gotoCommentPage = (page) => {
  if (page < 1 || page > state.commentPages || page === state.commentPage) {
    return;
  }
  state.commentPage = page;
  loadDetail({ scroll: false });
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

watch(
  () => route.params.id,
  () => {
    state.commentPage = 1;
    loadDetail();
  },
);

onMounted(() => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
  loadDetail();
});

onBeforeUnmount(() => {
  stopPaymentTimer();
});
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f4f9f4 0%, #ffffff 45%, #f7f8fb 100%);
  margin-top: calc(-1 * (var(--header-height) + clamp(4px, 2vw, 12px)));
  padding: clamp(24px, 6vw, 48px) clamp(16px, 6vw, 72px) clamp(56px, 8vw, 96px);
  display: flex;
  justify-content: center;
}

.detail-container {
  width: min(1200px, 100%);
  display: flex;
  flex-direction: column;
  gap: clamp(32px, 6vw, 48px);
}

.detail-hero {
  display: grid;
  grid-template-columns: minmax(280px, 1fr) minmax(320px, 1.2fr);
  gap: clamp(24px, 5vw, 48px);
  background: rgba(255, 255, 255, 0.92);
  border-radius: clamp(24px, 4vw, 32px);
  padding: clamp(28px, 5vw, 48px);
  box-shadow: 0 24px 64px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(10px);
}

.media-panel {
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vw, 24px);
}

.media-card {
  border-radius: clamp(16px, 3vw, 20px);
  overflow: hidden;
  border: 1px solid rgba(46, 125, 72, 0.12);
  background: #f6f9f7;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(16px, 4vw, 24px);
}

.media-card img {
  width: 100%;
  height: auto;
  object-fit: contain;
}

.meta-strip {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
}

.meta-item {
  padding: 12px 16px;
  border-radius: 16px;
  background: rgba(46, 125, 72, 0.08);
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #1f3d2a;
}

.meta-item .label {
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  opacity: 0.7;
}

.meta-item .value {
  font-size: 16px;
  font-weight: 600;
}

.info-panel {
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 4vw, 28px);
}

.title {
  font-size: clamp(26px, 4vw, 34px);
  font-weight: 700;
  color: #1b2b1f;
  margin: 0;
}

.subtitle {
  margin: 0;
  color: rgba(31, 61, 42, 0.72);
  font-size: clamp(15px, 2.8vw, 17px);
  line-height: 1.6;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: clamp(12px, 3vw, 20px);
}

.stat-card {
  padding: clamp(16px, 3vw, 20px);
  border-radius: 18px;
  background: rgba(46, 125, 72, 0.08);
  color: #1f3d2a;
  display: flex;
  flex-direction: column;
  gap: 8px;
  box-shadow: inset 0 0 0 1px rgba(46, 125, 72, 0.05);
}

.price-card {
  background: linear-gradient(135deg, rgba(60, 167, 92, 0.15), rgba(46, 125, 72, 0.12));
  box-shadow: 0 12px 32px rgba(46, 125, 72, 0.18);
}

.stat-label {
  font-size: 13px;
  opacity: 0.7;
  letter-spacing: 0.06em;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.stat-unit {
  font-size: 12px;
  font-weight: 500;
  opacity: 0.7;
}

.quantity-block {
  display: flex;
  align-items: center;
  gap: clamp(12px, 3vw, 20px);
  flex-wrap: wrap;
}

.quantity-label {
  font-weight: 600;
  color: #1f3d2a;
}

.quantity-control {
  display: inline-flex;
  align-items: center;
  border-radius: 14px;
  border: 1px solid rgba(46, 125, 72, 0.2);
  overflow: hidden;
}

.quantity-control button {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(46, 125, 72, 0.08);
  color: #2e7d48;
  font-size: 20px;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.quantity-control button:hover {
  background: rgba(46, 125, 72, 0.18);
}

.quantity-control input {
  width: 64px;
  height: 40px;
  border: none;
  text-align: center;
  font-size: 16px;
  color: #1f3d2a;
}

.quantity-stock {
  color: rgba(31, 61, 42, 0.65);
  font-size: 14px;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.btn {
  padding: 12px 28px;
  border-radius: 999px;
  border: none;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.btn.primary {
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  box-shadow: 0 18px 30px rgba(46, 125, 72, 0.28);
}

.btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 22px 36px rgba(46, 125, 72, 0.32);
}

.btn.outline {
  background: transparent;
  color: #2e7d48;
  border: 1px solid rgba(46, 125, 72, 0.3);
}

.btn.outline:hover {
  background: rgba(46, 125, 72, 0.08);
}

.btn.ghost {
  background: rgba(46, 125, 72, 0.08);
  color: #2e7d48;
  border: 1px solid transparent;
}

.btn.ghost:hover {
  background: rgba(46, 125, 72, 0.16);
}

.recommend-section,
.comment-section {
  background: rgba(255, 255, 255, 0.92);
  border-radius: clamp(24px, 4vw, 32px);
  padding: clamp(28px, 5vw, 44px);
  box-shadow: 0 20px 54px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 4vw, 28px);
  backdrop-filter: blur(8px);
}

.section-header h2 {
  margin: 0;
  font-size: clamp(20px, 3.5vw, 24px);
  color: #1b2b1f;
}

.section-header p {
  margin: 8px 0 0;
  color: rgba(31, 61, 42, 0.65);
  font-size: 14px;
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: clamp(16px, 3vw, 24px);
}

@media (max-width: 1024px) {
  .recommend-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .recommend-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

.recommend-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  border-radius: 18px;
  background: rgba(46, 125, 72, 0.06);
  text-decoration: none;
  color: inherit;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.recommend-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 28px rgba(46, 125, 72, 0.18);
  background: rgba(46, 125, 72, 0.12);
}

.recommend-card .thumb {
  border-radius: 14px;
  overflow: hidden;
  background: #f6f9f7;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
}

.recommend-card .thumb img {
  width: 100%;
  height: 140px;
  object-fit: contain;
}

.recommend-card .info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.recommend-card .name {
  font-weight: 600;
  color: #1f3d2a;
  line-height: 1.4;
}

.recommend-card .price {
  color: #2e7d48;
  font-weight: 700;
}

.payment-modal {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: center;
  justify-content: center;
}

.payment-modal .modal-mask {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  backdrop-filter: blur(4px);
}

.payment-modal .modal-card {
  position: relative;
  z-index: 1;
  width: min(560px, 94vw);
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.22);
  padding: clamp(24px, 4vw, 32px);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.payment-modal .modal-card.large {
  width: min(640px, 94vw);
}

.payment-modal header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.payment-modal header h2 {
  margin: 0;
  font-size: 20px;
  color: #1c2b1f;
}

.payment-modal header .close {
  border: none;
  background: transparent;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
  color: rgba(15, 23, 42, 0.5);
}

.modal-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: rgba(248, 250, 252, 0.96);
  border-radius: 18px;
  padding: 16px 20px;
}

.modal-summary .amount {
  margin: 0;
  font-size: clamp(30px, 5vw, 40px);
  font-weight: 700;
  color: #2e7d48;
}

.modal-summary .order-info {
  margin: 0;
  color: rgba(71, 85, 105, 0.7);
  font-size: 13px;
}

.modal-summary .item-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 8px;
}

.modal-summary .item-list li {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.75);
}

.payment-modal .modal-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  text-align: center;
}

.payment-modal .channel-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  width: 100%;
}

.payment-modal .channel-selector .channel {
  border: none;
  border-radius: 16px;
  padding: 10px 16px;
  background: rgba(248, 250, 246, 0.95);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.2);
  cursor: pointer;
  min-width: 140px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.payment-modal .channel-selector .channel .name {
  font-weight: 600;
  color: #1c2b1f;
}

.payment-modal .channel-selector .channel .description {
  font-size: 12px;
  color: rgba(71, 85, 105, 0.7);
}

.payment-modal .channel-selector .channel.active,
.payment-modal .channel-selector .channel:hover {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.16), rgba(99, 209, 130, 0.16));
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.12);
  transform: translateY(-2px);
}

.payment-modal .modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.payment-modal .modal-actions .ghost,
.payment-modal .modal-actions .primary {
  border: none;
  border-radius: 999px;
  padding: 10px 22px;
  font-weight: 600;
  cursor: pointer;
}

.payment-modal .modal-actions .ghost {
  background: rgba(148, 163, 184, 0.18);
  color: rgba(15, 23, 42, 0.72);
}

.payment-modal .modal-actions .primary {
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #ffffff;
}

.payment-modal .modal-actions .ghost:disabled,
.payment-modal .modal-actions .primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.comment-editor {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-editor textarea {
  width: 100%;
  min-height: 120px;
  border-radius: 18px;
  border: 1px solid rgba(46, 125, 72, 0.2);
  padding: 16px;
  resize: vertical;
  font-size: 14px;
  color: #1f3d2a;
  background: rgba(255, 255, 255, 0.92);
}

.comment-editor textarea:focus {
  outline: none;
  border-color: rgba(46, 125, 72, 0.5);
  box-shadow: 0 0 0 3px rgba(46, 125, 72, 0.18);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  padding: 18px 20px;
  border-radius: 16px;
  background: rgba(46, 125, 72, 0.05);
  display: flex;
  flex-direction: column;
  gap: 12px;
  color: #1f3d2a;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: rgba(31, 61, 42, 0.7);
}

.comment-meta .user {
  font-weight: 600;
  color: #2e7d48;
}

.comment-meta .index {
  margin-left: auto;
  color: rgba(31, 61, 42, 0.4);
}

.comment-item .content {
  margin: 0;
  line-height: 1.6;
}

.empty-comment {
  text-align: center;
  color: rgba(31, 61, 42, 0.6);
  padding: 24px 0;
}

.pagination {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
}

.pagination button {
  padding: 8px 16px;
  border-radius: 999px;
  border: 1px solid rgba(46, 125, 72, 0.24);
  background: transparent;
  color: #2e7d48;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.pagination button:hover:not(:disabled) {
  background: rgba(46, 125, 72, 0.12);
  transform: translateY(-2px);
}

.pagination button.active {
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  border-color: transparent;
}

.pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.loading-state {
  text-align: center;
  padding: 80px 0;
  color: #666;
}

@media (max-width: 1024px) {
  .detail-hero {
    grid-template-columns: 1fr;
  }

  .info-panel {
    order: -1;
  }
}

@media (max-width: 640px) {
  .product-detail-page {
    margin-top: calc(-1 * (var(--header-height) + clamp(2px, 1.5vw, 6px)));
    padding: clamp(16px, 7vw, 28px);
  }

  .detail-container {
    gap: 32px;
  }

  .detail-hero {
    padding: clamp(20px, 6vw, 28px);
  }

  .action-buttons {
    flex-direction: column;
  }

  .comment-meta {
    flex-wrap: wrap;
    gap: 8px;
  }

  .comment-meta .index {
    margin-left: 0;
  }
}
</style>

