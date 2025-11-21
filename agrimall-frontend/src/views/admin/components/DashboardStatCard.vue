<template>
  <article class="stat-card" :style="gradientStyle">
    <div class="stat-icon">
      <i :class="icon"></i>
    </div>
    <div class="stat-info">
      <h4>{{ title }}</h4>
      <p>{{ displayValue }}</p>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  value: {
    type: [Number, String],
    default: 0,
  },
  icon: {
    type: String,
    default: 'ri-bar-chart-line',
  },
  gradient: {
    type: String,
    default: '#0f172a,#1e293b',
  },
});

const displayValue = computed(() => {
  if (props.value === null || props.value === undefined) {
    return 0;
  }
  if (typeof props.value === 'number') {
    return new Intl.NumberFormat().format(props.value);
  }
  return props.value;
});

const gradientStyle = computed(() => {
  const colors = props.gradient.split(',');
  if (colors.length >= 2) {
    return {
      background: `linear-gradient(135deg, ${colors[0]}, ${colors[1]})`,
    };
  }
  return {
    background: colors[0] || '#0f172a',
  };
});
</script>

<style scoped>
.stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 20px;
  color: #fff;
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.24);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  overflow: hidden;
}

.stat-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.18), rgba(255, 255, 255, 0));
  opacity: 0.35;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 22px 30px rgba(15, 23, 42, 0.3);
}

.stat-icon {
  position: relative;
  z-index: 1;
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
}

.stat-info {
  position: relative;
  z-index: 1;
}

.stat-info h4 {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  opacity: 0.9;
}

.stat-info p {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 0.5px;
}
</style>
