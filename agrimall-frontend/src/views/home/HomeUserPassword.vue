<template>
  <div class="user-center-layout">
    <div class="user-center-inner">
      <aside class="user-center-sidebar">
        <ul>
          <li :class="{ active: isActive('/home/user/info') }">
            <RouterLink to="/home/user/info">个人信息</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/user/order') }">
            <RouterLink to="/home/user/order">我的订单</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/user/comment') }">
            <RouterLink to="/home/user/comment">我的评价</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/collect') }">
            <RouterLink to="/home/collect">我的收藏</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/address') }">
            <RouterLink to="/home/address">地址管理</RouterLink>
          </li>
          <li :class="{ active: isActive('/home/user/password') }">
            <RouterLink to="/home/user/password">修改密码</RouterLink>
          </li>
        </ul>
      </aside>

      <section class="user-center-main">
        <div class="user_Borders">
          <div class="title_name">
            <div class="heading">
              <span class="name">修改密码</span>
              <p class="desc">建议定期更新密码，提升账号安全性</p>
            </div>
            <div
              class="status_tag"
              :data-level="passwordStrength.level"
            >
              安全等级：<strong>{{ passwordStrength.label }}</strong>
            </div>
          </div>
          <form class="password_form" @submit.prevent="handleSubmit">
            <div class="form_grid">
              <label class="field">
                <span>原密码</span>
                <input
                  v-model.trim="form.prePassword"
                  type="password"
                  placeholder="请输入原密码"
                  autocomplete="current-password"
                />
              </label>
              <label class="field">
                <span>新密码</span>
                <input
                  v-model.trim="form.newPassword"
                  type="password"
                  placeholder="至少 8 位，建议包含字母与数字"
                  autocomplete="new-password"
                />
              </label>
              <label class="field">
                <span>确认新密码</span>
                <input
                  v-model.trim="form.reNewPassword"
                  type="password"
                  placeholder="再次输入新密码"
                  autocomplete="new-password"
                />
              </label>
            </div>
            <div class="password-tips">
              <h3>密码设置建议</h3>
              <ul>
                <li>长度 8-18 位，尽量混合大小写字母、数字和符号。</li>
                <li>避免使用生日、手机号、连续数字等易被猜到的信息。</li>
                <li>不要在不同网站重复使用同一个密码。</li>
              </ul>
            </div>
            <div class="form_action">
              <button type="submit" class="submit_btn">确认</button>
            </div>
          </form>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { updateUserPassword } from '@/apis/home';
import '@/views/home/userCenter.css';

const router = useRouter();
const route = useRoute();

const form = reactive({
  prePassword: '',
  newPassword: '',
  reNewPassword: '',
});

const passwordStrength = computed(() => {
  const value = form.newPassword || '';
  let score = 0;
  if (value.length >= 8) score += 1;
  if (value.length >= 12) score += 1;
  if (/[a-z]/.test(value) && /[A-Z]/.test(value)) score += 1;
  if (/\d/.test(value)) score += 1;
  if (/[^a-zA-Z0-9]/.test(value)) score += 1;

  let level = 'weak';
  let label = '低';
  if (score >= 4) {
    level = 'strong';
    label = '高';
  } else if (score >= 2) {
    level = 'medium';
    label = '中';
  }
  return { level, label };
});

const isActive = (path) => route.path === path;

const handleSubmit = async () => {
  if (!form.prePassword) {
    window.alert('原密码不能为空');
    return;
  }
  if (!form.newPassword) {
    window.alert('新密码不能为空');
    return;
  }
  if (!form.reNewPassword) {
    window.alert('确认新密码不能为空');
    return;
  }
  if (form.newPassword !== form.reNewPassword) {
    window.alert('两次输入的新密码不一致');
    return;
  }
  try {
    const res = await updateUserPassword({ ...form });
    if (res?.code === 0) {
      window.alert(res.msg || '修改密码成功');
      form.prePassword = '';
      form.newPassword = '';
      form.reNewPassword = '';
    } else if (res?.code === -6) {
      window.alert('请先登录后再操作');
      router.push('/login');
    } else {
      window.alert(res?.msg || '修改密码失败');
    }
  } catch (error) {
    console.error('修改密码失败', error);
    window.alert('网络错误，修改密码失败');
  }
};
</script>

<style scoped>
.user_Borders {
  border-radius: 28px;
  padding: clamp(26px, 4vw, 38px);
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 22px 44px rgba(15, 23, 42, 0.14);
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3vw, 28px);
}

.title_name {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
  padding-bottom: 18px;
}

.title_name .heading {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title_name .name {
  font-size: clamp(22px, 3.5vw, 26px);
  color: #0f172a;
  font-weight: 700;
}

.title_name .desc {
  margin: 0;
  font-size: 14px;
  color: rgba(71, 85, 105, 0.8);
}

.status_tag {
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
  font-size: 13px;
  font-weight: 600;
  transition: background 0.2s ease, color 0.2s ease;
}
.status_tag[data-level='weak'] {
  background: rgba(248, 113, 113, 0.18);
  color: #b91c1c;
}
.status_tag[data-level='medium'] {
  background: rgba(251, 191, 36, 0.18);
  color: #b45309;
}
.status_tag[data-level='strong'] {
  background: rgba(16, 185, 129, 0.18);
  color: #047857;
}

.password_form {
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3vw, 30px);
}

.form_grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: clamp(18px, 3vw, 28px);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
  color: rgba(15, 23, 42, 0.7);
  font-weight: 600;
}

.field span {
  font-size: 14px;
}

.field input {
  height: 44px;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.95);
  font-weight: 500;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.field input:focus {
  border-color: rgba(76, 187, 108, 0.6);
  box-shadow: 0 0 0 3px rgba(76, 187, 108, 0.18);
  outline: none;
}

.password-tips {
  background: rgba(236, 252, 245, 0.85);
  border-radius: 20px;
  padding: 18px 22px;
  box-shadow: inset 0 0 0 1px rgba(77, 208, 128, 0.2);
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: rgba(15, 23, 42, 0.7);
  font-size: 13px;
}

.password-tips h3 {
  margin: 0;
  font-size: 15px;
  color: #0f172a;
}

.password-tips ul {
  margin: 0;
  padding-left: 18px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form_action {
  display: flex;
  justify-content: flex-end;
}

.submit_btn {
  padding: 12px 36px;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.96), rgba(99, 209, 130, 0.92));
  color: #ffffff;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 18px 32px rgba(76, 187, 108, 0.3);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.submit_btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 22px 36px rgba(76, 187, 108, 0.35);
}

@media (max-width: 960px) {
  .form_action {
    justify-content: center;
  }

  .submit_btn {
    width: 100%;
  }
}
</style>

