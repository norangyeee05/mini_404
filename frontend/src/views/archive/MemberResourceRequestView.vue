<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { createResourceRequest } from '../../api/resources'
import { errorMessage } from '../../api/client'
import './admin-resource-create.css'

const router = useRouter()
const title = ref('')
const content = ref('')
const selectedFiles = ref([])
const message = ref('')
const saving = ref(false)

function handleFileChange(event) {
  const files = Array.from(event.target.files || [])
  if (files.some((file) => file.size > 10 * 1024 * 1024)) {
    window.alert('첨부파일은 파일당 10MB 이하만 선택할 수 있습니다.')
    event.target.value = ''
    selectedFiles.value = []
    return
  }
  selectedFiles.value = files
}

async function submitRequest() {
  message.value = ''
  saving.value = true
  const body = new FormData()
  body.append('title', title.value)
  body.append('content', content.value)
  selectedFiles.value.forEach((file) => body.append('files[]', file))
  try {
    await createResourceRequest(body)
    await router.push('/resources')
  } catch (error) { message.value = errorMessage(error, '자료실 추가 요청을 등록하지 못했습니다.') }
  finally { saving.value = false }
}
</script>

<template>
  <div class="admin-resource-create-page">
    <PageHeader role="member" />
    <main class="admin-resource-create-main">
      <section class="admin-resource-create-hero">
        <div class="admin-resource-create-breadcrumb">HOME <span>›</span> 자료실 <span>›</span> 추가 요청</div>
        <h1>자료실</h1>
        <p>자료실에 필요한 자료를 요청하실 수 있습니다.</p>
      </section>
      <form class="admin-resource-create-form" @submit.prevent="submitRequest">
        <p v-if="message" role="alert">{{ message }}</p>
        <label class="admin-resource-create-label" for="resource-request-title">제목 <span>*</span></label>
        <input id="resource-request-title" v-model="title" maxlength="200" placeholder="제목을 입력하세요." required />
        <div class="admin-resource-create-count">{{ title.length }}/200자</div>
        <label class="admin-resource-create-label admin-resource-create-content-label" for="resource-request-content">내용 <span>*</span></label>
        <textarea id="resource-request-content" v-model="content" maxlength="200" placeholder="요청 내용을 입력해주세요." required></textarea>
        <div class="admin-resource-create-count">{{ content.length }}/200자</div>
        <label class="admin-resource-create-file-title" for="resource-request-file">첨부파일(선택사항)</label>
        <label class="admin-resource-create-file" for="resource-request-file">
          <span>{{ selectedFiles.map((file) => file.name).join(', ') || '파일을 첨부해주세요. (파일당 10MB 이하)' }}</span>
          <input id="resource-request-file" type="file" multiple @change="handleFileChange" />
        </label>
        <div class="admin-resource-create-actions">
          <button class="admin-resource-create-button" type="button" @click="router.push('/resources')">목록</button>
          <div>
            <button class="admin-resource-create-button" type="button" @click="router.push('/resources')">취소</button>
            <button class="admin-resource-create-button" type="submit" :disabled="saving">요청</button>
          </div>
        </div>
      </form>
    </main>
    <PageFooter />
  </div>
</template>
