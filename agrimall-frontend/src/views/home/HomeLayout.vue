<template>
  <div class="home-layout">
    <HomeHeader
      :username="username"
      :cart-total="cartTotal"
      @logout="handleLogout"
    />
    <main class="home-content">
      <div class="page-container">
        <RouterView />
      </div>
    </main>
    <HomeFooter />
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import HomeHeader from '@/components/home/HomeHeader.vue';
import HomeFooter from '@/components/home/HomeFooter.vue';
import {
  CART_TOTAL_EVENT_NAME,
  getCartTotal,
  notifyCartCleared,
  setCartTotal,
} from '@/utils/cart';

const username = ref(localStorage.getItem('username') || '');
const cartTotal = ref(getCartTotal());

const handleStorageChange = (event) => {
  if (event.key === 'cartTotal') {
    cartTotal.value = getCartTotal();
  }
  if (event.key === 'username') {
    username.value = localStorage.getItem('username') || '';
  }
};

const handleCartEvent = (event) => {
  const next = Number(event?.detail);
  cartTotal.value = Number.isFinite(next) ? next : getCartTotal();
};

onMounted(() => {
  window.addEventListener('storage', handleStorageChange);
  window.addEventListener(CART_TOTAL_EVENT_NAME, handleCartEvent);
});

onBeforeUnmount(() => {
  window.removeEventListener('storage', handleStorageChange);
  window.removeEventListener(CART_TOTAL_EVENT_NAME, handleCartEvent);
});

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  localStorage.removeItem('email');
  localStorage.removeItem('phone');
  localStorage.removeItem('headPic');
  localStorage.removeItem('userId');
  notifyCartCleared();
  username.value = '';
  cartTotal.value = 0;
};

const setInitialCart = () => {
  const initial = getCartTotal();
  setCartTotal(initial);
  cartTotal.value = initial;
};

setInitialCart();
</script>

<style scoped>
:global(:root) {
  --header-height: 96px;
  --footer-min-height: 220px;
  --layout-horizontal-padding: clamp(16px, 6vw, 64px);
  --layout-vertical-gap: clamp(24px, 5vw, 48px);
}

.home-layout {
  min-height: 100vh;
  background: linear-gradient(180deg, #f7fbf7 0%, #fefefe 55%, #f8f9fb 100%);
  display: flex;
  flex-direction: column;
}

.home-content {
  flex: 1;
  padding: calc(var(--header-height) + clamp(4px, 2vw, 12px))
    clamp(12px, 5vw, 28px)
    clamp(28px, 7vw, 44px);
  box-sizing: border-box;
  min-height: calc(100vh - var(--footer-min-height));
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}

.page-container :deep(.Inside_pages) {
  max-width: 1280px;
  margin: 0 auto;
  padding: clamp(24px, 5vw, 40px) clamp(16px, 4vw, 32px);
  background: transparent;
}

@media (max-width: 768px) {
  :global(:root) {
    --header-height: 72px;
    --footer-min-height: 260px;
  }

  .home-content {
    padding: calc(var(--header-height) + clamp(6px, 4vw, 14px))
      clamp(12px, 5vw, 28px)
      calc(var(--layout-vertical-gap) + 40px);
  }
}
</style>

