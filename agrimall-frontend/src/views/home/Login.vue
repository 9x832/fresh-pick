<template>
  <div class="login-page">
    <HomeHeader
      :username="headerUsername"
      :cart-total="cartTotal"
      @logout="handleLogout"
    />
    <main class="login-shell">
      <section class="login-visual">
        <p class="badge">欢迎回到鲜采直送</p>
        <h1>一键登录，开启当日鲜达体验</h1>
        <p class="intro">
          登录后即可同步购物车与订单，预约同城鲜达。
        </p>
        <div class="highlights">
          <span>产地直采 · 冷链保鲜</span>
          <span>订单、地址一站查看</span>
        </div>
        <div class="visual-wrapper">
          <img :src="loginImg" alt="login illustration" />
        </div>
      </section>

      <section class="login-card">
        <header>
          <h2>登录账户</h2>
          <p>使用注册时填写的用户名称与密码完成登录</p>
        </header>

        <form class="login-form" @submit.prevent="handleLogin">
          <label class="field">
            <span>用户名称</span>
            <input
              id="username"
              v-model.trim="username"
              type="text"
              placeholder="请输入用户名称"
              autocomplete="username"
            />
          </label>

          <label class="field">
            <span>登录密码</span>
            <input
              id="password"
              v-model.trim="password"
              type="password"
              placeholder="请输入密码"
              autocomplete="current-password"
            />
          </label>

          <button class="submit-btn" type="submit" :disabled="loading">
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
        </form>

        <footer class="login-tips">
          <p>忘记密码或未注册？请联系平台客服协助开通账号。</p>
        </footer>
      </section>
    </main>
    <HomeFooter />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { homeLogin } from '@/apis/home';
import HomeHeader from '@/components/home/HomeHeader.vue';
import HomeFooter from '@/components/home/HomeFooter.vue';

const router = useRouter();
const username = ref('');
const password = ref('');
const loading = ref(false);
const cartTotal = ref(Number(localStorage.getItem('cartTotal') || 0));
const headerUsername = ref(localStorage.getItem('username') || '');

const loginImg = new URL('@/assets/home/images/login-illustration.png', import.meta.url).href;

const decodeToken = (token) => {
  if (!token) return {};
  try {
    const base64 = token.split('.')[1];
    if (!base64) return {};
    const normalized = base64.replace(/-/g, '+').replace(/_/g, '/');
    const padded = normalized.padEnd(normalized.length + (4 - (normalized.length % 4)) % 4, '=');
    const binary = window.atob(padded);
    const bytes = Uint8Array.from(binary, (char) => char.charCodeAt(0));
    const decoded = new TextDecoder('utf-8').decode(bytes);
    return JSON.parse(decoded);
  } catch (error) {
    console.warn('解析 token 失败', error);
    return {};
  }
};

const handleLogin = async () => {
  if (!username.value || !password.value) {
    window.alert('用户名和密码不能为空！');
    return;
  }
  loading.value = true;
  try {
    const res = await homeLogin({
      username: username.value,
      password: password.value,
    });
    if (res?.code === 0) {
      if (res.msg) {
        localStorage.setItem('token', res.msg);
        const payload = decodeToken(res.msg);
        if (payload?.username) {
          localStorage.setItem('username', payload.username);
          headerUsername.value = payload.username;
        } else {
          localStorage.setItem('username', username.value);
          headerUsername.value = username.value;
        }
        if (payload?.email) {
          localStorage.setItem('email', payload.email);
        }
        if (payload?.phone) {
          localStorage.setItem('phone', payload.phone);
        }
        if (payload?.headPic) {
          localStorage.setItem('headPic', payload.headPic);
        }
        if (payload?.id) {
          localStorage.setItem('userId', payload.id);
        }
      } else {
        localStorage.setItem('username', username.value);
        headerUsername.value = username.value;
      }
      window.alert('登录成功');
      const redirect = router.currentRoute.value.query?.redirect;
      router.push(typeof redirect === 'string' && redirect ? redirect : '/home');
    } else {
      window.alert(res?.msg || '登录失败，请重试');
    }
  } catch (error) {
    window.alert('网络错误，登录失败！');
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
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f3faf3 0%, #ffffff 45%, #f6f8fb 100%);
  display: flex;
  flex-direction: column;
}

.login-shell {
  flex: 1;
  width: min(980px, 100%);
  margin: 0 auto;
  padding: clamp(18px, 4.5vw, 26px) clamp(18px, 5vw, 32px) clamp(18px, 4vw, 24px);
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: clamp(16px, 3vw, 24px);
  align-items: stretch;
}

.login-visual,
.login-card {
  border-radius: 20px;
  padding: clamp(30px, 4.6vw, 38px);
  box-shadow: 0 20px 36px rgba(25, 45, 35, 0.1);
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: clamp(12px, 2.6vw, 18px);
  min-height: clamp(360px, 46vh, 420px);
}

.login-visual {
  background: linear-gradient(135deg, rgba(46, 125, 72, 0.14), rgba(60, 167, 92, 0.2));
  color: #1f3d2a;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(46, 125, 72, 0.1);
  color: #2e7d48;
  font-weight: 600;
  font-size: 13px;
}

.login-visual h1 {
  margin: 0;
  font-size: clamp(26px, 3.8vw, 32px);
  font-weight: 700;
  line-height: 1.25;
}

.intro {
  margin: 0;
  color: rgba(31, 61, 42, 0.72);
}

.highlights {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  color: rgba(31, 61, 42, 0.7);
  font-weight: 600;
}

.visual-wrapper {
  margin-top: auto;
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.55);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.3);
  max-height: clamp(180px, 26vw, 220px);
}

.visual-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login-card header h2 {
  margin: 0;
  font-size: clamp(22px, 3vw, 30px);
  color: #1f3d2a;
}

.login-card header p {
  margin: 8px 0 0;
  color: rgba(31, 61, 42, 0.6);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: clamp(18px, 4vw, 22px);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: rgba(31, 61, 42, 0.75);
  font-weight: 600;
}

.field input {
  height: 46px;
  padding: 0 18px;
  border-radius: 16px;
  border: 1px solid rgba(46, 125, 72, 0.18);
  background: rgba(46, 125, 72, 0.06);
  font-size: 16px;
  color: #1f3d2a;
  transition: border-color 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;
}

.field input:focus {
  outline: none;
  background: rgba(46, 125, 72, 0.1);
  border-color: rgba(46, 125, 72, 0.5);
  box-shadow: 0 0 0 4px rgba(46, 125, 72, 0.18);
}

.submit-btn {
  margin-top: clamp(8px, 2vw, 14px);
  height: 48px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #3ca75c, #2e7d48);
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 20px 36px rgba(46, 125, 72, 0.24);
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 24px 44px rgba(46, 125, 72, 0.28);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.login-tips {
  padding: 12px 16px;
  border-radius: 18px;
  background: rgba(46, 125, 72, 0.08);
  color: rgba(31, 61, 42, 0.68);
  font-size: 14px;
}

@media (max-width: 1024px) {
  .login-shell {
    grid-template-columns: 1fr;
    padding: clamp(24px, 7vw, 32px) clamp(16px, 6vw, 28px) clamp(32px, 8vw, 40px);
  }

  .login-visual {
    order: 2;
  }

  .login-card {
    order: 1;
  }
}

@media (max-width: 640px) {
  .login-page {
    margin-top: calc(-1 * (var(--header-height) + clamp(2px, 1.5vw, 6px)));
  }

  .login-card {
    padding: clamp(24px, 8vw, 32px);
  }

  .field input {
    height: 48px;
  }
}
</style>

