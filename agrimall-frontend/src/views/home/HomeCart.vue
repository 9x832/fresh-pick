<template>
  <div class="cart-page">
    <div class="cart-container" v-if="!loading">
      <header class="page-header">
        <h1>购物车</h1>
        <p class="subtitle">挑选好的生鲜商品都在这里，结算前记得确认数量和金额</p>
      </header>

      <section class="cart-panel" v-if="cartItems.length">
        <header class="panel-header">
          <label class="select-all">
            <input
              type="checkbox"
              v-model="selectAll"
              @change="toggleSelectAll"
            />
            <span>全选</span>
          </label>
          <div class="header-meta">
            <span>单价</span>
            <span>数量</span>
            <span>小计</span>
            <span>操作</span>
          </div>
        </header>

        <ul class="cart-list">
          <li v-for="item in cartItems" :key="item.productId" class="cart-item">
            <label class="select">
              <input
                type="checkbox"
                v-model="item.selected"
                @change="syncSelectAll"
              />
            </label>

            <RouterLink
              class="thumb"
              :to="`/home/product/detail/${item.productId}`"
            >
              <img :src="item.image" :alt="item.productName" />
            </RouterLink>

            <div class="info">
              <RouterLink
                class="name"
                :to="`/home/product/detail/${item.productId}`"
              >
                {{ item.productName }}
              </RouterLink>
              <p v-if="item.subtitle" class="desc">{{ item.subtitle }}</p>
            </div>

            <div class="price">￥{{ formatPrice(item.price) }}</div>

            <div class="quantity">
              <button type="button" :disabled="item.updating" @click="decreaseQuantity(item)">-</button>
              <input
                type="text"
                v-model.number="item.quantity"
                @blur="handleQuantityBlur(item)"
              />
              <button type="button" :disabled="item.updating" @click="increaseQuantity(item)">+</button>
            </div>

            <div class="total">￥{{ formatPrice(item.totalPrice) }}</div>

            <button
              type="button"
              class="remove"
              @click="removeItem(item)"
            >
              删除
            </button>
          </li>
        </ul>
      </section>

      <section v-else class="empty-state">
        <div class="empty-card">
          <h2>购物车还是空的</h2>
          <p>去首页逛逛，发现更多时令生鲜</p>
          <RouterLink to="/home" class="back-home">返回首页</RouterLink>
        </div>
      </section>
    </div>

    <transition name="slide-up">
      <aside class="cart-summary" v-if="cartItems.length">
        <div class="summary-count">
          已选 <strong>{{ selectedCount }}</strong> 件商品
        </div>
        <div class="summary-total">
          合计
          <span class="value">￥{{ formatPrice(selectedTotalPrice) }}</span>
        </div>
        <button type="button" class="checkout" @click="goToCheckout">
          去结算
        </button>
      </aside>
    </transition>

    <div v-if="loading" class="loading-state">正在加载购物车...</div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  fetchCart,
  updateCart,
  deleteCartItem,
  fetchAddresses,
} from '@/apis/home';
import {
  calculateCartTotal,
  notifyCartCleared,
  setCartTotal,
} from '@/utils/cart';

const router = useRouter();
const IMAGE_BASE_URL = 'http://localhost:8080';

const state = reactive({
  cartItems: [],
  loading: false,
});

const selectAll = ref(false);

const cartItems = computed(() => state.cartItems);
const loading = computed(() => state.loading);

const selectedItems = computed(() =>
  cartItems.value.filter((item) => item.selected),
);

const selectedCount = computed(() =>
  selectedItems.value.reduce((sum, item) => sum + item.quantity, 0),
);

const selectedTotalPrice = computed(() =>
  selectedItems.value.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0,
  ),
);

const formatPrice = (value) => Number(value || 0).toFixed(2);

const normalizeImage = (path) => {
  if (!path) return '';
  if (/^https?:\/\//.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const mapCartItem = (item) => ({
  productId: item.productId,
  productName: item.productName,
  subtitle: item.subtitle || '',
  image: normalizeImage(item.productPic),
  price: Number(item.price || 0),
  quantity: item.quantity || 1,
  totalPrice: Number(item.productTotalPrice || 0),
  selected: true,
  updating: false,
});

const loadCart = async () => {
  state.loading = true;
  try {
    const res = await fetchCart();
    if (res?.code === 0 && res.data) {
      const { cartProductVoList = [] } = res.data;
      state.cartItems = cartProductVoList.map(mapCartItem);
      selectAll.value = state.cartItems.every((item) => item.selected);
      setCartTotal(calculateCartTotal(cartProductVoList));
    } else if (res?.code === -6) {
      window.alert('请先登录后查看购物车');
      router.push('/login');
      notifyCartCleared();
    } else {
      state.cartItems = [];
      notifyCartCleared();
    }
  } catch (error) {
    console.error('加载购物车失败', error);
    state.cartItems = [];
    notifyCartCleared();
  } finally {
    state.loading = false;
  }
};

