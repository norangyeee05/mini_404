<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { deleteResource as requestDeleteResource, downloadResourceFile, getResource } from '../../api/resources'
import { errorMessage } from '../../api/client'
import { normalizeResource } from '../../api/normalize'
import './admin-resource-detail.css'

const props = defineProps({
  role: { type: String, default: 'admin' },
})

const router = useRouter()
const route = useRoute()
const message = ref('')
const loading = ref(true)
const resource = ref({ id: null, title: '', writer: '', views: 0, date: '', content: '', fileName: '', fileSize: '', fileId: null })

onMounted(async () => {
  const id = route.params.resourceId || route.query.id
  if (!id) { message.value = '자료 번호가 없습니다.'; loading.value = false; return }
  try { resource.value = normalizeResource(await getResource(id)) }
  catch (error) { message.value = errorMessage(error, '자료를 불러오지 못했습니다.') }
  finally { loading.value = false }
})

function goToList() {
  if (props.role === 'admin') router.push('/admin/resources')
  else router.push('/resources')
}

async function downloadFile() {
  if (!resource.value.fileId) { message.value = '다운로드할 파일 정보가 없습니다.'; return }
  try {
    const response = await downloadResourceFile(resource.value.id, resource.value.fileId)
    const url = URL.createObjectURL(response.data)
    const anchor = document.createElement('a')
    anchor.href = url
    anchor.download = resource.value.fileName || 'download'
    anchor.click()
    URL.revokeObjectURL(url)
  } catch (error) { message.value = errorMessage(error, '파일을 다운로드하지 못했습니다.') }
}

async function downloadAttachment(file) {
  if (!file?.fileId) { message.value = '다운로드할 파일 정보가 없습니다.'; return }
  try {
    const response = await downloadResourceFile(resource.value.id, file.fileId)
    const url = URL.createObjectURL(response.data)
    const anchor = document.createElement('a')
    anchor.href = url
    anchor.download = file.fileName || 'download'
    anchor.click()
    URL.revokeObjectURL(url)
  } catch (error) { message.value = errorMessage(error, '파일을 다운로드하지 못했습니다.') }
}

async function deleteResource() {
  if (!window.confirm('자료를 삭제하시겠습니까?')) return
  try { await requestDeleteResource(resource.value.id); router.push('/admin/resources') }
  catch (error) { message.value = errorMessage(error, '자료를 삭제하지 못했습니다.') }
}
</script>

<template>
  <div class="admin-resource-detail-page">
    <PageHeader :role="props.role" />

    <main class="admin-resource-detail-main">
      <section class="admin-resource-detail-hero" aria-labelledby="admin-resource-detail-page-title">
        <div class="admin-resource-detail-breadcrumb">HOME <span>›</span> 자료실 <span>›</span> 상세</div>
        <h1 id="admin-resource-detail-page-title">자료실</h1>
        <p>교통서비스 관련 자료를 확인하실 수 있습니다.</p>
      </section>

      <article class="admin-resource-detail-card">
          <p v-if="loading" role="status">불러오는 중...</p>
          <h2>{{ resource.title }}</h2>
        <dl class="admin-resource-detail-meta">
          <div><dt>작성자</dt><dd>{{ resource.writer }}</dd></div>
          <div><dt>조회수</dt><dd>{{ resource.views }}</dd></div>
          <div><dt>등록일</dt><dd>{{ String(resource.date).split(' ')[0] }}</dd></div>
        </dl>

        <p v-if="props.role === 'member'" class="admin-resource-detail-content">{{ resource.content }}</p>

        <section class="admin-resource-detail-attachment" aria-labelledby="resource-attachment-title">
          <h3 id="resource-attachment-title">첨부파일</h3>
          <div v-for="file in resource.files" :key="file.fileId" class="admin-resource-detail-file-row">
            <span class="admin-resource-detail-file-name">{{ file.fileName }} <span v-if="file.fileSize">({{ file.fileSize }})</span></span>
            <button type="button" class="admin-resource-detail-download" @click="downloadAttachment(file)">↓ 다운로드</button>
          </div>
          <div v-if="!resource.files?.length && resource.fileId" class="admin-resource-detail-file-row">
            <span class="admin-resource-detail-file-name">{{ resource.fileName }}</span>
            <button type="button" class="admin-resource-detail-download" @click="downloadFile">↓ 다운로드</button>
          </div>
          <p v-if="!resource.files?.length && !resource.fileId">첨부파일이 없습니다.</p>
        </section>
      </article>

      <p v-if="message" class="admin-resource-detail-message" role="status">{{ message }}</p>
      <div class="admin-resource-detail-actions">
        <button class="admin-resource-detail-button" type="button" @click="goToList">목록</button>
        <div v-if="props.role === 'admin'">
          <button class="admin-resource-detail-button" type="button" @click="router.push({ path: '/admin/resources/new', query: { id: resource.id } })">수정</button>
          <button class="admin-resource-detail-button" type="button" @click="deleteResource">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
