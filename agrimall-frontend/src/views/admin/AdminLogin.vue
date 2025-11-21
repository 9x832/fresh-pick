<template>
  <div class="admin-login">
    <div class="login-container">
      <section class="login-info">
        <div class="brand">
          <span class="pill">Fresh Pick Admin</span>
          <h1>鲜采直送后台</h1>
          <p>集中管理商品、订单与用户数据，实时掌握业务动态。</p>
        </div>
        <ul class="highlights">
          <li>仪表盘概览核心指标，重要事件第一时间掌握</li>
          <li>完善的权限体系，支持角色与菜单自由配置</li>
          <li>订单、评论、公告等模块一站式管理</li>
        </ul>
        <footer>
          <p class="tips">推荐在公司网络环境下使用，保障数据安全。</p>
        </footer>
        <div class="illustration">
          <img src="@/assets/home/images/login-illustration.png" alt="admin illustration" />
        </div>
      </section>

      <section class="login-panel">
        <header>
          <h2>管理员登录</h2>
          <p>请输入后台账户信息完成验证</p>
        </header>

        <form class="login-form" @submit.prevent="handleSubmit">
          <div class="form-item">
            <label for="admin-name">用户名</label>
            <input
              id="admin-name"
              v-model.trim="name"
              type="text"
              placeholder="请输入管理员用户名"
              autocomplete="username"
            />
          </div>
          <div class="form-item">
            <label for="admin-password">密码</label>
            <input
              id="admin-password"
              v-model.trim="password"
              type="password"
              placeholder="请输入登录密码"
              autocomplete="current-password"
            />
          </div>
          <div class="form-item captcha-item">
            <div class="captcha-input">
              <label for="admin-captcha">验证码</label>
              <input
                id="admin-captcha"
                v-model.trim="captcha"
                type="text"
                placeholder="请输入验证码"
                maxlength="6"
                autocomplete="one-time-code"
              />
            </div>
            <button
              type="button"
              class="captcha-button"
              @click="refreshCaptcha"
              title="点击刷新验证码"
            >
              <img :src="captchaUrl" alt="验证码" />
            </button>
          </div>
          <div class="form-actions">
            <button type="submit" :disabled="loading">
              {{ loading ? '登录中...' : '登录' }}
            </button>
            <button type="button" class="reset-button" @click="resetForm">重置</button>
          </div>
        </form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { adminLogin } from '@/apis/admin';

const router = useRouter();
const route = useRoute();

const name = ref('');
const password = ref('');
const captcha = ref('');
const loading = ref(false);
const captchaSeed = ref(Date.now());

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const captchaUrl = computed(
  () =>
    `${API_BASE_URL}/api/common/captcha?method=admin_login&vl=4&fs=21&w=110&h=36&t=${captchaSeed.value}`,
);

const refreshCaptcha = () => {
  captchaSeed.value = Date.now();
};

const resetForm = () => {
  name.value = '';
  password.value = '';
  captcha.value = '';
  refreshCaptcha();
};

const handleSubmit = async () => {
  if (!name.value || !password.value || !captcha.value) {
    window.alert('请完整填写用户名、密码和验证码');
    return;
  }
  loading.value = true;
  try {
    const res = await adminLogin({
      name: name.value,
      password: password.value,
      captcha: captcha.value,
    });
    if (res?.code === 0) {
      localStorage.setItem('adminLoggedIn', 'true');
      localStorage.setItem('adminName', name.value);
      window.alert(res?.msg || '登录成功');
      const redirect = route.query?.redirect;
      router.push(
        typeof redirect === 'string' && redirect && redirect !== '/admin/login'
          ? redirect
          : '/admin',
      );
    } else {
      window.alert(res?.msg || '登录失败，请检查输入信息');
      refreshCaptcha();
    }
  } catch (error) {
    console.error('管理员登录失败', error);
    window.alert('网络错误，登录失败！');
    refreshCaptcha();
  } finally {
    loading.value = false;
    captcha.value = '';
  }
};
</script>