const toggleSelectAll = () => {
  state.cartItems.forEach((item) => {
    item.selected = selectAll.value;
  });
};

const syncSelectAll = () => {
  selectAll.value = state.cartItems.every((item) => item.selected);
};

const increaseQuantity = async (item) => {
  if (item.updating) return;
  const prevQuantity = item.quantity;
  const nextQuantity = prevQuantity + 1;
  item.quantity = nextQuantity;
  item.totalPrice = item.price * nextQuantity;
  item.updating = true;
  try {
    await updateCart(item.productId, 'add');
  } catch (error) {
    console.error('增加数量失败', error);
    window.alert('增加数量失败，请稍后重试');
    item.quantity = prevQuantity;
    item.totalPrice = item.price * prevQuantity;
  } finally {
    item.updating = false;
  }
};

const decreaseQuantity = async (item) => {
  if (item.updating) return;
  if (item.quantity <= 1) {
    window.alert('最少保留一件商品，如需删除请点击删除');
    return;
  }
  const prevQuantity = item.quantity;
  const nextQuantity = prevQuantity - 1;
  item.quantity = nextQuantity;
  item.totalPrice = item.price * nextQuantity;
  item.updating = true;
  try {
    await updateCart(item.productId, 'reduce');
  } catch (error) {
    console.error('减少数量失败', error);
    window.alert('减少数量失败，请稍后重试');
    item.quantity = prevQuantity;
    item.totalPrice = item.price * prevQuantity;
  } finally {
    item.updating = false;
  }
};

const handleQuantityBlur = async (item) => {
  if (!Number.isInteger(item.quantity) || item.quantity <= 0) {
    item.quantity = 1;
  }
  // 直接重新加载数据保证与后端一致
  await loadCart();
};

const removeItem = async (item) => {
  if (!window.confirm('确定要从购物车中移除此商品吗？')) {
    return;
  }
  try {
    await deleteCartItem(item.productId);
    await loadCart();
  } catch (error) {
    console.error('删除商品失败', error);
    window.alert('删除商品失败，请稍后重试');
  }
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
  window.alert('尚未配置收货地址，请先在“地址管理”中新增地址后再结算。');
  return false;
};

const goToCheckout = async () => {
  const items = selectedItems.value;
  if (!items.length) {
    window.alert('请先选择要结算的商品');
    return;
  }
  const cartIds = items.map((item) => item.productId).join(',');
  if (!cartIds) {
    window.alert('未找到有效的结算商品，请重新选择');
    return;
  }
  const hasAddress = await ensureHasAddress();
  if (!hasAddress) return;
  router.push({
    path: '/home/order/confirm',
    query: {
      cartIds,
      ts: Date.now(),
    },
  });
};

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f4f9f4 0%, #ffffff 45%, #f7f8fb 100%);
  margin-top: calc(-1 * (var(--header-height) + clamp(4px, 2vw, 12px)));
  padding: clamp(24px, 6vw, 48px) clamp(16px, 6vw, 72px) clamp(72px, 10vw, 120px);
}

.cart-container {
  width: min(1100px, 100%);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: clamp(24px, 5vw, 32px);
}

.page-header h1 {
  margin: 0;
  font-size: clamp(26px, 4vw, 34px);
  color: #1b2b1f;
}

.page-header .subtitle {
  margin: 8px 0 0;
  color: rgba(31, 61, 42, 0.6);
}

.cart-panel {
  background: rgba(255, 255, 255, 0.92);
  border-radius: clamp(20px, 4vw, 28px);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
  overflow: hidden;
  backdrop-filter: blur(8px);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: clamp(16px, 4vw, 24px) clamp(20px, 5vw, 32px);
  border-bottom: 1px solid rgba(46, 125, 72, 0.08);
  font-size: 14px;
  color: rgba(31, 61, 42, 0.6);
}

.select-all {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: rgba(31, 61, 42, 0.75);
  font-weight: 600;
}

.select-all input {
  width: 18px;
  height: 18px;
  accent-color: #2e7d48;
}

.header-meta {
  display: grid;
  grid-template-columns: repeat(4, minmax(80px, 120px));
  gap: clamp(12px, 3vw, 18px);
  justify-items: end;
}

.cart-list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: clamp(12px, 3vw, 18px);
  padding: clamp(12px, 4vw, 20px);
}

.cart-item {
  display: grid;
  grid-template-columns: auto 96px 1fr repeat(3, minmax(100px, 140px)) 70px;
  align-items: center;
  gap: clamp(12px, 3vw, 18px);
  padding: clamp(16px, 4vw, 22px);
  background: rgba(46, 125, 72, 0.06);
  border-radius: clamp(16px, 3vw, 20px);
  color: #1f3d2a;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.cart-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 16px 36px rgba(46, 125, 72, 0.16);
  background: rgba(46, 125, 72, 0.12);
}

.select input {
  width: 18px;
  height: 18px;
  accent-color: #2e7d48;
}

