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
              <span class="name">个人信息设置</span>
              <p class="desc">完善联系信息便于订单通知和售后服务</p>
            </div>
            <button type="button" class="refresh_btn" @click="loadUserFromToken">
              恢复本地信息
            </button>
          </div>
          <form class="user_form" @submit.prevent="handleSubmit">
            <div class="avatar_row">
              <div class="avatar-preview">
                <img :src="avatarUrl" alt="avatar" />
                <button type="button" class="upload_btn" @click="triggerUpload">
                  上传头像
                </button>
              </div>
              <div class="avatar-hint">
                <h3>头像建议</h3>
                <p>支持 JPG、PNG 格式，大小不超过 2MB；清晰的头像有助于客服快速识别。</p>
              </div>
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                @change="handleUpload"
                hidden
              />
            </div>
            <div class="form_grid">
              <label class="field">
                <span>用户名称</span>
                <input v-model.trim="form.username" type="text" placeholder="请输入用户名称" />
              </label>
              <label class="field">
                <span>手机号码</span>
                <input v-model.trim="form.phone" type="tel" placeholder="请输入手机号码" maxlength="11" />
              </label>
              <label class="field">
                <span>电子邮箱</span>
                <input v-model.trim="form.email" type="email" placeholder="请输入电子邮箱" />
              </label>
            </div>
            <div class="form_hint">
              <i class="ri-information-line"></i>
              <span>提交后会同步更新展示昵称及通知联系方式。</span>
            </div>
            <div class="form_action">
              <button type="submit" class="submit_btn">提交</button>
            </div>
          </form>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onBeforeUnmount, onMounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { updateUserInfo, uploadPhoto } from '@/apis/home';
import '@/views/home/userCenter.css';

const DEFAULT_AVATAR = 'common/user_img.jpg';
const IMAGE_BASE_URL = 'http://localhost:8080';

const router = useRouter();
const route = useRoute();

const form = reactive({
  id: '',
  username: '',
  phone: '',
  email: '',
  headPic: '',
});

const fileInput = ref(null);
const pendingAvatar = ref(null);

const decodeToken = (token) => {
  if (!token) return {};
  try {
    const base64 = token.split('.')[1];
    if (!base64) return {};
    const normalized = base64.replace(/-/g, '+').replace(/_/g, '/');
    const padded = normalized.padEnd(normalized.length + (4 - (normalized.length % 4)) % 4, '=');
    const binary = window.atob(padded);
    const bytes = Uint8Array.from(binary, (char) => char.charCodeAt(0));
    const json = new TextDecoder('utf-8').decode(bytes);
    return JSON.parse(json);
  } catch (error) {
    console.warn('解析 token 失败', error);
    return {};
  }
};

const loadUserFromToken = () => {
  const token = localStorage.getItem('token');
  const payload = decodeToken(token);
  form.id = payload?.id || '';
  form.username = payload?.username || localStorage.getItem('username') || '';
  form.phone = payload?.phone || localStorage.getItem('phone') || '';
  form.email = payload?.email || localStorage.getItem('email') || '';
  form.headPic = payload?.headPic || localStorage.getItem('headPic') || '';
};

loadUserFromToken();
if (!form.id) {
  window.alert('请先登录后再操作');
  router.push('/login');
}

