<template>
  <div class="register-page">
    <HomeHeader
      :username="headerUsername"
      :cart-total="cartTotal"
      @logout="handleLogout"
    />
    <main class="register-shell">
      <section class="register-card">
        <header>
          <h1>创建账户</h1>
          <p>填写基础信息即可开通鲜采直送账户，随时查看订单与地址。</p>
        </header>

        <form class="register-form" @submit.prevent="handleRegister">
          <div class="field">
            <label for="username">用户名称</label>
            <input
              id="username"
              v-model.trim="form.username"
              type="text"
              placeholder="请输入用户名称"
              autocomplete="username"
              required
            />
          </div>

          <div class="field">
            <label for="password">登录密码</label>
            <input
              id="password"
              v-model.trim="form.password"
              type="password"
              placeholder="请输入密码"
              autocomplete="new-password"
              required
            />
          </div>

          <div class="field">
            <label for="confirmPassword">确认密码</label>
            <input
              id="confirmPassword"
              v-model.trim="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              autocomplete="new-password"
              required
            />
          </div>

          <div class="field-group">
            <label for="captcha">图形验证码</label>
            <div class="captcha-row">
              <input
                id="captcha"
                v-model.trim="form.captcha"
                type="text"
                placeholder="请输入验证码"
                maxlength="4"
                required
              />
              <button type="button" class="captcha-btn" @click="refreshCaptcha">
                <img :src="captchaImg" alt="captcha" />
              </button>
            </div>
          </div>

          <div class="field">
            <label for="phone">手机号码</label>
            <input
              id="phone"
              v-model.trim="form.phone"
              type="tel"
              placeholder="请输入 11 位手机号"
              maxlength="11"
              required
            />
          </div>

          <div class="field">
            <label for="email">邮箱</label>
            <input
              id="email"
              v-model.trim="form.email"
              type="email"
              placeholder="用于接收订单通知"
            />
          </div>

          <button class="submit-btn" type="submit" :disabled="loading">
            {{ loading ? '注册中...' : '立即注册' }}
          </button>
        </form>

        <footer class="switch-login">
          <span>已有账户？</span>
          <RouterLink to="/login">直接登录</RouterLink>
        </footer>
      </section>

      <section class="register-visual">
        <div class="badge">一分钟完成注册</div>
        <h2>同步购物车与订单信息，享受当天鲜达体验</h2>
        <ul>
          <li>支持订单提醒、地址管理、收藏同步</li>
          <li>图形验证码校验，保障账号安全</li>
          <li>注册成功后可立即浏览并下单</li>
        </ul>
        <div class="visual-wrapper">
          <img :src="registerImg" alt="register illustration" />
        </div>
      </section>
    </main>
    <HomeFooter />
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { homeRegister } from '@/apis/home';
import HomeHeader from '@/components/home/HomeHeader.vue';
import HomeFooter from '@/components/home/HomeFooter.vue';

const router = useRouter();

const headerUsername = ref(localStorage.getItem('username') || '');
const cartTotal = ref(Number(localStorage.getItem('cartTotal') || 0));

const loading = ref(false);
const captchaKey = ref(Date.now());

const captchaImg = computed(
  () =>
    `http://localhost:8080/api/common/captcha?method=user_register&vl=4&fs=21&w=120&h=40&t=${captchaKey.value}`,
);

const registerImg = new URL('@/assets/home/images/login-illustration.png', import.meta.url).href;

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  captcha: '',
  phone: '',
  email: '',
});

const resetForm = () => {
  form.username = '';
  form.password = '';
  form.confirmPassword = '';
  form.captcha = '';
  form.email = '';
  form.phone = '';
  captchaKey.value = Date.now();
};

const refreshCaptcha = () => {
  captchaKey.value = Date.now();
};

const validateForm = () => {
  if (!form.username || !form.password || !form.confirmPassword || !form.captcha || !form.phone) {
    window.alert('请完整填写必填项');
    return false;
  }
  if (!/^\d{11}$/.test(form.phone)) {
    window.alert('请输入11位手机号码');
    return false;
  }
  if (form.password !== form.confirmPassword) {
    window.alert('两次输入的密码不一致');
    return false;
  }
  if (form.password.length < 6) {
    window.alert('密码至少需要 6 位');
    return false;
  }
  return true;
};

