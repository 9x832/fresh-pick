<template>
  <section class="dashboard-container">
    <div v-if="loading" class="card loading-card">
      <p>仪表盘数据加载中，请稍候...</p>
    </div>
    <template v-else>
      <div v-if="errorMessage" class="card error-card">
        <h3>仪表盘数据加载失败</h3>
        <p>{{ errorMessage }}</p>
        <button type="button" class="btn primary" @click="loadDashboard">重新加载</button>
      </div>

      <header class="welcome-card">
        <h2>欢迎回来，{{ adminName || '管理员' }}！</h2>
        <p>让我们继续守护鲜采直送自营平台的好品质，把最新鲜的好物送到用户餐桌上 🍎</p>
      </header>

      <div class="stats-grid">
        <DashboardStatCard
          title="今日订单个数"
          icon="ri-shopping-bag-3-line"
          :value="stats.todayOrderCount"
          gradient="#22c55e,#16a34a"
        />
        <DashboardStatCard
          title="本周订单个数"
          icon="ri-calendar-check-line"
          :value="stats.weekOrderCount"
          gradient="#10b981,#0f766e"
        />
        <DashboardStatCard
          title="本月订单个数"
          icon="ri-bar-chart-2-line"
          :value="stats.monthOrderCount"
          gradient="#38bdf8,#0284c7"
        />
        <DashboardStatCard
          title="公告总数"
          icon="ri-megaphone-line"
          :value="stats.announcementTotal"
          gradient="#fb923c,#f97316"
        />
        <DashboardStatCard
          title="邮件总数"
          icon="ri-mail-send-line"
          :value="stats.mailTotal"
          gradient="#60a5fa,#2563eb"
        />
        <DashboardStatCard
          title="附件总数"
          icon="ri-attachment-line"
          :value="stats.attachmentTotal"
          gradient="#a855f7,#7c3aed"
        />
      </div>

      <div class="content-grid">
        <div class="left-column">
          <section class="card trend-card">
            <header class="card-header">
              <h3>订单趋势概览</h3>
            </header>
            <div v-if="orderTrend.length" class="line-chart">
              <svg class="line-chart-svg" viewBox="0 0 800 180">
                <!-- 网格线 -->
                <defs>
                  <pattern id="grid" width="60" height="30" patternUnits="userSpaceOnUse">
                    <path d="M 60 0 L 0 0 0 30" fill="none" stroke="#e2e8f0" stroke-width="1"/>
                  </pattern>
                </defs>
                <rect width="100%" height="100%" fill="url(#grid)"/>
                
                <!-- 折线 -->
                <polyline
                  :points="lineChartPoints"
                  fill="none"
                  stroke="#22c55e"
                  stroke-width="3"
                  class="line-path"
                />
                
                <!-- 数据点 -->
                <circle
                  v-for="(point, index) in lineChartPointsArray"
                  :key="index"
                  :cx="point.x"
                  :cy="point.y"
                  r="4"
                  fill="#22c55e"
                  stroke="#fff"
                  stroke-width="2"
                  class="line-point"
                />
                
                <!-- 数值标签（只显示有数据的点，避免拥挤） -->
                <template v-for="(point, index) in lineChartPointsArray" :key="index">
                  <text
                    v-if="point.value > 0"
                    :x="point.x"
                    :y="point.y - 12"
                    text-anchor="middle"
                    font-size="11"
                    font-weight="600"
                    fill="#0f172a"
                    class="line-value"
                  >
                    {{ point.value }}
                  </text>
                </template>
                
                <!-- X轴标签（月份） -->
                <text
                  v-for="(item, index) in orderTrend"
                  :key="'label-' + index"
                  :x="lineChartPointsArray[index]?.x || 0"
                  y="175"
                  text-anchor="middle"
                  font-size="11"
                  fill="#64748b"
                  class="line-label"
                >
                  {{ item.label }}
                </text>
              </svg>
            </div>
            <p v-else class="empty-tip">暂无订单数据</p>
          </section>

          <div class="bottom-row">
            <section class="card announcements-card">
              <header class="card-header">
                <h3>最新公告</h3>
                <RouterLink to="/admin/announcements" class="more-link">查看全部</RouterLink>
              </header>
              <div v-if="latestAnnouncements.length" class="announcement-list">
                <article
                  v-for="announcement in latestAnnouncements"
                  :key="announcement.id"
                  class="announcement-item"
                >
                  <h4>{{ announcement.title || '公告' }}</h4>
                  <p class="announcement-content">
                    {{ getAnnouncementSnippet(announcement.content) }}
                  </p>
                  <footer>
                    <span class="announcement-author">
                      发布人：{{ announcement.adminId || '管理员' }}
                    </span>
                    <time>{{ formatDate(announcement.createTime) }}</time>
                  </footer>
                </article>
              </div>
              <p v-else class="empty-tip">暂无公告，快去发布第一条吧！</p>
            </section>

            <section class="card system-card">
              <header class="card-header">
                <h3>系统信息</h3>
              </header>
              <table class="system-table">
                <tbody>
                  <tr>
                    <th>运行环境</th>
                    <td>JDK 17 / Spring Boot 3.0.2 / Vue 3 + Vite</td>
                  </tr>
                  <tr>
                    <th>数据库</th>
                    <td>MySQL 8.x + Redis 5.x</td>
                  </tr>
                  <tr>
                    <th>上传附件大小限制</th>
                    <td>200MB</td>
                  </tr>
                  <tr>
                    <th>上传图片大小限制</th>
                    <td>2MB</td>
                  </tr>
                  <tr>
                    <th>权限有效时间</th>
                    <td>30 分钟</td>
                  </tr>
                </tbody>
              </table>
            </section>
          </div>
        </div>

        <div class="right-column">
          <!-- 核心业务数据对比 -->
          <section class="card chart-card">
            <header class="card-header">
              <h3>核心业务数据</h3>
            </header>
            <div class="stats-comparison">
              <div class="stat-item">
                <div class="stat-label">用户总数</div>
                <div class="stat-bar">
                  <div
                    class="stat-bar-fill user-bar"
                    :style="{ width: getStatPercentage(stats.userTotal) + '%' }"
                  ></div>
                </div>
                <div class="stat-value">{{ stats.userTotal }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">商品总数</div>
                <div class="stat-bar">
                  <div
                    class="stat-bar-fill product-bar"
                    :style="{ width: getStatPercentage(stats.productTotal) + '%' }"
                  ></div>
                </div>
                <div class="stat-value">{{ stats.productTotal }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">订单总数</div>
                <div class="stat-bar">
                  <div
                    class="stat-bar-fill order-bar"
                    :style="{ width: getStatPercentage(stats.orderTotal) + '%' }"
                  ></div>
                </div>
                <div class="stat-value">{{ stats.orderTotal }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">评论总数</div>
                <div class="stat-bar">
                  <div
                    class="stat-bar-fill comment-bar"
                    :style="{ width: getStatPercentage(stats.commentTotal) + '%' }"
                  ></div>
                </div>
                <div class="stat-value">{{ stats.commentTotal }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">商品分类</div>
                <div class="stat-bar">
                  <div
                    class="stat-bar-fill category-bar"
                    :style="{ width: getStatPercentage(stats.categoryTotal) + '%' }"
                  ></div>
                </div>
                <div class="stat-value">{{ stats.categoryTotal }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">收藏总数</div>
                <div class="stat-bar">
                  <div
                    class="stat-bar-fill collect-bar"
                    :style="{ width: getStatPercentage(stats.collectTotal) + '%' }"
                  ></div>
                </div>
                <div class="stat-value">{{ stats.collectTotal }}</div>
              </div>
            </div>
            <!-- 总销售额单独显示 -->
            <div class="sales-amount">
              <div class="sales-label">总销售额</div>
              <div class="sales-value">¥{{ formatSalesAmount(stats.totalSalesAmount) }}</div>
            </div>
          </section>

          <!-- 订单时间分布（按星期） -->
          <section class="card chart-card">
            <header class="card-header">
              <h3>本周订单分布</h3>
            </header>
            <div class="week-distribution">
              <div 
                v-for="(item, index) in weekOrderDataWithPercent" 
                :key="index"
                class="week-item"
                :class="{ 'week-item-empty': item.value === 0, 'week-item-today': item.isToday }"
              >
                <div class="week-day">{{ item.dayName }}</div>
                <div class="week-bar-wrapper">
                  <div 
                    class="week-bar" 
                    :style="{ 
                      height: item.percent + '%',
                      background: item.color 
                    }"
                    :title="`${item.dayName}: ${item.value}单`"
                  ></div>
                </div>
                <div class="week-value">{{ item.value }}</div>
              </div>
            </div>
            <div class="week-summary" v-if="weekTotal > 0">
              <span class="summary-label">本周总计：</span>
              <span class="summary-value">{{ weekTotal }} 单</span>
            </div>
          </section>
        </div>
      </div>

      <footer class="team-card card">
        <header class="card-header">
          <h3>开发团队</h3>
        </header>
        <p>xqx (2350064257@qq.com)</p>
        <small>© 2025 鲜采直送</small>
      </footer>
    </template>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { fetchAdminDashboard } from '@/apis/admin';
import DashboardStatCard from '@/views/admin/components/DashboardStatCard.vue';

const adminName = computed(() => localStorage.getItem('adminName') || '');
const loading = ref(false);
const errorMessage = ref('');

const stats = reactive({
  todayOrderCount: 0,
  weekOrderCount: 0,
  monthOrderCount: 0,
  announcementTotal: 0,
  mailTotal: 0,
  attachmentTotal: 0,
  adminTotal: 0,
  productTotal: 0,
  userTotal: 0,
  orderTotal: 0,
  commentTotal: 0,
  categoryTotal: 0,
  collectTotal: 0,
  totalSalesAmount: 0,
});

const latestAnnouncements = ref([]);
const orderCountByMonth = ref([]);
const orderCountByDayOfWeek = ref([]);

// 计算总订单数
const totalOrderCount = computed(() => {
  return stats.todayOrderCount + stats.weekOrderCount + stats.monthOrderCount;
});

// 获取统计百分比（用于条形图）
const getStatPercentage = (value) => {
  const maxValue = Math.max(
    stats.userTotal,
    stats.productTotal,
    stats.orderTotal,
    stats.commentTotal,
    stats.categoryTotal,
    stats.collectTotal,
    1
  );
  return maxValue > 0 ? Math.min((value / maxValue) * 100, 100) : 0;
};

// 格式化销售额
const formatSalesAmount = (amount) => {
  if (!amount && amount !== 0) return '0.00';
  const num = typeof amount === 'string' ? parseFloat(amount) : amount;
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

// 计算本周订单按星期分布数据
const weekOrderData = computed(() => {
  const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  const colors = [
    '#8b5cf6', '#3b82f6', '#22c55e', '#f59e0b', 
    '#ef4444', '#ec4899', '#06b6d4'
  ];
  
  // 创建星期数据映射（MySQL的DAYOFWEEK返回1-7，1为周日）
  const dayDataMap = new Map();
  if (Array.isArray(orderCountByDayOfWeek.value)) {
    orderCountByDayOfWeek.value.forEach((item) => {
      const dayOfWeek = Number(
        item.dayOfWeek || 
        item.DAYOFWEEK || 
        item.day_of_week || 
        0
      );
      const count = Number(
        item.orderCount || 
        item.ORDERCOUNT || 
        item.order_count || 
        item.count || 
        0
      );
      if (dayOfWeek >= 1 && dayOfWeek <= 7) {
        dayDataMap.set(dayOfWeek, count);
      }
    });
  }
  
  // 获取当前是星期几（JavaScript的getDay()返回0-6，0为周日）
  const today = new Date().getDay();
  
  // 生成7天的数据
  return dayNames.map((dayName, index) => {
    // MySQL的DAYOFWEEK：1=周日, 2=周一, ..., 7=周六
    // 数组索引：0=周日, 1=周一, ..., 6=周六
    // 所以MySQL的dayOfWeek = index + 1（但index=0时是周日，对应MySQL的1）
    const mysqlDayOfWeek = index + 1;
    const value = dayDataMap.get(mysqlDayOfWeek) || 0;
    const isToday = index === today;
    
    return {
      dayName,
      value,
      color: colors[index],
      isToday,
    };
  });
});

// 计算本周总订单数
const weekTotal = computed(() => {
  return weekOrderData.value.reduce((sum, item) => sum + item.value, 0);
});

// 计算每个星期的百分比（相对于本周最高值）
const weekOrderDataWithPercent = computed(() => {
  const maxValue = Math.max(...weekOrderData.value.map(item => item.value), 1);
  return weekOrderData.value.map(item => ({
    ...item,
    percent: maxValue > 0 ? Math.round((item.value / maxValue) * 100) : 0,
  }));
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

const getAnnouncementSnippet = (content) => {
  if (!content) return '暂无内容';
  const cleaned = String(content).trim().replace(/\s+/g, ' ');
  return cleaned.length > 80 ? `${cleaned.slice(0, 80)}…` : cleaned;
};

// 处理月度订单数据，确保12个月都有数据
const orderTrend = computed(() => {
  const monthNames = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
  
  // 创建月份数据映射
  const monthDataMap = new Map();
  if (Array.isArray(orderCountByMonth.value)) {
    orderCountByMonth.value.forEach((item) => {
      // 兼容不同的字段名（MyBatis可能返回不同的键名）
      const month = Number(
        item.month || 
        item.MONTH || 
        item.monthNumber || 
        item.month_number || 
        0
      );
      const count = Number(
        item.orderCount || 
        item.ORDERCOUNT || 
        item.order_count || 
        item.count || 
        0
      );
      if (month >= 1 && month <= 12) {
        monthDataMap.set(month, count);
      }
    });
  }
  
  // 生成12个月的数据，没有数据的月份显示0
  return monthNames.map((name, index) => {
    const month = index + 1;
    return {
      label: name,
      value: monthDataMap.get(month) || 0,
      month: month,
    };
  });
});

// 计算折线图数据点
const lineChartPoints = computed(() => {
  const items = orderTrend.value;
  if (items.length === 0) return '';
  
  const maxValue = Math.max(...items.map((item) => item.value), 1);
  const width = 800;
  const height = 150;
  const padding = 50;
  const chartWidth = width - padding * 2;
  const chartHeight = height - padding * 2;
  
  return items.map((item, index) => {
    const x = padding + (index * chartWidth / Math.max(items.length - 1, 1));
    const y = padding + chartHeight - ((item.value / maxValue) * chartHeight);
    return `${x},${y}`;
  }).join(' ');
});

// 计算折线图数据点数组（用于标签定位）
const lineChartPointsArray = computed(() => {
  const items = orderTrend.value;
  if (items.length === 0) return [];
  
  const maxValue = Math.max(...items.map((item) => item.value), 1);
  const width = 800;
  const height = 150;
  const padding = 50;
  const chartWidth = width - padding * 2;
  const chartHeight = height - padding * 2;
  
  return items.map((item, index) => {
    const x = padding + (index * chartWidth / Math.max(items.length - 1, 1));
    const y = padding + chartHeight - ((item.value / maxValue) * chartHeight);
    return { x, y, value: item.value };
  });
});

const loadDashboard = async () => {
  loading.value = true;
  errorMessage.value = '';
  try {
    const res = await fetchAdminDashboard();
    if (res?.code === 0 && res.data) {
      Object.assign(stats, {
        todayOrderCount: res.data.todayOrderCount ?? 0,
        weekOrderCount: res.data.weekOrderCount ?? 0,
        monthOrderCount: res.data.monthOrderCount ?? 0,
        announcementTotal: res.data.announcementTotal ?? 0,
        mailTotal: res.data.mailTotal ?? 0,
        attachmentTotal: res.data.attachmentTotal ?? 0,
        adminTotal: res.data.adminTotal ?? 0,
        productTotal: res.data.productTotal ?? 0,
        userTotal: res.data.userTotal ?? 0,
        orderTotal: res.data.orderTotal ?? 0,
        commentTotal: res.data.commentTotal ?? 0,
        categoryTotal: res.data.categoryTotal ?? 0,
        collectTotal: res.data.collectTotal ?? 0,
        totalSalesAmount: res.data.totalSalesAmount ?? 0,
      });
      
      // 加载月度订单统计
      orderCountByMonth.value = res.data.orderCountByMonth || [];
      // 加载按星期分布统计
      orderCountByDayOfWeek.value = res.data.orderCountByDayOfWeek || [];
      
      if (res.data.latestAnnouncements?.list) {
        latestAnnouncements.value = res.data.latestAnnouncements.list.slice(0, 5);
      } else {
        latestAnnouncements.value = [];
      }
    } else {
      errorMessage.value = res?.msg || '加载仪表盘数据失败，请稍后再试。';
      latestAnnouncements.value = [];
    }
  } catch (error) {
    console.error('加载仪表盘数据失败', error);
    errorMessage.value = '网络错误，无法获取仪表盘数据。';
    latestAnnouncements.value = [];
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadDashboard();
});
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 32px;
}

.welcome-card {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 50%, #0f766e 100%);
  color: #ecfdf5;
  border-radius: 20px;
  padding: 32px 36px;
  box-shadow: 0 20px 50px rgba(15, 118, 110, 0.35);
}

.welcome-card h2 {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 700;
}

.welcome-card p {
  margin: 0;
  font-size: 16px;
  opacity: 0.85;
}

.stats-grid {
  display: grid;
  gap: 20px;
  grid-template-columns: repeat(3, minmax(200px, 1fr));
}

.content-grid {
  display: grid;
  gap: 24px;
  grid-template-columns: 2fr 1fr;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.12);
  padding: 24px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.error-card {
  border: 1px solid rgba(248, 113, 113, 0.45);
  background: rgba(254, 226, 226, 0.7);
  color: #7f1d1d;
  gap: 12px;
  align-items: flex-start;
}

.error-card h3 {
  margin: 0;
}

.trend-card {
  min-height: 240px;
  justify-content: space-between;
}

/* 折线图 */
.line-chart {
  width: 100%;
  height: 220px;
  padding: 10px 0;
  overflow-x: auto;
}

.line-chart-svg {
  width: 100%;
  min-width: 800px;
  height: 100%;
}

.line-path {
  transition: all 0.6s ease;
  filter: drop-shadow(0 2px 4px rgba(34, 197, 94, 0.2));
}

.line-point {
  transition: all 0.3s ease;
  cursor: pointer;
}

.line-point:hover {
  r: 7;
  filter: drop-shadow(0 0 8px rgba(34, 197, 94, 0.5));
}

.line-value {
  transition: all 0.3s ease;
}

.line-label {
  font-weight: 500;
}

.more-link {
  font-size: 14px;
  color: #10b981;
  text-decoration: none;
  transition: color 0.2s ease;
}

.more-link:hover {
  color: #059669;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.announcement-item {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(59, 130, 246, 0.02));
  padding: 18px 20px;
  border-radius: 16px;
  box-shadow: inset 0 1px 0 rgba(59, 130, 246, 0.12);
}

.announcement-item h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #1d4ed8;
}

.announcement-content {
  margin: 0 0 10px;
  font-size: 14px;
  color: #334155;
  line-height: 1.6;
}

.announcement-item footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #64748b;
}

.empty-tip {
  text-align: center;
  color: #94a3b8;
  margin: 40px 0;
}

.system-card {
  padding: 16px 20px;
}

.system-card .card-header {
  margin-bottom: 12px;
}

.system-card .card-header h3 {
  font-size: 16px;
}

.system-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.system-table th,
.system-table td {
  padding: 8px 10px;
  text-align: left;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.system-table th {
  width: 40%;
  color: #64748b;
  font-weight: 500;
  font-size: 12px;
}

.system-table td {
  color: #0f172a;
  font-weight: 400;
}

.right-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.chart-card {
  min-height: 280px;
}

/* 数据统计对比图 */
.stats-comparison {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 8px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-label {
  width: 70px;
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.stat-bar {
  flex: 1;
  height: 24px;
  background: rgba(226, 232, 240, 0.5);
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.stat-bar-fill {
  height: 100%;
  border-radius: 12px;
  transition: width 0.6s ease;
}

.user-bar {
  background: linear-gradient(90deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.product-bar {
  background: linear-gradient(90deg, #8b5cf6 0%, #7c3aed 100%);
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.3);
}

.order-bar {
  background: linear-gradient(90deg, #22c55e 0%, #16a34a 100%);
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.3);
}

.comment-bar {
  background: linear-gradient(90deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.category-bar {
  background: linear-gradient(90deg, #ec4899 0%, #db2777 100%);
  box-shadow: 0 2px 8px rgba(236, 72, 153, 0.3);
}

.collect-bar {
  background: linear-gradient(90deg, #06b6d4 0%, #0891b2 100%);
  box-shadow: 0 2px 8px rgba(6, 182, 212, 0.3);
}

.stat-value {
  width: 50px;
  text-align: right;
  font-weight: 600;
  font-size: 14px;
  color: #0f172a;
}

/* 总销售额样式 */
.sales-amount {
  margin-top: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.sales-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 8px;
  font-weight: 500;
}

.sales-value {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 本周订单分布（按星期） */
.week-distribution {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 8px;
  margin: 20px 0;
  padding: 0 4px;
  min-height: 180px;
}

.week-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.week-item:hover {
  transform: translateY(-4px);
}

.week-item-today {
  position: relative;
}

.week-item-today::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 6px;
  background: #ef4444;
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(239, 68, 68, 0.6);
}

.week-item-empty {
  opacity: 0.4;
}

.week-day {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  text-align: center;
}

.week-item-today .week-day {
  color: #ef4444;
  font-weight: 700;
}

.week-bar-wrapper {
  width: 100%;
  height: 120px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  position: relative;
}

.week-bar {
  width: 100%;
  min-height: 4px;
  border-radius: 4px 4px 0 0;
  transition: height 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: relative;
}

.week-bar:hover {
  opacity: 0.8;
  transform: scaleY(1.05);
  transform-origin: bottom;
}

.week-item-today .week-bar {
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
  border: 2px solid #ffffff;
  border-bottom: none;
}

.week-value {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
  text-align: center;
  min-height: 20px;
}

.week-item-today .week-value {
  color: #ef4444;
}

.week-summary {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 2px solid #e2e8f0;
  text-align: center;
}

.summary-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.summary-value {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin-left: 8px;
}

.team-card {
  text-align: center;
  gap: 6px;
  background: linear-gradient(180deg, rgba(244, 244, 249, 0.9), rgba(255, 255, 255, 0.9));
}

.team-card p {
  margin: 0;
  color: #334155;
}

.team-card small {
  color: #94a3b8;
}

@media (max-width: 1080px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .bottom-row {
    grid-template-columns: 1fr;
  }

  .trend-chart {
    gap: 16px;
  }
}

@media (max-width: 720px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
}
</style>
