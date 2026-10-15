<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getQna, createQna, updateQna } from './qnaMockData'
import { inquiryToApi } from '../../services/apiMappers'
import { apiError } from '../../services/backendApi'
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
onMounted(async () => {
  if (!isEdit.value) return
  if (!route.query.id) return
  try { fillForm(await getQna(route.query.id)) } catch (error) { window.alert(apiError(error)) }
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
    router.push({ path: '/qna/detail', query: { id: route.query.id } })
    return
  }
  router.push('/qna?mine=true')
}

async function saveQuestion() {
  if (!title.value.trim() || !content.value.trim()) { window.alert('제목과 내용을 입력해 주세요.'); return }
  const fields = { title: title.value.trim(), inquiryType: inquiryToApi[category.value], content: content.value.trim() }
  try {
    const result = isEdit.value ? await updateQna(route.query.id, fields, [selectedFile.value]) : await createQna(fields, [selectedFile.value])
    const qnaId = result?.qnaId ?? route.query.id
    router.push(isEdit.value ? { path: '/qna/detail', query: { id: qnaId } } : '/qna?mine=true')
  } catch (error) { window.alert(apiError(error)) }
}
</script>

<template>
  <div class="member-qna-form-page">
    <PageHeader />

    <main class="member-qna-form-main">
      <PageBanner title="Q&amp;A" description="궁금하신 내용을 확인하실 수 있습니다." :stage="isEdit ? '수정' : '등록'" />

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
