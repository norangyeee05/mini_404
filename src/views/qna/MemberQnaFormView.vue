<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getQnas, saveQnas } from './qnaMockData'
import './member-qna-form.css'

const props = defineProps({ mode: { type: String, default: 'create' } })
const route = useRoute()
const router = useRouter()
const title = ref('')
const content = ref('')
const category = ref('운행')
const visibility = ref('전체')
const selectedFile = ref(null)
const titleLength = computed(() => title.value.length)
const contentLength = computed(() => content.value.length)
const isEdit = computed(() => props.mode === 'edit')
const isSample = computed(() => route.query.sample === 'true')

onMounted(() => {
  if (!isEdit.value) return

  if (isSample.value) {
    const savedSample = localStorage.getItem('memberQnaDetail')
    const qna = savedSample ? JSON.parse(savedSample) : null
    if (qna) fillForm(qna)
    return
  }

  const qna = getQnas().find((item) => String(item.id) === String(route.query.id))
  if (qna) fillForm(qna)
})

function fillForm(qna) {
  title.value = qna.title || ''
  content.value = qna.content || ''
  category.value = qna.category || '운행'
  visibility.value = qna.visibility || '전체'
}

function handleFileChange(event) {
  const file = event.target.files?.[0]
  if (file && file.size > 10 * 1024 * 1024) {
    window.alert('첨부파일은 10MB 이하만 선택할 수 있습니다.')
    event.target.value = ''
    selectedFile.value = null
    return
  }
  selectedFile.value = file || null
}

function goBack() {
  if (isEdit.value) {
    const query = isSample.value ? { sample: 'true' } : { id: route.query.id }
    router.push({ path: '/qna/detail', query })
    return
  }
  router.push('/qna?mine=true')
}

function saveQuestion() {
  const now = new Date()
  const date = `${now.getFullYear()}.${String(now.getMonth() + 1).padStart(2, '0')}.${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`

  if (isSample.value) {
    const savedSample = localStorage.getItem('memberQnaDetail')
    const oldQna = savedSample ? JSON.parse(savedSample) : {}
    localStorage.setItem('memberQnaDetail', JSON.stringify({ ...oldQna, title: title.value, content: content.value, category: category.value, visibility: visibility.value, date: date.split(' ')[0], attachment: selectedFile.value?.name || oldQna.attachment || '' }))
    router.push('/qna/detail?sample=true')
    return
  }

  const qnas = getQnas()
  if (isEdit.value) {
    const updatedQnas = qnas.map((qna) => String(qna.id) === String(route.query.id)
      ? { ...qna, title: title.value, content: content.value, category: category.value, visibility: visibility.value, attachment: selectedFile.value?.name || qna.attachment || '' }
      : qna)
    saveQnas(updatedQnas)
    router.push({ path: '/qna/detail', query: { id: route.query.id } })
    return
  }

  const nextId = Math.max(0, ...qnas.map((qna) => Number(qna.id))) + 1
  saveQnas([{ id: nextId, title: title.value, content: content.value, category: category.value, visibility: visibility.value, writer: '홍길동', status: '답변대기', date, views: 0, answer: '', attachment: selectedFile.value?.name || '' }, ...qnas])
  router.push('/qna?mine=true')
}
</script>

<template>
  <div class="member-qna-form-page">
    <PageHeader role="member" user-name="홍길동" />

    <main class="member-qna-form-main">
      <section class="member-qna-form-hero">
        <div class="member-qna-form-breadcrumb">HOME <span>›</span> Q&amp;A <span>›</span> {{ isEdit ? '수정' : '등록' }}</div>
        <h1>Q&amp;A</h1>
        <p>궁금하신 내용을 확인하실 수 있습니다.</p>
      </section>

      <form class="member-qna-form-card" @submit.prevent="saveQuestion">
        <div class="member-qna-form-title-row">
          <label for="qna-title">제목 <span>*</span></label>
          <div class="member-qna-form-selects">
            <label class="visually-hidden" for="qna-visibility">공개 여부</label>
            <select id="qna-visibility" v-model="visibility"><option>전체</option><option>비공개</option></select>
            <label class="visually-hidden" for="qna-category">문의 유형</label>
            <select id="qna-category" v-model="category"><option>운행</option><option>시설</option><option>기타</option><option>자료실</option></select>
          </div>
        </div>
        <input id="qna-title" v-model="title" class="member-qna-form-title-input" maxlength="200" placeholder="제목을 입력하세요." required />
        <div class="member-qna-form-count">{{ titleLength }}/200자</div>

        <label class="member-qna-form-content-label" for="qna-content">내용 <span>*</span></label>
        <textarea id="qna-content" v-model="content" maxlength="200" placeholder="질문 내용을 입력해주세요." required></textarea>
        <div class="member-qna-form-count">{{ contentLength }}/200자</div>

        <label class="member-qna-form-file-label" for="qna-file">첨부파일(선택사항)</label>
        <label class="member-qna-form-file-control" for="qna-file">
          <span>{{ selectedFile?.name || '파일을 첨부해주세요. (10MB 이하)' }}</span>
          <input id="qna-file" type="file" @change="handleFileChange" />
        </label>

        <div class="member-qna-form-actions">
          <button type="button" class="member-qna-form-button" @click="goBack">목록</button>
          <div>
            <button type="button" class="member-qna-form-button" @click="goBack">취소</button>
            <button type="submit" class="member-qna-form-button">{{ isEdit ? '수정' : '등록' }}</button>
          </div>
        </div>
      </form>
    </main>

    <PageFooter />
  </div>
</template>
