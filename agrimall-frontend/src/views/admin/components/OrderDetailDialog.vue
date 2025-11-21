<template>
  <div class="dialog-backdrop">
    <div class="dialog">
      <header>
        <h3>订单详情</h3>
        <button type="button" class="close-btn" @click="$emit('close')">
          <i class="ri-close-line"></i>
        </button>
      </header>
      <section class="dialog-body" v-if="order">
        <div class="info-grid">
          <div>
            <h4>基本信息</h4>
            <p><span>订单号：</span>{{ order.orderNo }}</p>
            <p><span>用户：</span>{{ order.user?.username || '-' }}（ID: {{ order.userId }}）</p>
            <p><span>金额：</span>￥{{ formatPrice(order.totalPrice) }}</p>
            <p><span>状态：</span>{{ stateText(order.state) }}</p>
            <p><span>下单时间：</span>{{ formatDate(order.createTime) }}</p>
            <p><span>更新时间：</span>{{ formatDate(order.updateTime) }}</p>
          </div>
          <div>
            <h4>收货信息</h4>
            <p><span>联系人：</span>{{ shippingInfo.name }}</p>
            <p><span>电话：</span>{{ shippingInfo.phone }}</p>
            <p><span>地址：</span>{{ shippingInfo.address }}</p>
            <p><span>备注：</span>{{ shippingInfo.remark }}</p>
          </div>
        </div>

        <div class="item-list">
          <h4>商品明细</h4>
          <table>
            <thead>
              <tr>
                <th>商品</th>
                <th style="width: 80px;">单价</th>
                <th style="width: 80px;">数量</th>
                <th style="width: 100px;">小计</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in items" :key="item.id">
                <td>
                  <div class="item-info">
                    <img :src="resolveImage(item.productPic)" alt="商品图片" />
                    <span>{{ item.productName }}</span>
                  </div>
                </td>
                <td class="center">￥{{ formatPrice(item.productPrice) }}</td>
                <td class="center">{{ item.quantity }}</td>
                <td class="center">￥{{ formatPrice(item.totalPrice) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
      <section v-else class="dialog-body">
        <p class="loading-cell">详情加载中...</p>
      </section>
      <footer class="dialog-footer">
        <button type="button" class="btn secondary" @click="$emit('close')">关闭</button>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  order: {
    type: Object,
    default: null,
  },
  items: {
    type: Array,
    default: () => [],
  },
});

const stateOptions = [
  { value: 0, label: '未支付' },
  { value: 1, label: '已支付，待发货' },
  { value: 5, label: '已发货' },
  { value: 3, label: '已送达，待签收' },
  { value: 4, label: '已签收' },
  { value: 2, label: '已取消' },
];

const stateMap = computed(() =>
  stateOptions.reduce((map, item) => {
    map[item.value] = item.label;
    return map;
  }, {}),
);

const stateText = (state) => stateMap.value[state] || `状态${state}`;

const shippingInfo = computed(() => {
  const order = props.order || {};
  const addr = order.address || {};
  const user = order.user || {};
  const name = addr.receiverName || addr.name || order.receiverName || user.username || '-';
  const phone = addr.receiverPhone || addr.phone || order.receiverPhone || user.phone || '-';
  let address = '-';
  if (addr.detailAddress || addr.province || addr.city || addr.area) {
    address = `${addr.province || ''}${addr.city || ''}${addr.area || ''}${addr.detailAddress || ''}`.trim();
  } else if (addr.receiverAddress) {
    address = addr.receiverAddress;
  } else if (order.receiverAddress) {
    address = order.receiverAddress;
  }
  if (!address) address = '-';
  return {
    name,
    phone,
    address,
    remark: order.remark || '无',
  };
});

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

const resolveImage = (path) => {
  if (!path) return 'https://via.placeholder.com/60.png?text=Product';
  if (/^https?:\/\//i.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  const base = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
  return `${base}/api/common/photo/view?filename=${clean}`;
};
</script>

<style scoped>
.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2100;
  padding: 24px;
}

.dialog {
  width: min(720px, 100%);
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.3);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 90vh;
  overflow: hidden;
}

.dialog header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog header h3 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}

.close-btn {
  border: none;
  background: transparent;
  font-size: 22px;
  cursor: pointer;
  color: #64748b;
}

.dialog-body {
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.info-grid h4 {
  margin: 0 0 8px;
  color: #1f2937;
}

.info-grid p {
  margin: 6px 0;
  color: #475569;
}

.info-grid span {
  color: #1f2937;
  font-weight: 600;
}

.item-list h4 {
  margin: 0 0 12px;
  color: #1f2937;
}

.item-list table {
  width: 100%;
  border-collapse: collapse;
}

.item-list th,
.item-list td {
  padding: 10px 12px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  font-size: 14px;
  color: #1f2933;
  text-align: left;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-info img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 12px;
}

.center {
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn {
  border: none;
  border-radius: 10px;
  padding: 0 16px;
  height: 38px;
  cursor: pointer;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
}
.loading-cell {
  text-align: center;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .dialog {
    padding: 18px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>

