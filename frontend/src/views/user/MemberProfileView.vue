<script setup>
import { onMounted, ref } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getMe, updateMe } from '../../api/auth'
import { errorMessage } from '../../api/client'
import './member-profile.css'

const profile = ref({ loginId: '', name: '', email: '' })
const editing = ref({ name: false, email: false })
const savedMessage = ref('')
const loading = ref(false)
const saving = ref(false)

async function loadProfile() {
  loading.value = true
  try {
    const response = await getMe()
    const data = response.data?.data ?? response.data
    profile.value = { loginId: data?.loginId ?? '', name: data?.name ?? '', email: data?.email ?? '' }
  } catch (error) { savedMessage.value = errorMessage(error, '회원정보를 불러오지 못했습니다.') }
  finally { loading.value = false }
}
onMounted(loadProfile)

function toggleEdit(field) {
  editing.value[field] = !editing.value[field]
  savedMessage.value = ''
}

async function saveProfile() {
  saving.value = true
  try {
    await updateMe({ name: profile.value.name, email: profile.value.email })
    await loadProfile()
    editing.value = { name: false, email: false }
    savedMessage.value = '회원정보가 저장되었습니다.'
  } catch (error) { savedMessage.value = errorMessage(error, '회원정보를 저장하지 못했습니다.') }
  finally { saving.value = false }
}
</script>

<template>
  <div class="member-profile-page">
    <PageHeader role="member" :user-name="profile.name" />

    <main class="member-profile-main">
      <section class="member-profile-hero" aria-labelledby="member-profile-page-title">
        <div class="member-profile-breadcrumb">HOME <span>›</span> 마이페이지</div>
        <h1 id="member-profile-page-title">마이페이지</h1>
        <p>언제나 안전하고 편리한 교통서비스를 위해<br />최선을 다하겠습니다.</p>
      </section>

      <div class="member-profile-layout">
        <aside class="member-profile-sidebar" aria-label="마이페이지 메뉴">
          <a class="member-profile-sidebar__link is-active" href="/mypage" aria-current="page">회원정보</a>
          <a class="member-profile-sidebar__link" href="/mypage/withdraw">회원 탈퇴</a>
        </aside>

        <section class="member-profile-panel" aria-labelledby="member-profile-heading">
          <h2 id="member-profile-heading">회원정보</h2>
          <form class="member-profile-form" @submit.prevent="saveProfile">
            <div class="member-profile-row">
              <strong>아이디 :</strong>
              <div class="member-profile-value">{{ profile.loginId }}</div>
            </div>
            <div class="member-profile-row">
              <strong>전화번호 :</strong>
              <div class="member-profile-value">-</div>
            </div>
            <div class="member-profile-row">
              <label for="profile-name">이름:</label>
              <div class="member-profile-value">
                <input v-if="editing.name" id="profile-name" v-model.trim="profile.name" required />
                <span v-else>{{ profile.name }}</span>
                <button class="member-profile-change" type="button" @click="toggleEdit('name')">{{ editing.name ? '취소' : '변경' }}</button>
              </div>
            </div>
            <div class="member-profile-row">
              <label for="profile-email">이메일:</label>
              <div class="member-profile-value">
                <input v-if="editing.email" id="profile-email" v-model.trim="profile.email" type="email" required />
                <span v-else>{{ profile.email }}</span>
                <button class="member-profile-change" type="button" @click="toggleEdit('email')">{{ editing.email ? '취소' : '변경' }}</button>
              </div>
            </div>
            <p v-if="loading" role="status">불러오는 중...</p>
            <p v-if="savedMessage" class="member-profile-message" role="status">{{ savedMessage }}</p>
            <button class="member-profile-save" type="submit" :disabled="saving">저 장</button>
          </form>
        </section>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
