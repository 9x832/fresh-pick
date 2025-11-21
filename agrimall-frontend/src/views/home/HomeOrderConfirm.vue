<template>
  <div class="order-confirm">
    <div v-if="loading" class="loading-state">正在加载订单信息...</div>
    <div v-else-if="cartItems.length" class="order-shell">
      <header class="page-header">
        <div>
          <p class="eyebrow">确认订单</p>
          <h1>核对收货信息与商品，完成下单</h1>
          <p class="subtitle">完成支付前仍可以返回购物车调整，确认无误后继续提交</p>
        </div>
        <ul class="steps">
          <li class="done">购物车</li>
          <li class="current">确认订单</li>
          <li>提交成功</li>
        </ul>
      </header>

      <main class="order-body">
        <section class="content">
          <article class="card address-card" v-if="addresses.length">
            <header>
              <h2>收货信息</h2>
              <button type="button" class="ghost" @click="openAddressSelector">
                切换地址
              </button>
            </header>
            <ul class="address-info">
              <li>
                <span>收货人</span>
                <strong>{{ selectedAddress?.receiverName }}</strong>
              </li>
              <li>
                <span>联系电话</span>
                <strong>{{ selectedAddress?.receiverPhone }}</strong>
              </li>
              <li>
                <span>收货地址</span>
                <strong>{{ selectedAddress?.receiverAddress }}</strong>
              </li>
            </ul>
          </article>
          <article class="card empty-address" v-else>
            <h2>暂无默认收货地址</h2>
            <p>当前缺少收货信息，请先前往地址管理，新增并设置默认地址后继续结算。</p>
            <button type="button" class="primary" @click="goManageAddress">去管理地址</button>
          </article>

          <article class="card items-card">
            <header>
              <h2>商品清单</h2>
              <span>{{ itemCount }} 件</span>
            </header>
            <ul class="items">
              <li v-for="item in cartItems" :key="item.productId">
                <RouterLink
                  class="thumb"
                  :to="`/home/product/detail/${item.productId}`"
                >
                  <img :src="normalizeImage(item.productPic)" :alt="item.productName" />
                </RouterLink>
                <div class="info">
                  <RouterLink :to="`/home/product/detail/${item.productId}`" class="name">
                    {{ item.productName }}
                  </RouterLink>
                  <p class="meta">
                    <span>单价 ￥{{ formatPrice(item.productPrice) }}</span>
                    <span>数量 ×{{ item.quantity }}</span>
                  </p>
                </div>
                <div class="total">￥{{ formatPrice(item.productPrice * item.quantity) }}</div>
              </li>
            </ul>
          </article>

          <article class="card remark-card">
            <header>
              <h2>订单备注</h2>
              <span>最多 50 字</span>
            </header>
            <textarea
              v-model.trim="remark"
              placeholder="如需联系配送员、指定配送时间等说明，可在此填写"
              maxlength="50"
            ></textarea>
          </article>
        </section>

        <aside class="summary">
          <div class="card summary-card">
            <h2>结算信息</h2>
            <dl>
              <div>
                <dt>商品金额</dt>
                <dd>￥{{ formatPrice(orderTotal) }}</dd>
              </div>
              <div>
                <dt>运费</dt>
                <dd>￥0.00</dd>
              </div>
              <div class="total">
                <dt>应付总额</dt>
                <dd>￥{{ formatPrice(orderTotal) }}</dd>
              </div>
            </dl>
            <div class="actions">
              <button type="button" class="ghost" @click="goBack">返回购物车</button>
              <button type="button" class="primary" :disabled="submitting" @click="handleSubmit">
                {{ submitting ? '处理中...' : '立即支付' }}
              </button>
            </div>
          </div>
          <div class="card tips-card">
            <h3>下单提示</h3>
            <ul>
              <li>提交后请尽快完成支付，避免因库存不足导致订单关闭。</li>
              <li>如需修改收货地址，可返回购物车重新选择后再下单。</li>
              <li>支持订单备注，特殊需求可提前告诉配送员。</li>
            </ul>
          </div>
        </aside>
      </main>
    </div>
    <div v-else class="empty-state">
      <h2>未找到订单信息</h2>
      <p>请从购物车重新发起结算。</p>
      <RouterLink to="/home/cart">返回购物车</RouterLink>
    </div>

    <transition name="fade">
      <div v-if="paymentVisible" class="payment-modal">
        <div class="modal-mask" @click="closePayment"></div>
        <div class="modal-card">
          <header>
            <h2>确认支付</h2>
            <button type="button" class="close" @click="closePayment">×</button>
          </header>
          <section class="modal-body">
            <p class="amount">￥{{ formatPrice(orderTotal) }}</p>
            <p class="order-info">订单号：{{ currentOrderId }}</p>
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
            <div class="payment-preview" v-if="selectedChannel === 'wechat' || selectedChannel === 'alipay'">
              <div class="qr-placeholder">
                <span>二维码</span>
              </div>
              <p class="tips">{{ paymentChannels.find((c) => c.code === selectedChannel)?.tips }}</p>
            </div>
            <div class="card-form" v-else>
              <label>
                <span>卡号</span>
                <input type="text" placeholder="6222 **** **** 1234" />
              </label>
              <label>
                <span>有效期</span>
                <input type="text" placeholder="MM/YY" />
              </label>
              <label>
                <span>CVV</span>
                <input type="password" placeholder="***" />
              </label>
              <p class="tips">模拟输入，无需真实卡片信息</p>
            </div>
            <p class="desc">支付剩余时间：<strong>{{ Math.floor(countdown / 60) }}分 {{ countdown % 60 }}秒</strong></p>
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

    <transition name="fade">
      <div v-if="addressSelectorVisible" class="payment-modal">
        <div class="modal-mask" @click="closeAddressSelector"></div>
        <div class="modal-card address-modal">
          <header>
            <h2>选择收货地址</h2>
            <button type="button" class="close" @click="closeAddressSelector">×</button>
          </header>
          <section class="modal-body address-body">
            <ul class="address-list">
              <li
                v-for="item in addresses"
                :key="item.id"
                :class="{ active: selectedAddress?.id === item.id }"
                @click="selectAddress(item)"
              >
                <div class="row">
                  <strong>{{ item.receiverName }}</strong>
                  <span>{{ item.receiverPhone }}</span>
                  <span v-if="Number(item.firstSelected) === 1" class="badge">默认</span>
                </div>
                <p>{{ item.receiverAddress }}</p>
              </li>
            </ul>
          </section>
          <footer class="modal-actions">
            <button type="button" class="primary" @click="closeAddressSelector">完成</button>
          </footer>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  generateOrder,
  submitOrder,
  fetchCart,
  fetchAddresses,
  setDefaultAddress,
} from '@/apis/home';