<style scoped>
.admin-login {
  min-height: 100vh;
  background: radial-gradient(circle at top left, rgba(62, 110, 180, 0.2), transparent 42%),
    radial-gradient(circle at bottom right, rgba(52, 199, 89, 0.22), transparent 38%),
    linear-gradient(135deg, #0f172a 0%, #1e293b 28%, #0f172a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(24px, 6vw, 40px);
  color: #0f172a;
}

.login-container {
  width: min(960px, 100%);
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(18px, 4vw, 32px);
}

.login-panel,
.login-info {
  border-radius: clamp(20px, 3vw, 28px);
  padding: clamp(24px, 4vw, 36px);
  box-shadow: 0 30px 70px rgba(15, 23, 42, 0.42);
  backdrop-filter: blur(10px);
  background: rgba(15, 23, 42, 0.78);
  color: rgba(226, 232, 240, 0.92);
  display: flex;
  flex-direction: column;
}

.login-info {
  background: linear-gradient(140deg, rgba(59, 130, 246, 0.24), rgba(16, 185, 129, 0.22));
  color: #f8fafc;
  gap: clamp(20px, 3.6vw, 26px);
}

.login-info .brand {
  display: flex;
  flex-direction: column;
  gap: clamp(12px, 2.5vw, 16px);
}

.login-info .pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.32);
  font-size: 13px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  font-weight: 600;
}

.login-info h1 {
  margin: 0;
  font-size: clamp(30px, 4.2vw, 38px);
  line-height: 1.2;
}

.login-info p {
  margin: 0;
  color: rgba(241, 245, 249, 0.82);
}

.login-info .highlights {
  margin: 0;
  padding-left: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: rgba(226, 232, 240, 0.85);
}

.login-info footer {
  margin-top: auto;
}

.tips {
  margin: 0;
  font-size: 13px;
  color: rgba(226, 232, 240, 0.68);
}

.illustration {
  margin-top: clamp(20px, 4vw, 32px);
  border-radius: clamp(16px, 2.8vw, 22px);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.22);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.25);
}

.illustration img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  mix-blend-mode: lighten;
}

.login-panel header h2 {
  margin: 0;
  font-size: clamp(26px, 3.6vw, 32px);
}

.login-panel header p {
  margin: 8px 0 0;
  color: rgba(226, 232, 240, 0.72);
}

.login-form {
  margin-top: clamp(16px, 3vw, 24px);
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vw, 20px);
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 600;
  color: rgba(226, 232, 240, 0.86);
}

.form-item input {
  height: 48px;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  padding: 0 18px;
  background: rgba(148, 163, 184, 0.15);
  color: #f1f5f9;
  font-size: 16px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.form-item input:focus {
  outline: none;
  border-color: rgba(96, 165, 250, 0.65);
  background: rgba(96, 165, 250, 0.24);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.18);
}

.captcha-item {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.captcha-input {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.captcha-button {
  border: none;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(148, 163, 184, 0.18);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.captcha-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 22px rgba(15, 23, 42, 0.28);
}

.captcha-button img {
  display: block;
  width: 118px;
  height: 48px;
  object-fit: cover;
}

.form-actions {
  display: flex;
  gap: 12px;
}

.form-actions button {
  flex: 1;
  height: 48px;
  border-radius: 14px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.form-actions button:first-child {
  background: linear-gradient(135deg, #38bdf8 0%, #22d3ee 50%, #34d399 100%);
  color: #0f172a;
  box-shadow: 0 16px 30px rgba(56, 189, 248, 0.32);
}

.form-actions button:first-child:hover {
  transform: translateY(-1px);
  box-shadow: 0 24px 40px rgba(56, 189, 248, 0.4);
}

.form-actions button:disabled {
  opacity: 0.65;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.reset-button {
  background: rgba(148, 163, 184, 0.22);
  color: rgba(226, 232, 240, 0.9);
}

.reset-button:hover {
  background: rgba(148, 163, 184, 0.28);
  transform: translateY(-1px);
}

@media (max-width: 920px) {
  .login-container {
    grid-template-columns: 1fr;
  }

  .login-info {
    order: 2;
  }

  .login-panel {
    order: 1;
  }
}

@media (max-width: 520px) {
  .login-panel,
  .login-info {
    padding: clamp(22px, 8vw, 28px);
  }

  .captcha-button img {
    width: 104px;
  }
}
</style>

