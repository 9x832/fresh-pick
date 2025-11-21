<template>
  <div class="dialog-backdrop">
    <div class="dialog">
      <header>
        <h3>{{ product?.id ? '编辑商品' : '新增商品' }}</h3>
      </header>
      <section class="dialog-body">
        <label class="form-label" for="product-name">商品名称</label>
        <input
          id="product-name"
          v-model.trim="form.productName"
          type="text"
          maxlength="16"
          placeholder="请输入 1-16 字的商品名称"
        />

        <label class="form-label" for="product-info">商品简介</label>
        <textarea
          id="product-info"
          v-model.trim="form.info"
          rows="3"
          maxlength="32"
          placeholder="请输入 1-32 字的商品简介"
        ></textarea>

        <label class="form-label" for="product-price">价格（元/箱）</label>
        <input
          id="product-price"
          v-model="form.price"
          type="number"
          step="0.01"
          min="0"
          max="100000000"
          placeholder="请输入商品价格"
        />

        <label class="form-label" for="product-stock">库存（箱）</label>
        <input
          id="product-stock"
          v-model.number="form.stock"
          type="number"
          min="0"
          max="100000000"
          placeholder="请输入库存数量"
        />

        <label class="form-label" for="product-category">所属分类</label>
        <select id="product-category" v-model.number="form.categoryId">
          <option disabled value="">请选择分类</option>
          <option v-for="item in categories" :key="item.id" :value="item.id">
            {{ item.categoryName }}
          </option>
        </select>

        <label class="form-label" for="product-pic">商品图片</label>
        <div class="image-field">
          <div v-if="previewUrl" class="image-preview">
            <img :src="previewUrl" alt="商品图片预览" />
            <button
              type="button"
              class="btn ghost"
              @click="removeSelectedImage"
              :disabled="loading || uploading"
            >
              移除图片
            </button>
          </div>
          <div class="image-actions">
            <label class="upload-button">
              <input
                id="product-pic"
                type="file"
                accept="image/*"
                @change="handleFileChange"
                :disabled="loading || uploading"
              />
              <span>{{ uploading ? '上传中...' : '选择图片' }}</span>
            </label>
            <span
              v-if="form.productPic && !uploading"
              class="current-path"
              :title="form.productPic"
            >
              当前路径：{{ form.productPic }}
            </span>
          </div>
        </div>
        <small class="hint">支持 JPG、PNG 等常见格式，建议 2MB 以内。</small>
      </section>
      <footer class="dialog-footer">
        <button type="button" class="btn secondary" @click="$emit('close')" :disabled="loading || uploading">
          取消
        </button>
        <button type="button" class="btn primary" @click="submit" :disabled="loading || uploading">
          {{ loading || uploading ? '处理中...' : '保存' }}
        </button>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, reactive, ref, watch } from 'vue';
import { uploadAdminPhoto } from '@/apis/admin';

const props = defineProps({
  product: {
    type: Object,
    default: null,
  },
  categories: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close', 'submit']);

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const form = reactive({
  id: null,
  productName: '',
  info: '',
  price: '',
  stock: 0,
  categoryId: '',
  productPic: '',
});

const uploading = ref(false);
const tempPreview = ref('');

const resolveImage = (path) => {
  if (!path) return '';
  if (/^https?:\/\//i.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

const previewUrl = computed(() => {
  if (tempPreview.value) return tempPreview.value;
  if (form.productPic) return resolveImage(form.productPic);
  return '';
});

const resetTempPreview = () => {
  if (tempPreview.value) {
    URL.revokeObjectURL(tempPreview.value);
    tempPreview.value = '';
  }
};

watch(
  () => props.product,
  (product) => {
    resetTempPreview();
    if (product) {
      form.id = product.id ?? null;
      form.productName = product.productName ?? '';
      form.info = product.info ?? '';
      form.price = product.price ?? '';
      form.stock = product.stock ?? 0;
      form.categoryId = product.categoryId ?? '';
      form.productPic = product.productPic ?? '';
    } else {
      form.id = null;
      form.productName = '';
      form.info = '';
      form.price = '';
      form.stock = 0;
      form.categoryId = '';
      form.productPic = '';
    }
  },
  { immediate: true },
);

const handleFileChange = async (event) => {
  const inputEl = event.target;
  const [file] = inputEl?.files || [];
  if (inputEl) {
    inputEl.value = '';
  }
  if (!file) return;

  resetTempPreview();
  tempPreview.value = URL.createObjectURL(file);
  uploading.value = true;
  try {
    const res = await uploadAdminPhoto(file);
    if (res?.code === 0 && res.data) {
      form.productPic = res.data;
      resetTempPreview(); // 立即切换为服务器返回的地址
    } else {
      window.alert(res?.msg || '图片上传失败，请稍后再试');
      resetTempPreview();
    }
  } catch (error) {
    console.error('上传商品图片失败', error);
    window.alert('网络错误，上传商品图片失败');
    resetTempPreview();
  } finally {
    uploading.value = false;
  }
};

const removeSelectedImage = () => {
  if (uploading.value) return;
  resetTempPreview();
  form.productPic = '';
};

const submit = async () => {
  if (!form.productName) {
    window.alert('商品名称不能为空');
    return;
  }
  if (!form.info) {
    window.alert('商品简介不能为空');
    return;
  }
  if (form.price === '' || Number(form.price) < 0) {
    window.alert('商品价格不能为空且需大于等于 0');
    return;
  }
  if (form.stock === '' || Number(form.stock) < 0) {
    window.alert('商品库存不能为空且需大于等于 0');
    return;
  }
  if (!form.categoryId) {
    window.alert('请选择商品分类');
    return;
  }
  if (!form.productPic) {
    window.alert('请上传商品图片');
    return;
  }
  emit('submit', {
    id: form.id,
    productName: form.productName,
    info: form.info,
    price: Number(form.price),
    stock: Number(form.stock),
    categoryId: Number(form.categoryId),
    productPic: form.productPic,
  });
};

onBeforeUnmount(() => {
  resetTempPreview();
});
</script>

<style scoped>
.dialog-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  padding: 24px;
}

.dialog {
  width: min(560px, 100%);
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.25);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog header h3 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-label {
  font-weight: 600;
  color: #475569;
}

input,
select,
textarea {
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  padding: 0 12px;
  height: 38px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

textarea {
  height: auto;
  padding: 12px;
  resize: vertical;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
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

.btn.primary {
  background: linear-gradient(135deg, #38bdf8, #2563eb);
  color: #fff;
}

.btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px rgba(37, 99, 235, 0.35);
}

.btn.secondary {
  background: #e2e8f0;
  color: #1f2937;
}

.hint {
  color: #94a3b8;
  font-size: 12px;
}

.image-field {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.image-preview {
  display: flex;
  gap: 12px;
  align-items: center;
}

.image-preview img {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: #f8fafc;
}

.image-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.upload-button {
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  border-radius: 10px;
  background: rgba(59, 130, 246, 0.12);
  color: #1d4ed8;
  cursor: pointer;
  font-weight: 600;
}

.upload-button input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.current-path {
  max-width: 220px;
  color: #475569;
  font-size: 12px;
  line-height: 1.4;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

