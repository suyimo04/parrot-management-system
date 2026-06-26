<template>
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
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { uploadFile } from '../api/upload'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const uploadImage = async ({ file }) => {
  const formData = new FormData()
  formData.append('file', file)
  const data = await uploadFile(formData)
  const url = data?.accessUrl || data?.url || data
  emit('update:modelValue', url)
  ElMessage.success('图片上传成功')
}
</script>

<style scoped>
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
</style>