const photoUrl = (filename) => {
  if (!filename) {
    const prefix = DEFAULT_AVATAR.startsWith('/') ? DEFAULT_AVATAR.slice(1) : DEFAULT_AVATAR;
    return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${prefix}`;
  }
  if (/^https?:/.test(filename)) return filename;
  const prefix = filename.startsWith('/') ? filename.slice(1) : filename;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${prefix}`;
};

const avatarUrl = computed(() => {
  if (pendingAvatar.value?.preview) {
    return pendingAvatar.value.preview;
  }
  return photoUrl(form.headPic);
});

const isActive = (path) => route.path === path;

const triggerUpload = () => {
  fileInput.value?.click();
};

const handleUpload = async (event) => {
  const file = event.target.files?.[0];
  if (!file) return;
  if (pendingAvatar.value?.preview) {
    URL.revokeObjectURL(pendingAvatar.value.preview);
  }
  pendingAvatar.value = {
    file,
    preview: URL.createObjectURL(file),
  };
  if (fileInput.value) {
    fileInput.value.value = '';
  }
};

const emailReg =
  /^([a-zA-Z0-9]+[_|\.|-]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\.|-]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;

const applyProfileResult = (tokenStr) => {
  let payload = null;
  if (tokenStr) {
    localStorage.setItem('token', tokenStr);
    payload = decodeToken(tokenStr);
  }
  const username = payload?.username ?? form.username;
  const phone = payload?.phone ?? form.phone;
  const email = payload?.email ?? form.email;
  const headPic = payload?.headPic ?? form.headPic;

  form.username = username;
  form.phone = phone;
  form.email = email;
  form.headPic = headPic;

  localStorage.setItem('username', username);
  localStorage.setItem('phone', phone);
  localStorage.setItem('email', email);
  if (headPic) {
    localStorage.setItem('headPic', headPic);
  } else {
    localStorage.removeItem('headPic');
  }
};

const persistProfile = async ({ showSuccess = true, reload = true } = {}) => {
  try {
    const res = await updateUserInfo({
      id: form.id,
      username: form.username,
      phone: form.phone,
      email: form.email,
      headPic: form.headPic,
    });
    if (res?.code === 0) {
      if (showSuccess) {
        window.alert(res.msg || '修改个人信息成功');
      }
      applyProfileResult(res.data);
      if (reload) {
        window.location.reload();
      }
      return true;
    }
    if (res?.code === -6) {
      window.alert('请先登录后再操作');
      router.push('/login');
    } else {
      window.alert(res?.msg || '修改个人信息失败');
    }
  } catch (error) {
    console.error('修改信息失败', error);
    window.alert('网络错误，修改个人信息失败');
  }
  return false;
};

const handleSubmit = async () => {
  if (!form.username) {
    window.alert('用户名称不能为空');
    return;
  }
  if (!form.phone) {
    window.alert('手机号码不能为空');
    return;
  }
  if (!form.email) {
    window.alert('电子邮箱不能为空');
    return;
  }
  if (!emailReg.test(form.email)) {
    window.alert('电子邮箱格式不正确');
    return;
  }
  if (pendingAvatar.value?.file) {
    try {
      const res = await uploadPhoto(pendingAvatar.value.file);
      if (res?.code === 0 && res.data) {
        form.headPic = res.data;
      } else {
        window.alert(res?.msg || '上传头像失败，请稍后再试');
        return;
      }
    } catch (error) {
      console.error('上传头像失败', error);
      window.alert('网络错误，上传头像失败');
      return;
    }
  }
  const success = await persistProfile({ showSuccess: true, reload: true });
  if (success && pendingAvatar.value?.preview) {
    URL.revokeObjectURL(pendingAvatar.value.preview);
    pendingAvatar.value = null;
  }
};

onMounted(() => {
  nextTick(() => {
    window.scrollTo({ top: 0, behavior: 'auto' });
  });
});

onBeforeUnmount(() => {
  if (pendingAvatar.value?.preview) {
    URL.revokeObjectURL(pendingAvatar.value.preview);
  }
});
</script>

<style scoped>
.user-center-layout {
  display: flex;
  flex-direction: column;
  gap: clamp(24px, 4vw, 40px);
}

.user-center-inner {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  gap: clamp(20px, 3.5vw, 32px);
  padding: 0;
}

.user-center-sidebar {
  width: 240px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.95);
  padding: clamp(18px, 3vw, 28px);
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.12);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-center-sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-center-sidebar li {
  margin: 0;
}

.user-center-sidebar li a {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 18px;
  border-radius: 16px;
  color: rgba(15, 23, 42, 0.68);
  font-weight: 600;
  transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.user-center-sidebar li.active a,
.user-center-sidebar li a:hover {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.16), rgba(99, 209, 130, 0.16));
  color: #2f8f4a;
  transform: translateX(4px);
}

.user-center-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3vw, 30px);
}

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

.refresh_btn {
  border: none;
  border-radius: 999px;
  padding: 8px 18px;
  background: rgba(77, 208, 128, 0.14);
  color: #1f8f52;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
}

.refresh_btn:hover {
  background: rgba(77, 208, 128, 0.2);
  transform: translateY(-1px);
}

.user_form {
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3vw, 30px);
}

.avatar_row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: clamp(20px, 3vw, 32px);
  align-items: center;
}

.avatar-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-preview img {
  width: clamp(90px, 12vw, 120px);
  height: clamp(90px, 12vw, 120px);
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(76, 187, 108, 0.25);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.15);
}

.upload_btn {
  padding: 8px 20px;
  border: none;
  border-radius: 999px;
  background: rgba(99, 102, 241, 0.14);
  color: #4338ca;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.upload_btn:hover {
  background: rgba(99, 102, 241, 0.2);
}

.avatar-hint {
  background: rgba(236, 252, 245, 0.85);
  border-radius: 18px;
  padding: 16px 20px;
  box-shadow: inset 0 0 0 1px rgba(77, 208, 128, 0.2);
}

.avatar-hint h3 {
  margin: 0 0 8px;
  font-size: 15px;
  color: #0f172a;
}

.avatar-hint p {
  margin: 0;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.68);
  line-height: 1.6;
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

.form_hint {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 16px;
  background: rgba(148, 163, 184, 0.12);
  color: rgba(71, 85, 105, 0.85);
  font-size: 13px;
}

.form_hint i {
  font-size: 18px;
  color: #2f8f4a;
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
  .user-center-layout {
    padding: clamp(16px, 5vw, 28px) clamp(16px, 6vw, 36px) clamp(28px, 8vw, 56px);
  }

  .user-center-inner {
    flex-direction: column;
  }

  .user-center-sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }

  .user-center-sidebar li a {
    padding: 10px 14px;
  }

  .form_action {
    justify-content: center;
  }

  .submit_btn {
    width: 100%;
  }
}

@media (max-width: 640px) {
  .user-center-sidebar {
    gap: 10px;
  }
}

.banner {
  width: 100%;
  display: block;
}
</style>


