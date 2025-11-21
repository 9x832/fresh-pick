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
              <span class="name">用户地址列表</span>
              <p class="desc">设为订单首选后，下单时将优先使用该地址</p>
            </div>
          </div>
          <div v-if="addresses.length" class="address_cards">
            <article
              class="address_card"
              v-for="address in addresses"
              :key="address.id"
              :class="{ editing: editingId === address.id }"
            >
              <header>
                <div>
                  <strong>{{ address.receiverName }}</strong>
                  <span>{{ address.receiverPhone }}</span>
                </div>
                <span v-if="address.firstSelected === 1" class="badge">订单首选</span>
              </header>
              <p class="address_line">{{ address.receiverAddress }}</p>
              <footer>
                <button type="button" class="btn subtle" @click="startEdit(address)">
                  编辑
                </button>
                <button
                  v-if="address.firstSelected !== 1"
                  type="button"
                  class="btn outline"
                  @click="handleSetDefault(address.id)"
                >
                  设为首选
                </button>
                <button type="button" class="btn subtle" @click="handleDelete(address.id)">
                  删除地址
                </button>
              </footer>
            </article>
          </div>
          <div v-else class="empty-text">暂无收货地址，请先添加。</div>
        </div>

        <div class="user_Borders">
          <div class="title_name">
            <div class="heading">
              <span class="name">新增地址</span>
              <p class="desc">维护常用收货信息，便于下单选择与配送</p>
            </div>
          </div>

          <form class="address_form" @submit.prevent="handleSubmit">
            <div class="form_grid">
              <label class="field">
                <span>收件人姓名</span>
                <input v-model.trim="form.receiverName" type="text" placeholder="请输入收货人姓名" />
              </label>
              <label class="field">
                <span>收货人电话</span>
                <input v-model.trim="form.receiverPhone" type="text" placeholder="请输入收货人电话" />
              </label>
            </div>
            <div class="select_row">
              <label class="field">
                <span>省份</span>
                <select v-model="provinceCode">
                  <option value="" disabled>请选择省份</option>
                  <option v-for="province in areaTree" :key="province.code" :value="province.code">
                    {{ province.name }}
                  </option>
                </select>
              </label>
              <label class="field">
                <span>城市</span>
                <select v-model="cityCode" :disabled="!provinceCode">
                  <option value="" disabled>请选择城市</option>
                  <option v-for="city in cityOptions" :key="city.code" :value="city.code">
                    {{ city.name }}
                  </option>
                </select>
              </label>
              <label class="field">
                <span>区县</span>
                <select v-model="districtCode" :disabled="!cityCode">
                  <option value="" disabled>请选择区县</option>
                  <option v-for="district in districtOptions" :key="district.code" :value="district.code">
                    {{ district.name }}
                  </option>
                </select>
              </label>
            </div>
            <label class="field">
              <span>详细地址</span>
              <input v-model.trim="form.detailAddress" type="text" placeholder="请输入详细地址（街道、门牌号等）" />
            </label>
            <div class="form_action">
              <button type="submit" class="btn primary">{{ isEditing ? '保存修改' : '添加地址' }}</button>
              <button v-if="isEditing" type="button" class="btn subtle" @click="resetForm">
                取消编辑
              </button>
            </div>
          </form>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  fetchAddresses,
  addAddress,
  updateAddress,
  setDefaultAddress,
  deleteAddress,
} from '@/apis/home';
import areaData from 'china-area-data';

const form = reactive({
  receiverName: '',
  receiverAddress: '',
  receiverPhone: '',
  detailAddress: '',
});

const state = reactive({
  addresses: [],
  loading: false,
});

const provinceCode = ref('');
const cityCode = ref('');
const districtCode = ref('');
const editingId = ref(null);

const router = useRouter();
const route = useRoute();
const buildAreaTree = () => {
  const provinces = areaData['86'] || {};
  return Object.entries(provinces).map(([provinceCode, provinceName]) => {
    const cities = areaData[provinceCode] || {};
    return {
      code: provinceCode,
      name: provinceName,
      children: Object.entries(cities).map(([cityCode, cityName]) => {
        const districts = areaData[cityCode] || {};
        return {
          code: cityCode,
          name: cityName,
          children: Object.entries(districts).map(([districtCode, districtName]) => ({
            code: districtCode,
            name: districtName,
          })),
        };
      }),
    };
  });
};

