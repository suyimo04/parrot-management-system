<template>
  <div class="image-picker">
    <el-upload
      class="image-uploader"
      :show-file-list="false"
      :http-request="uploadImage"
      accept="image/*"
    >
      <img v-if="modelValue" :src="modelValue" class="image-uploader__img" alt="上传图片" />
      <div v-else class="image-uploader__empty">
        <el-icon><Plus /></el-icon>
        <span>上传图片</span>
      </div>
    </el-upload>

    <div class="image-picker__url">
      <el-input
        v-model="urlInput"
        clearable
        placeholder="也可以粘贴图片URL"
        @blur="applyUrl"
        @keyup.enter="applyUrl"
        @clear="clearUrl"
      >
        <template #append>
          <el-button :icon="Link" @click="applyUrl">使用</el-button>
        </template>
      </el-input>
      <div class="image-picker__tip">支持 http(s) 图片地址，也支持 /upload 开头的本地访问路径</div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Link } from '@element-plus/icons-vue'
import { uploadFile } from '../api/upload'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const urlInput = ref(props.modelValue)

watch(
  () => props.modelValue,
  (value) => {
    urlInput.value = value || ''
  }
)

const uploadImage = async ({ file }) => {
  const formData = new FormData()
  formData.append('file', file)
  const data = await uploadFile(formData)
  const url = data?.accessUrl || data?.url || data
  emit('update:modelValue', url)
  ElMessage.success('图片上传成功')
}

function applyUrl() {
  const value = (urlInput.value || '').trim()
  if (!value) {
    clearUrl()
    return
  }
  if (!isImageUrl(value)) {
    ElMessage.warning('请输入 http(s) 图片地址，或 /upload 开头的本地路径')
    return
  }
  // 手填 URL 不经过服务器保存，只把图片地址写回当前表单。
  emit('update:modelValue', value)
  ElMessage.success('图片地址已使用')
}

function clearUrl() {
  emit('update:modelValue', '')
}

function isImageUrl(value) {
  return /^(https?:\/\/|\/upload\/|data:image\/)/i.test(value)
}
</script>

<style scoped>
.image-picker {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  flex-wrap: wrap;
}

.image-uploader {
  width: 132px;
}

.image-uploader :deep(.el-upload) {
  width: 132px;
  height: 132px;
  overflow: hidden;
  border: 1px dashed var(--pm-border);
  border-radius: 8px;
  background: #FAFBFC;
}

.image-uploader__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-uploader__empty {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--pm-text-light);
}

.image-picker__url {
  width: min(420px, 100%);
  padding-top: 6px;
}

.image-picker__tip {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.5;
  color: var(--pm-text-light);
}
</style>