const route = useRoute();
const router = useRouter();

const IMAGE_BASE_URL = 'http://localhost:8080';

const order = ref(null);
const cartItems = ref([]);
const address = ref(null);
const remark = ref('');
const loading = ref(false);
const initializing = ref(false);
const submitting = ref(false);
const cartIds = ref([]);
const paymentVisible = ref(false);
const processingPay = ref(false);
const currentOrderId = ref(null);
const countdown = ref(900); // 15 minutes
const timer = ref(null);
const selectedChannel = ref('wechat');
const addresses = ref([]);
const addressSelectorVisible = ref(false);

const selectedAddress = computed(() => address.value);

const orderTotal = computed(() =>
  cartItems.value.reduce((sum, item) => sum + item.productPrice * item.quantity, 0),
);

const itemCount = computed(() => cartItems.value.length);

const paymentChannels = [
  {
    code: 'wechat',
    name: '微信支付',
    desc: '扫码支付，支持微信钱包余额',
    tips: '请使用微信扫描二维码完成支付',
  },
  {
    code: 'alipay',
    name: '支付宝',
    desc: '推荐支付宝用户，支持花呗',
    tips: '使用支付宝扫一扫即可完成支付',
  },
  {
    code: 'card',
    name: '银行卡快捷支付',
    desc: '支持主流借记卡、信用卡',
    tips: '开启短信验证，保障您的资金安全',
  },
];

const orderId = ref(route.query.orderId ? Number(route.query.orderId) : null);