const areaTree = buildAreaTree();

const addresses = computed(() => state.addresses);
const isActive = (path) => route.path === path;
const isEditing = computed(() => !!editingId.value);

const cityOptions = computed(() => {
  const province = areaTree.find((item) => item.code === provinceCode.value);
  return province?.children ?? [];
});

const districtOptions = computed(() => {
  const province = areaTree.find((item) => item.code === provinceCode.value);
  if (!province) return [];
  const city = province.children?.find((item) => item.code === cityCode.value);
  return city?.children ?? [];
});

watch(provinceCode, () => {
  cityCode.value = '';
  districtCode.value = '';
});

watch(cityCode, () => {
  districtCode.value = '';
});

const loadAddresses = async () => {
  state.loading = true;
  try {
    const res = await fetchAddresses();
    if (res?.code === 0 && Array.isArray(res.data)) {
      state.addresses = res.data;
    } else if (res?.code === -6) {
      window.alert('请先登录后管理地址');
      router.push('/login');
    } else {
      state.addresses = [];
    }
  } catch (error) {
    console.error('加载地址失败', error);
    state.addresses = [];
  } finally {
    state.loading = false;
  }
};

const validateForm = () => {
  if (!form.receiverName) {
    window.alert('收货人姓名不能为空');
    return false;
  }
  if (!provinceCode.value || !cityCode.value || !districtCode.value) {
    window.alert('请选择完整的省市区信息');
    return false;
  }
  if (!form.detailAddress) {
    window.alert('详细地址不能为空');
    return false;
  }
  if (!form.receiverPhone) {
    window.alert('收货人电话不能为空');
    return false;
  }
  return true;
};

const resetForm = () => {
      form.receiverName = '';
      form.receiverAddress = '';
      form.receiverPhone = '';
      form.detailAddress = '';
      provinceCode.value = '';
      cityCode.value = '';
      districtCode.value = '';
  editingId.value = null;
};

const composeAddress = () => {
  const province = areaTree.find((item) => item.code === provinceCode.value);
  const city = cityOptions.value.find((item) => item.code === cityCode.value);
  const district = districtOptions.value.find((item) => item.code === districtCode.value);
  return `${province?.name ?? ''}${city?.name ?? ''}${district?.name ?? ''}${form.detailAddress.trim()}`;
};

const handleSubmit = async () => {
  if (!validateForm()) return;
  form.receiverAddress = composeAddress();
  try {
    let res;
    if (editingId.value) {
      res = await updateAddress(editingId.value, { ...form, id: editingId.value });
    } else {
      res = await addAddress({ ...form });
    }
    if (res?.code === 0) {
      window.alert(res.msg || (editingId.value ? '地址更新成功' : '地址添加成功'));
      resetForm();
      await loadAddresses();
    } else if (res?.code === -6) {
      window.alert('请先登录后操作地址');
      router.push('/login');
    } else {
      window.alert(res?.msg || (editingId.value ? '地址更新失败' : '地址添加失败'));
    }
  } catch (error) {
    console.error(editingId.value ? '更新地址失败' : '添加地址失败', error);
    window.alert('网络错误，操作失败');
  }
};

