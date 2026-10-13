<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { createQna, getQna, updateQna } from '../../api/qna'
import { errorMessage } from '../../api/client'
import { normalizeQna } from '../../api/normalize'
import './member-qna-form.css'

const props = defineProps({ mode: { type: String, default: 'create' } })
const route = useRoute()
const router = useRouter()
const title = ref('')
const content = ref('')
const category = ref('운행')
const selectedFiles = ref([])
const titleLength = computed(() => title.value.length)
const contentLength = computed(() => content.value.length)
const isEdit = computed(() => props.mode === 'edit')
const qnaId = computed(() => route.params.qnaId || route.query.id)
const message = ref('')
const saving = ref(false)

onMounted(async () => {
  if (!isEdit.value || !qnaId.value) return
  try { fillForm(normalizeQna(await getQna(qnaId.value))) }
  catch (error) { message.value = errorMessage(error, 'Q&A를 불러오지 못했습니다.') }
})

function fillForm(qna) {
  title.value = qna.title || ''
  content.value = qna.content || ''
  category.value = qna.category || '운행'
}

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
  if (isEdit.value) {
    router.push(`/qna/${qnaId.value}`)
    return
  }
  router.push('/qna?mine=true')
}

async function saveQuestion() {
  message.value = ''
  saving.value = true
  try {
    const inquiryType = ({ '운행': 'OPERATION', '시설': 'FACILITY', '기타': 'ETC' })[category.value]
    if (!inquiryType) throw new Error('명세에 정의되지 않은 문의 유형입니다.')
    let body = { title: title.value, inquiryType, content: content.value }
    if (selectedFiles.value.length) {
      const formData = new FormData()
      formData.append('title', title.value)
      formData.append('inquiryType', inquiryType)
      formData.append('content', content.value)
      selectedFiles.value.forEach((file) => formData.append('files[]', file))
      body = formData
    }
    if (isEdit.value) {
      if (!qnaId.value) throw new Error('수정할 Q&A 번호가 없습니다.')
      await updateQna(qnaId.value, body)
      await router.push(`/qna/${qnaId.value}`)
    } else {
      await createQna(body)
      await router.push('/qna/my')
    }
  } catch (error) { message.value = errorMessage(error, 'Q&A를 저장하지 못했습니다.') }
  finally { saving.value = false }
}
</script>

<template>
  <div class="member-qna-form-page">
    <PageHeader role="member" />

    <main class="member-qna-form-main">
      <section class="member-qna-form-hero">
        <div class="member-qna-form-breadcrumb">HOME <span>›</span> Q&amp;A <span>›</span> {{ isEdit ? '수정' : '등록' }}</div>
        <h1>Q&amp;A</h1>
        <p>궁금하신 내용을 확인하실 수 있습니다.</p>
      </section>

      <form class="member-qna-form-card" @submit.prevent="saveQuestion">
        <p v-if="message" role="alert">{{ message }}</p>
        <div class="member-qna-form-title-row">
          <label for="qna-title">제목 <span>*</span></label>
          <div class="member-qna-form-selects">
            <label class="visually-hidden" for="qna-category">문의 유형</label>
            <select id="qna-category" v-model="category"><option>운행</option><option>시설</option><option>기타</option></select>
          </div>
        </div>
        <input id="qna-title" v-model="title" class="member-qna-form-title-input" maxlength="200" placeholder="제목을 입력하세요." required />
        <div class="member-qna-form-count">{{ titleLength }}/200자</div>

        <label class="member-qna-form-content-label" for="qna-content">내용 <span>*</span></label>
        <textarea id="qna-content" v-model="content" maxlength="200" placeholder="질문 내용을 입력해주세요." required></textarea>
        <div class="member-qna-form-count">{{ contentLength }}/200자</div>

        <label class="member-qna-form-file-label" for="qna-file">첨부파일(선택사항)</label>
        <label class="member-qna-form-file-control" for="qna-file">
          <span>{{ selectedFiles.map((file) => file.name).join(', ') || '파일을 첨부해주세요. (파일당 10MB 이하)' }}</span>
          <input id="qna-file" type="file" multiple @change="handleFileChange" />
        </label>

        <div class="member-qna-form-actions">
          <button type="button" class="member-qna-form-button" @click="goBack">목록</button>
          <div>
            <button type="button" class="member-qna-form-button" @click="goBack">취소</button>
            <button type="submit" class="member-qna-form-button" :disabled="saving">{{ isEdit ? '수정' : '등록' }}</button>
          </div>
        </div>
      </form>
    </main>

    <PageFooter />
  </div>
</template>