.thumb {
  width: 96px;
  height: 96px;
  border-radius: 16px;
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

.info .desc {
  margin: 0;
  font-size: 13px;
  color: rgba(31, 61, 42, 0.55);
}

.price,
.total {
  font-weight: 600;
  text-align: right;
}

.quantity {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  border: 1px solid rgba(46, 125, 72, 0.24);
  overflow: hidden;
}

.quantity button {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(46, 125, 72, 0.08);
  color: #2e7d48;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.quantity button:hover {
  background: rgba(46, 125, 72, 0.18);
}

.quantity button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
  background: rgba(46, 125, 72, 0.08);
}

.quantity input {
  width: 56px;
  height: 40px;
  border: none;
  text-align: center;
  font-size: 16px;
  color: #1f3d2a;
  background: transparent;
}

.remove {
  border: none;
  background: none;
  color: #d9504f;
  cursor: pointer;
  font-weight: 600;
  transition: color 0.2s ease;
}

.remove:hover {
  color: #b43d3c;
}

.empty-state {
  display: flex;
  justify-content: center;
  padding: clamp(80px, 10vw, 120px) 0;
}

.empty-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: clamp(24px, 4vw, 32px);
  padding: clamp(32px, 6vw, 48px);
  text-align: center;
  box-shadow: 0 28px 64px rgba(15, 23, 42, 0.1);
  backdrop-filter: blur(8px);
}

.empty-card h2 {
  margin: 0;
  font-size: clamp(22px, 3.5vw, 28px);
  color: #1b2b1f;
}

.empty-card p {
  margin: 12px 0 24px;
  color: rgba(31, 61, 42, 0.55);
}

.back-home {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 28px;
  border-radius: 999px;
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  text-decoration: none;
  font-weight: 600;
  box-shadow: 0 18px 32px rgba(46, 125, 72, 0.24);
}

.cart-summary {
  position: fixed;
  left: 50%;
  bottom: clamp(16px, 4vw, 32px);
  transform: translateX(-50%);
  width: min(840px, calc(100% - clamp(32px, 12vw, 120px)));
  background: rgba(255, 255, 255, 0.95);
  border-radius: 999px;
  padding: clamp(16px, 4vw, 20px) clamp(20px, 6vw, 32px);
  display: flex;
  align-items: center;
  gap: clamp(16px, 4vw, 24px);
  box-shadow: 0 20px 48px rgba(15, 23, 42, 0.18);
  backdrop-filter: blur(10px);
}

.summary-count {
  color: rgba(31, 61, 42, 0.7);
  font-size: 14px;
}

.summary-count strong {
  color: #2e7d48;
  font-size: 18px;
  margin: 0 4px;
}

.summary-total {
  margin-left: auto;
  color: rgba(31, 61, 42, 0.7);
  font-weight: 600;
}

.summary-total .value {
  margin-left: 8px;
  font-size: 22px;
  color: #2e7d48;
}

.checkout {
  padding: 12px 32px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 18px 32px rgba(46, 125, 72, 0.25);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.checkout:hover {
  transform: translateY(-2px);
  box-shadow: 0 24px 42px rgba(46, 125, 72, 0.28);
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.24s ease, opacity 0.24s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translate(-50%, 40px);
  opacity: 0;
}

.loading-state {
  text-align: center;
  padding: 80px 0;
  color: rgba(31, 61, 42, 0.6);
}

@media (max-width: 1024px) {
  .cart-item {
    grid-template-columns: auto 80px 1fr 100px 120px 100px 60px;
  }

  .cart-summary {
    width: calc(100% - clamp(32px, 8vw, 72px));
  }
}

@media (max-width: 768px) {
  .cart-page {
    margin-top: calc(-1 * (var(--header-height) + clamp(2px, 1.5vw, 6px)));
    padding: clamp(16px, 6vw, 24px);
  }

  .panel-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-meta {
    width: 100%;
    grid-template-columns: repeat(4, 1fr);
    justify-items: center;
  }

  .cart-list {
    padding: clamp(10px, 4vw, 18px);
  }

  .cart-item {
    grid-template-columns: repeat(2, 1fr);
    grid-template-areas:
      'select info'
      'thumb info'
      'price quantity'
      'total remove';
    gap: 12px;
  }

  .select {
    grid-area: select;
  }

  .thumb {
    grid-area: thumb;
    width: 100%;
    height: 140px;
  }

  .info {
    grid-area: info;
  }

  .price {
    grid-area: price;
    text-align: left;
  }

  .quantity {
    grid-area: quantity;
    justify-self: end;
  }

  .total {
    grid-area: total;
    text-align: left;
  }

  .remove {
    grid-area: remove;
    justify-self: end;
  }

  .cart-summary {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    border-radius: clamp(20px, 5vw, 28px);
    padding: clamp(18px, 6vw, 24px);
  }

  .summary-total {
    margin-left: 0;
    display: flex;
    justify-content: space-between;
  }

  .checkout {
    width: 100%;
  }
}
</style>

