const CART_TOTAL_EVENT = 'cart-total-updated';

const sanitizeTotal = (total) => {
  const parsed = Number(total);
  if (!Number.isFinite(parsed) || parsed <= 0) return 0;
  return Math.floor(parsed);
};

export const calculateCartTotal = (cartItems = []) =>
  cartItems.reduce((sum, item) => {
    const quantity =
      Number(item?.quantity) ||
      Number(item?.productQuantity) ||
      Number(item?.cartQuantity) ||
      0;
    return sum + (Number.isFinite(quantity) ? quantity : 0);
  }, 0);

export const setCartTotal = (total) => {
  const normalized = sanitizeTotal(total);
  localStorage.setItem('cartTotal', String(normalized));
  window.dispatchEvent(new CustomEvent(CART_TOTAL_EVENT, { detail: normalized }));
  return normalized;
};

export const getCartTotal = () => sanitizeTotal(localStorage.getItem('cartTotal'));

export const notifyCartCleared = () => {
  localStorage.removeItem('cartTotal');
  window.dispatchEvent(new CustomEvent(CART_TOTAL_EVENT, { detail: 0 }));
};

export const CART_TOTAL_EVENT_NAME = CART_TOTAL_EVENT;