const handleRegister = async () => {
  if (loading.value) return;
  if (!validateForm()) return;
  loading.value = true;
  try {
    const res = await homeRegister({
      username: form.username,
      password: form.password,
      repassword: form.confirmPassword,
      captcha: form.captcha,
      phone: form.phone,
      email: form.email,
    });
    if (res?.code === 0) {
      window.alert(res?.msg || '注册成功，请登录');
      resetForm();
      router.push('/login');
    } else {
      window.alert(res?.msg || '注册失败，请重试');
      refreshCaptcha();
    }
  } catch (error) {
    console.error('注册失败', error);
    window.alert('网络错误，注册失败');
    refreshCaptcha();
  } finally {
    loading.value = false;
  }
};

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  localStorage.removeItem('cartTotal');
  localStorage.removeItem('email');
  localStorage.removeItem('phone');
  localStorage.removeItem('headPic');
  localStorage.removeItem('userId');
  headerUsername.value = '';
  cartTotal.value = 0;
  router.push('/login');
};
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f3faf3 0%, #ffffff 45%, #f6f8fb 100%);
  display: flex;
  flex-direction: column;
}

.register-shell {
  flex: 1;
  width: min(1020px, 100%);
  margin: 0 auto;
  padding: clamp(24px, 6vw, 32px) clamp(18px, 6vw, 40px) clamp(32px, 7vw, 48px);
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(18px, 4vw, 28px);
  align-items: stretch;
}

.register-card,
.register-visual {
  border-radius: 22px;
  padding: clamp(24px, 4.8vw, 38px);
  box-shadow: 0 22px 44px rgba(15, 23, 42, 0.1);
  background: rgba(255, 255, 255, 0.96);
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3vw, 24px);
}

.register-card header h1 {
  margin: 0;
  font-size: clamp(28px, 4vw, 34px);
  color: #1f3d2a;
}

.register-card header p {
  margin: 8px 0 0;
  color: rgba(31, 61, 42, 0.65);
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 3.4vw, 20px);
}

.field,
.field-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: rgba(31, 61, 42, 0.72);
  font-weight: 600;
}

.field input,
.field-group input {
  height: 46px;
  padding: 0 16px;
  border-radius: 14px;
  border: 1px solid rgba(46, 125, 72, 0.2);
  background: rgba(46, 125, 72, 0.06);
  font-size: 15px;
  color: #1f3d2a;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.field input:focus,
.field-group input:focus {
  outline: none;
  border-color: rgba(46, 125, 72, 0.4);
  box-shadow: 0 0 0 4px rgba(46, 125, 72, 0.18);
  background: rgba(46, 125, 72, 0.1);
}

.captcha-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.captcha-row input {
  flex: 1;
}

.captcha-btn {
  width: 118px;
  height: 46px;
  border: none;
  border-radius: 14px;
  background: rgba(46, 125, 72, 0.1);
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.captcha-btn img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.submit-btn {
  margin-top: 8px;
  height: 50px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 20px 36px rgba(46, 125, 72, 0.22);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 24px 44px rgba(46, 125, 72, 0.26);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.switch-login {
  margin-top: auto;
  font-size: 14px;
  color: rgba(31, 61, 42, 0.6);
  display: flex;
  align-items: center;
  gap: 6px;
}

.switch-login a {
  color: #2e7d48;
  font-weight: 600;
  text-decoration: none;
}

.register-visual {
  background: linear-gradient(135deg, rgba(46, 125, 72, 0.2), rgba(60, 167, 92, 0.16));
  color: #1f3d2a;
}

.register-visual .badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.4);
  color: #2e7d48;
  font-weight: 600;
  font-size: 13px;
}

.register-visual h2 {
  margin: 0;
  font-size: clamp(24px, 3.5vw, 30px);
}

.register-visual ul {
  margin: 0;
  padding-left: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: rgba(31, 61, 42, 0.7);
}

.visual-wrapper {
  margin-top: auto;
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.55);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.3);
  max-height: clamp(200px, 28vw, 260px);
}

.visual-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 1024px) {
  .register-shell {
    grid-template-columns: 1fr;
    padding: clamp(24px, 7vw, 32px) clamp(16px, 6vw, 30px) clamp(32px, 8vw, 40px);
  }

  .register-visual {
    order: 2;
  }

  .register-card {
    order: 1;
  }
}

@media (max-width: 640px) {
  .register-page {
    margin-top: calc(-1 * (var(--header-height) + clamp(2px, 1.5vw, 6px)));
  }

  .register-card,
  .register-visual {
    padding: clamp(22px, 8vw, 30px);
  }

  .field input,
  .field-group input {
    height: 48px;
  }
}
</style>