const normalizeImage = (path) => {
  if (!path) return '';
  if (/^https?:\/\//.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const formatPrice = (value) => Number(value || 0).toFixed(2);

const mapCartItem = (item) => ({
  productId: item.productId,
  productName: item.productName,
  productPic: item.productPic,
  productPrice: Number(item.price || item.productPrice || 0),
  quantity: Number(item.quantity || 1),
});

const loadSelectedCartItems = async (ids) => {
  try {
    const res = await fetchCart();
    if (res?.code === 0 && res.data) {
      const selectedSet = new Set(ids.map((id) => String(id)));
      const list = (res.data.cartProductVoList || []).filter((item) =>
        selectedSet.has(String(item.productId)),
      );
      cartItems.value = list.map(mapCartItem);
      return cartItems.value.length > 0;
    }
    if (res?.code === -6) {
      window.alert('请先登录后再下单');
      router.replace('/login');
      return false;
    }
  } catch (error) {
    console.error('加载购物车商品失败', error);
  }
  window.alert('未找到要结算的商品，请重新选择');
  router.replace('/home/cart');
  return false;
};

const loadDefaultAddress = async () => {
  try {
    const res = await fetchAddresses();
    if (res?.code === 0 && Array.isArray(res.data)) {
      addresses.value = res.data;
      address.value =
        res.data.find((item) => Number(item.firstSelected) === 1) || res.data[0] || null;
      return;
    }
  } catch (error) {
    console.error('加载地址失败', error);
  }
  addresses.value = [];
  address.value = null;
};

const createOrderFromCart = async (ids) => {
  try {
    const res = await generateOrder({ ids });
    if (res?.code === 0 && res.data) {
      return Number(res.data);
    }
    if (res?.code === -6) {
      window.alert('请先登录后再下单');
      router.replace('/login');
    } else {
      window.alert(res?.msg || '生成订单失败，请重试');
      router.replace('/home/cart');
    }
  } catch (error) {
    console.error('生成订单失败', error);
    window.alert('网络错误，生成订单失败');
    router.replace('/home/cart');
  }
  return null;
};

const initPage = async () => {
  if (initializing.value) return;
  initializing.value = true;
  loading.value = true;
  try {
    const idParam = route.query.cartIds ? String(route.query.cartIds) : '';
    const parsedIds = idParam
      .split(',')
      .map((id) => id.trim())
      .filter((id) => id);

    cartIds.value = parsedIds;

    if (!cartIds.value.length) {
      window.alert('未选择结算商品，请重新从购物车发起结算');
      router.replace('/home/cart');
      return;
    }

    const listLoaded = await loadSelectedCartItems(cartIds.value);
    if (!listLoaded) return;

    await loadDefaultAddress();
  } finally {
    loading.value = false;
    initializing.value = false;
  }
};

const goBack = () => {
  router.push('/home/cart');
};

const openAddressSelector = () => {
  if (!addresses.value.length) {
    window.alert('暂无可选地址，请先新增地址。');
    return;
  }
  addressSelectorVisible.value = true;
};

const closeAddressSelector = () => {
  addressSelectorVisible.value = false;
};

const selectAddress = async (item) => {
  if (!item || address.value?.id === item.id) {
    addressSelectorVisible.value = false;
    return;
  }
  try {
    const res = await setDefaultAddress(item.id);
    if (res?.code === 0) {
      await loadDefaultAddress();
      addressSelectorVisible.value = false;
    } else {
      window.alert(res?.msg || '设置默认地址失败，请稍后重试');
    }
  } catch (error) {
    console.error('切换默认地址失败', error);
    window.alert('网络错误，设置默认地址失败');
  }
};

const goManageAddress = () => {
  router.push('/home/user/address');
};

const handleSubmit = async () => {
  if (!cartIds.value.length) {
    window.alert('未选择结算商品，请重新从购物车发起结算');
    router.replace('/home/cart');
    return;
  }
  submitting.value = true;
  try {
    const orderId = await createOrderFromCart(cartIds.value.join(','));
    if (!orderId) {
      return;
    }
    currentOrderId.value = orderId;
    paymentVisible.value = true;
    startCountdown();
  } catch (error) {
    console.error('提交订单失败', error);
    window.alert('网络错误，提交失败');
  } finally {
    submitting.value = false;
  }
};

const closePayment = () => {
  if (processingPay.value) return;
  paymentVisible.value = false;
  currentOrderId.value = null;
  selectedChannel.value = 'wechat';
  stopCountdown();
  countdown.value = 900;
};

const confirmPayment = async () => {
  if (!currentOrderId.value) {
    window.alert('订单信息异常，请重新下单');
    paymentVisible.value = false;
    router.replace('/home/cart');
    return;
  }
  processingPay.value = true;
  try {
    const res = await submitOrder({
      remark: remark.value,
      orderId: currentOrderId.value,
    });
    if (res?.code === 0) {
      window.alert(res.msg || '支付成功，订单已提交');
      paymentVisible.value = false;
      router.push('/home/user/order');
    } else if (res?.code === -6) {
      window.alert('请先登录后提交订单');
      router.push('/login');
    } else {
      window.alert(res?.msg || '支付失败，请稍后再试');
    }
  } catch (error) {
    console.error('支付失败', error);
    window.alert('网络错误，支付失败');
  } finally {
    processingPay.value = false;
    stopCountdown();
    countdown.value = 900;
  }
};

const startCountdown = () => {
  stopCountdown();
  timer.value = setInterval(() => {
    if (countdown.value <= 0) {
      stopCountdown();
      window.alert('支付超时，订单已取消');
      paymentVisible.value = false;
      router.push('/home/cart');
        return;
      }
    countdown.value -= 1;
  }, 1000);
};

const stopCountdown = () => {
  if (timer.value) {
    clearInterval(timer.value);
    timer.value = null;
  }
};

watch(
  () => [route.query.orderId, route.query.cartIds],
  () => {
    initPage();
  },
);

onMounted(() => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
  initPage();
});
</script>

<style scoped>
.order-confirm {
  min-height: 100vh;
  margin-top: calc(-1 * (var(--header-height) + clamp(4px, 2vw, 12px)));
  background: linear-gradient(180deg, #f3faf3 0%, #ffffff 40%, #f7f8fb 100%);
  padding: clamp(24px, 6vw, 48px) clamp(16px, 7vw, 88px) clamp(72px, 12vw, 140px);
}

.order-shell {
  width: min(1200px, 100%);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: clamp(24px, 6vw, 40px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: clamp(24px, 5vw, 32px);
}

.eyebrow {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #2e7d48;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.page-header h1 {
  margin: 8px 0 12px;
  font-size: clamp(28px, 4.5vw, 36px);
  color: #1c2b1f;
}

.subtitle {
  margin: 0;
  color: rgba(31, 61, 42, 0.65);
}

.steps {
  display: inline-flex;
  align-items: center;
  gap: clamp(12px, 3vw, 18px);
  list-style: none;
  margin: 0;
  padding: 16px 20px;
  background: rgba(46, 125, 72, 0.08);
  border-radius: 999px;
  font-size: 14px;
  color: rgba(31, 61, 42, 0.6);
}

.steps li {
  padding: 6px 16px;
  border-radius: 999px;
  font-weight: 600;
}

.steps .done {
  background: rgba(46, 125, 72, 0.14);
  color: #2e7d48;
}

.steps .current {
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
}

.order-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) clamp(280px, 28vw, 360px);
  gap: clamp(24px, 5vw, 32px);
  align-items: start;
}

.card {
  background: rgba(255, 255, 255, 0.92);
  border-radius: clamp(18px, 3vw, 24px);
  padding: clamp(20px, 5vw, 28px);
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(8px);
}

.card h2 {
  margin: 0;
  font-size: clamp(20px, 3vw, 24px);
  color: #223729;
}

.card header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: clamp(16px, 4vw, 20px);
  flex-wrap: wrap;
}

