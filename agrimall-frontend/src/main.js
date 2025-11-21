import { createApp } from 'vue';
import '@/assets/home/css/common.css';
import '@/assets/home/css/css.css';
import App from './App.vue';
import router from './router';

createApp(App).use(router).mount('#app');
