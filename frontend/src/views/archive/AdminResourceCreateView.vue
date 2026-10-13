<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { createResource, getResource, updateResource } from '../../api/resources'
import { errorMessage } from '../../api/client'
import { normalizeResource } from '../../api/normalize'
import './admin-resource-create.css'

const router = useRouter()
const route = useRoute()
const title = ref('')
const content = ref('')
const selectedFiles = ref([])
const savedFileName = ref('')
const resourceId = computed(() => route.params.resourceId || route.query.id)
const isEdit = computed(() => Boolean(resourceId.value))
const titleLength = computed(() => title.value.length)
const contentLength = computed(() => content.value.length)
const message = ref('')
const saving = ref(false)

onMounted(async () => {
  if (!isEdit.value) return
  try {
    const resource = normalizeResource(await getResource(resourceId.value))
    title.value = resource.title
    content.value = resource.content
    savedFileName.value = resource.files?.map((file) => file.fileName).join(', ') || resource.fileName || ''
  } catch (error) { message.value = errorMessage(error, '자료를 불러오지 못했습니다.') }
})

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

function goBack() {
  router.push('/admin/resources')
}

async function registerResource() {
  message.value = ''
  saving.value = true
  const body = new FormData()
  body.append('title', title.value)
  body.append('content', content.value)
  selectedFiles.value.forEach((file) => body.append('files[]', file))
  try {
    if (isEdit.value) await updateResource(resourceId.value, body)
    else await createResource(body)
    router.push('/admin/resources')
  } catch (error) { message.value = errorMessage(error, '자료를 저장하지 못했습니다.') }
  finally { saving.value = false }
}
</script>

<template>
  <div class="admin-resource-create-page">
    <PageHeader role="admin" />

    <main class="admin-resource-create-main">
      <section class="admin-resource-create-hero" aria-labelledby="admin-resource-create-title">
        <div class="admin-resource-create-breadcrumb">HOME <span>›</span> 자료실 <span>›</span> {{ isEdit ? '수정' : '등록' }}</div>
        <h1 id="admin-resource-create-title">자료실</h1>
        <p>교통서비스 관련 자료를 확인하실 수 있습니다.</p>
      </section>

      <form class="admin-resource-create-form" @submit.prevent="registerResource">
        <p v-if="message" role="alert">{{ message }}</p>
        <label class="admin-resource-create-label" for="resource-title">제목 <span>*</span></label>
        <input id="resource-title" v-model="title" maxlength="200" placeholder="제목을 입력하세요." required />
        <div class="admin-resource-create-count">{{ titleLength }}/200자</div>

        <label class="admin-resource-create-label admin-resource-create-content-label" for="resource-content">내용 <span>*</span></label>
        <textarea id="resource-content" v-model="content" maxlength="200" placeholder="자료 내용을 입력해주세요." required></textarea>
        <div class="admin-resource-create-count">{{ contentLength }}/200자</div>

        <label class="admin-resource-create-file-title" for="resource-file">첨부파일(선택사항)</label>
        <label class="admin-resource-create-file" for="resource-file">
          <span>{{ selectedFiles.map((file) => file.name).join(', ') || savedFileName || '파일을 첨부해주세요. (파일당 10MB 이하)' }}</span>
          <input id="resource-file" type="file" multiple @change="handleFileChange" />
        </label>

        <div class="admin-resource-create-actions">
          <button class="admin-resource-create-button" type="button" @click="goBack">목록</button>
          <div>
            <button class="admin-resource-create-button" type="button" @click="goBack">취소</button>
            <button class="admin-resource-create-button" type="submit" :disabled="saving">{{ isEdit ? '수정' : '등록' }}</button>
          </div>
        </div>
      </form>
    </main>

    <PageFooter />
  </div>
</template>