.card header a {
  color: #2e7d48;
  text-decoration: none;
  font-size: 14px;
}

.card header a:hover {
  text-decoration: underline;
}

.address-info {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: clamp(12px, 3vw, 16px);
}

.address-info li {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: rgba(31, 61, 42, 0.6);
  font-size: 14px;
}

.address-info strong {
  color: #1f3d2a;
  font-size: 16px;
}

.empty-address {
  text-align: center;
}

.empty-address h2 {
  font-size: clamp(22px, 3.2vw, 26px);
  color: #1f3d2a;
}

.empty-address p {
  margin: 12px 0 20px;
  color: rgba(31, 61, 42, 0.6);
}

.empty-address .primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 32px;
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  border-radius: 999px;
  text-decoration: none;
  font-weight: 600;
  box-shadow: 0 18px 36px rgba(46, 125, 72, 0.24);
}

.items-card header span {
  color: rgba(31, 61, 42, 0.5);
  font-size: 14px;
}

.items {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vw, 20px);
}

.items li {
  display: grid;
  grid-template-columns: 96px 1fr auto;
  gap: clamp(16px, 4vw, 20px);
  align-items: center;
  padding: clamp(16px, 4vw, 22px);
  background: rgba(46, 125, 72, 0.08);
  border-radius: clamp(16px, 3vw, 20px);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.items li:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 40px rgba(46, 125, 72, 0.16);
}