const startEdit = (address) => {
  editingId.value = address.id;
  form.receiverName = address.receiverName || '';
  form.receiverPhone = address.receiverPhone || '';
  form.detailAddress = '';
  form.receiverAddress = address.receiverAddress || '';
  const target = address.receiverAddress || '';
  let remaining = target;
  const province = areaTree.find((item) => remaining.startsWith(item.name));
  if (province) {
    provinceCode.value = province.code;
    remaining = remaining.slice(province.name.length);
    const city = province.children?.find((item) => remaining.startsWith(item.name));
    if (city) {
      cityCode.value = city.code;
      remaining = remaining.slice(city.name.length);
      const district = city.children?.find((item) => remaining.startsWith(item.name));
      if (district) {
        districtCode.value = district.code;
        remaining = remaining.slice(district.name.length);
      } else {
        districtCode.value = '';
      }
    } else {
      cityCode.value = '';
      districtCode.value = '';
    }
  } else {
    provinceCode.value = '';
    cityCode.value = '';
    districtCode.value = '';
  }
  form.detailAddress = remaining.trim() || '';
  if (!provinceCode.value) {
    form.detailAddress = target;
  }
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const handleSetDefault = async (id) => {
  try {
    const res = await setDefaultAddress(id);
    if (res?.code === 0) {
      window.alert(res.msg || '设置成功');
      await loadAddresses();
    } else {
      window.alert(res?.msg || '设置失败');
    }
  } catch (error) {
    console.error('设置默认地址失败', error);
    window.alert('网络错误，设置失败');
  }
};

const handleDelete = async (id) => {
  if (!window.confirm('确定删除该地址吗？')) {
    return;
  }
  try {
    const res = await deleteAddress(id);
    if (res?.code === 0) {
      window.alert(res.msg || '删除成功');
      if (editingId.value === id) {
        resetForm();
      }
      await loadAddresses();
    } else {
      window.alert(res?.msg || '删除失败');
    }
  } catch (error) {
    console.error('删除地址失败', error);
    window.alert('网络错误，删除失败');
  }
};

onMounted(() => {
  loadAddresses();
});
</script>

<style scoped>

.user-center-layout {
  display: flex;
  flex-direction: column;
  gap: clamp(18px, 3vw, 30px);
  padding: clamp(20px, 5vw, 40px) clamp(16px, 6vw, 72px) clamp(12px, 3vw, 24px);
}

.user-center-inner {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  gap: clamp(16px, 3vw, 24px);
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
  text-decoration: none;
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
  gap: clamp(15px, 2.5vw, 22px);
}

.user_Borders {
  border-radius: 28px;
  padding: clamp(18px, 2.5vw, 26px);
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 22px 44px rgba(15, 23, 42, 0.14);
  display: flex;
  flex-direction: column;
  gap: clamp(12px, 2vw, 20px);
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

.address_form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  background: rgba(248, 250, 252, 0.9);
  border-radius: 18px;
  padding: clamp(16px, 3vw, 24px);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.16);
}

.address_form select {
  height: 42px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 12px;
  padding: 0 12px;
  background: #fff;
  font-size: 14px;
  color: #0f172a;
}

.form_grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field span {
  font-size: 14px;
  color: rgba(15, 23, 42, 0.7);
  font-weight: 600;
}

.field input {
  height: 44px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  border-radius: 12px;
  padding: 0 16px;
  font-size: 16px;
  color: #0f172a;
  transition: border 0.2s ease, box-shadow 0.2s ease;
}

.field input:focus {
  border-color: rgba(76, 187, 108, 0.6);
  box-shadow: 0 0 0 3px rgba(76, 187, 108, 0.18);
  outline: none;
}

.form_action {
  display: flex;
  justify-content: flex-end;
}

.btn {
  border: none;
  border-radius: 999px;
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.btn.primary {
  background: linear-gradient(135deg, rgba(76, 187, 108, 0.96), rgba(99, 209, 130, 0.92));
  color: #fff;
  box-shadow: 0 16px 28px rgba(76, 187, 108, 0.3);
}

.btn.outline {
  background: rgba(99, 102, 241, 0.12);
  color: #4338ca;
}

.btn.subtle {
  background: rgba(148, 163, 184, 0.16);
  color: rgba(15, 23, 42, 0.72);
}

.btn:hover {
  transform: translateY(-1px);
}

.select_row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 14px;
}

.address_cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: clamp(14px, 2.5vw, 22px);
}

.address_card {
  border-radius: 18px;
  padding: 16px;
  background: rgba(248, 250, 252, 0.9);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.16);
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.address_card.editing {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.35);
  transform: translateY(-2px);
}

.address_card header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.address_card header div {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.address_card header strong {
  font-size: 16px;
  color: #0f172a;
  font-weight: 700;
}

.address_card header span {
  font-size: 13px;
  color: rgba(71, 85, 105, 0.75);
}

.address_card .badge {
  background: rgba(59, 130, 246, 0.15);
  color: #1d4ed8;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.address_line {
  margin: 0;
  font-size: 14px;
  color: rgba(15, 23, 42, 0.78);
  line-height: 1.6;
}

.address_card footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  flex-wrap: wrap;
}

.empty-text {
  text-align: center;
  padding: 40px 0;
  color: rgba(100, 116, 139, 0.74);
}

@media (max-width: 960px) {
  .user-center-inner {
    flex-direction: column;
  }
}

@media (max-width: 640px) {
  .address_card header {
    flex-direction: column;
    align-items: flex-start;
  }
}

</style>

