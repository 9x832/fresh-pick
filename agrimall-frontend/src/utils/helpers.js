/**
 * 通用工具函数
 */

const IMAGE_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

/**
 * 格式化价格为两位小数
 * @param {number|string|null|undefined} value - 价格值
 * @returns {string} 格式化后的价格字符串
 */
export const formatPrice = (value) => {
  if (value === null || value === undefined) return '0.00';
  const num = Number(value);
  return Number.isNaN(num) ? '0.00' : num.toFixed(2);
};

/**
 * 标准化图片路径
 * @param {string|null|undefined} path - 图片路径
 * @param {string} placeholder - 占位图URL（可选）
 * @returns {string} 完整的图片URL
 */
export const normalizeImage = (path, placeholder = '') => {
  if (!path) return placeholder;
  if (/^https?:\/\//i.test(path)) return path;
  const clean = path.startsWith('/') ? path.slice(1) : path;
  return `${IMAGE_BASE_URL}/api/common/photo/view?filename=${clean}`;
};

/**
 * 格式化日期时间
 * @param {string|Date} value - 日期值
 * @returns {string} 格式化后的日期字符串 (yyyy-MM-dd HH:mm:ss)
 */
export const formatDateTime = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const mi = String(date.getMinutes()).padStart(2, '0');
  const ss = String(date.getSeconds()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`;
};

/**
 * 格式化文件大小
 * @param {number} size - 文件大小（KB）
 * @returns {string} 格式化后的大小字符串
 */
export const formatFileSize = (size) => {
  const num = Number(size);
  if (!Number.isFinite(num) || num <= 0) return '';
  if (num < 1) {
    // 字节数
    if (num < 1024) return `${num.toFixed(0)} B`;
    if (num < 1024 * 1024) return `${(num / 1024).toFixed(1)} KB`;
    if (num < 1024 * 1024 * 1024) return `${(num / 1024 / 1024).toFixed(1)} MB`;
    return `${(num / 1024 / 1024 / 1024).toFixed(2)} GB`;
  } else {
    // KB
    if (num < 1024) return `${num.toFixed(1)} KB`;
    if (num < 1024 * 1024) return `${(num / 1024).toFixed(1)} MB`;
    return `${(num / 1024 / 1024).toFixed(2)} GB`;
  }
};

export { IMAGE_BASE_URL };