.thumb {
  width: 96px;
  height: 96px;
  border-radius: clamp(16px, 3vw, 20px);
  overflow: hidden;
  background: rgba(245, 248, 246, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info .name {
  font-weight: 600;
  color: #1f3d2a;
  text-decoration: none;
}

.info .name:hover {
  color: #2e7d48;
}

.meta {
  margin: 0;
  color: rgba(31, 61, 42, 0.6);
  display: flex;
  gap: clamp(12px, 3vw, 18px);
  font-size: 14px;
}

.items .total {
  font-weight: 700;
  color: #2e7d48;
  font-size: 18px;
}

.remark-card {
  padding: clamp(20px, 5vw, 24px);
}

.remark-card header {
  margin-bottom: clamp(12px, 3vw, 16px);
}

.remark-card header span {
  color: rgba(31, 61, 42, 0.5);
  font-size: 14px;
}

.remark-card textarea {
  width: 100%;
  min-height: 120px;
  border: 1px solid rgba(46, 125, 72, 0.18);
  border-radius: clamp(16px, 3vw, 20px);
  padding: clamp(14px, 4vw, 16px);
  resize: none;
  font-size: 15px;
  color: #1f3d2a;
  background: rgba(46, 125, 72, 0.05);
  box-sizing: border-box;
  overflow-wrap: break-word;
}

.remark-card textarea:focus {
  outline: none;
  border-color: rgba(46, 125, 72, 0.4);
  background: rgba(46, 125, 72, 0.08);
}

.summary-card h2 {
  font-size: clamp(22px, 3.2vw, 26px);
}

.summary-card dl {
  margin: clamp(16px, 4vw, 24px) 0;
  display: flex;
  flex-direction: column;
  gap: clamp(12px, 3vw, 16px);
}

.summary-card dt {
  color: rgba(31, 61, 42, 0.6);
  font-size: 14px;
}

.summary-card dd {
  margin: 0;
  font-weight: 600;
  color: #1f3d2a;
}

.summary-card .total dt {
  color: #1f3d2a;
  font-weight: 700;
  font-size: 16px;
}

.summary-card .total dd {
  color: #2e7d48;
  font-size: 22px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.actions button {
  height: 48px;
  border-radius: 999px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.actions .ghost {
  background: rgba(46, 125, 72, 0.08);
  color: #2e7d48;
}

.actions .ghost:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 32px rgba(46, 125, 72, 0.18);
}

.actions .primary {
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  box-shadow: 0 18px 36px rgba(46, 125, 72, 0.24);
}

.actions .primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 22px 44px rgba(46, 125, 72, 0.26);
}

.tips-card h3 {
  margin: 0 0 12px;
  font-size: 18px;
  color: #1f3d2a;
}

.tips-card ul {
  margin: 0;
  padding-left: 18px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  color: rgba(31, 61, 42, 0.6);
  font-size: 14px;
}

.loading-state {
  text-align: center;
  padding: 48px 0;
  color: rgba(31, 61, 42, 0.6);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
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
  width: min(420px, 92vw);
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.22);
  padding: 24px clamp(20px, 4vw, 32px) clamp(20px, 4vw, 28px);
  display: flex;
  flex-direction: column;
  gap: 18px;
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

.payment-modal .modal-body {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}
.payment-modal .modal-body .amount {
  font-size: clamp(30px, 5vw, 40px);
  font-weight: 700;
  color: #2e7d48;
  margin: 0;
}
.payment-modal .modal-body .order-info,
.payment-modal .modal-body .desc {
  margin: 0;
  color: rgba(71, 85, 105, 0.7);
  font-size: 14px;
}

.payment-modal .channel-selector {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
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
  display: flex;
  flex-direction: column;
  min-width: 140px;
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
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.18), rgba(99, 209, 130, 0.18));
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.12);
  transform: translateY(-2px);
}

.payment-modal .payment-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.payment-modal .qr-placeholder {
  width: 160px;
  height: 160px;
  border-radius: 16px;
  background: repeating-linear-gradient(
      45deg,
      rgba(76, 187, 108, 0.12),
      rgba(76, 187, 108, 0.12) 10px,
      rgba(76, 187, 108, 0.05) 10px,
      rgba(76, 187, 108, 0.05) 20px
    ),
    #fff;
  box-shadow: inset 0 0 0 2px rgba(76, 187, 108, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(31, 61, 42, 0.55);
  font-weight: 600;
}
.payment-modal .payment-preview .tips {
  margin: 0;
  color: rgba(31, 61, 42, 0.6);
  font-size: 13px;
}

.payment-modal .card-form {
  width: 100%;
  display: grid;
  gap: 12px;
  justify-items: stretch;
}
.payment-modal .card-form label {
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: left;
  color: rgba(31, 61, 42, 0.7);
  font-size: 13px;
}
.payment-modal .card-form input {
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  padding: 10px 14px;
  font-size: 14px;
}
.payment-modal .card-form .tips {
  margin: 0;
  color: rgba(71, 85, 105, 0.65);
  text-align: center;
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
.payment-modal .modal-actions .primary:disabled,
.payment-modal .modal-actions .ghost:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.payment-modal .address-modal {
  width: min(520px, 92vw);
}

.address-body {
  align-items: stretch;
  text-align: left;
  padding-top: 0;
}

.address-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 360px;
  overflow-y: auto;
}

.address-list li {
  border-radius: 16px;
  padding: 12px 16px;
  background: rgba(248, 250, 252, 0.95);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.18);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 6px;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.address-list li.active {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.18), rgba(99, 209, 130, 0.18));
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.12);
}

.address-list li .row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.address-list li .row strong {
  font-size: 15px;
  color: #1c2b1f;
}

.address-list li .row span {
  font-size: 13px;
  color: rgba(71, 85, 105, 0.75);
}

.address-list li .row .badge {
  padding: 2px 10px;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.18);
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 600;
}

.address-list li p {
  margin: 0;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.78);
}

.empty-state {
  text-align: center;
  padding: clamp(100px, 14vw, 150px) clamp(16px, 6vw, 36px);
  color: #1f3d2a;
}

.empty-state h2 {
  font-size: clamp(24px, 4vw, 30px);
  margin-bottom: 12px;
}

.empty-state a {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 32px;
  border-radius: 999px;
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  text-decoration: none;
  margin-top: 20px;
}

@media (max-width: 1024px) {
  .order-body {
    grid-template-columns: 1fr;
  }

  .summary {
    display: flex;
    flex-direction: column;
    gap: clamp(16px, 4vw, 24px);
  }
}

@media (max-width: 768px) {
  .order-confirm {
    margin-top: calc(-1 * (var(--header-height) + clamp(2px, 1.5vw, 6px)));
    padding: clamp(16px, 6vw, 24px);
  }

  .page-header {
    flex-direction: column;
  }

  .steps {
    align-self: flex-start;
  }

  .items li {
    grid-template-columns: 72px 1fr;
    grid-template-rows: auto auto;
    grid-template-areas:
      'thumb info'
      'total total';
  }

  .items .total {
    grid-area: total;
    justify-self: flex-end;
  }
}
</style>

